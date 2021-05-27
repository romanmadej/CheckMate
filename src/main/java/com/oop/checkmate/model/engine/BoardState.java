package com.oop.checkmate.model.engine;

import static com.oop.checkmate.Constants.*;
import static com.oop.checkmate.Constants.Color.BLACK;
import static com.oop.checkmate.Constants.Color.WHITE;
import static com.oop.checkmate.Constants.PieceType.*;
import static com.oop.checkmate.model.Piece.*;
import static com.oop.checkmate.model.engine.BitboardUtils.*;
import static com.oop.checkmate.model.engine.Bitboards.*;
import static com.oop.checkmate.model.engine.EngineConstants.*;
import static com.oop.checkmate.model.engine.EngineConstants.MoveType.*;
import static com.oop.checkmate.model.engine.EngineConstants.Square.*;

import java.util.*;

import com.oop.checkmate.model.Piece;
import com.oop.checkmate.model.Position;

public class BoardState {
	Color sideToMove;
	long[] byTypeBB;
	long[] byColorBB;
	Piece[] board;
	int epSquare;
	byte castlingRights;

	StateInfo prev;
	Move lastMove;
	Piece capturedPiece;
	long checkers; // opposing color pieces giving check
	static final List<Move> EMPTY_LIST = new ArrayList<>();

	static class StateInfo {
		int epSquare;
		byte castlingRights;
		long checkers;
		Move prevMove;
		Piece prevCaptured;
		StateInfo prevSt;

		StateInfo(int epSquare, byte castlingRights, long checkers, Move prevMove, Piece prevCaptured, StateInfo prevSt) {
			this.epSquare = epSquare;
			this.castlingRights = castlingRights;
			this.checkers = checkers;
			this.prevMove = prevMove;
			this.prevCaptured = prevCaptured;
			this.prevSt = prevSt;
		}
	}

	// starting position constructor
	public BoardState() {
		this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
	}

	public BoardState(String fenString) {
		byTypeBB = new long[PIECE_TYPE_N + 1];
		byColorBB = new long[COLOR_N];
		board = new Piece[64];
		Arrays.fill(board, NO_PIECE);

		fenString = fenString.trim();
		int i = 0, squareId = 0, n = fenString.length();

		for (; i < n && squareId < 64; i++) {
			int sq = 8 * (7 - squareId / 8) + squareId % 8;
			char c = fenString.charAt(i);
			if (c == '/') continue;
			if (Character.isDigit(c)) {
				squareId += Character.digit(c, 10);
				continue;
			}
			Piece pc = Piece.getPiece(c);
			board[sq] = pc;
			byTypeBB[pc.pieceType.id] ^= squareBB(sq);
			byTypeBB[ALL_PIECES] ^= squareBB(sq);
			byColorBB[pc.color.id] ^= squareBB(sq);
			squareId++;
		}
		while (Character.isWhitespace(fenString.charAt(i))) i++;
		sideToMove = fenString.charAt(i++) == 'w' ? WHITE : BLACK;
		while (Character.isWhitespace(fenString.charAt(i))) i++;

		castlingRights = 0;
		while (!Character.isWhitespace(fenString.charAt(i)) && fenString.charAt(i) != '-') {
			char c = fenString.charAt(i);
			switch (c) {
				case 'k':
					castlingRights |= BLACK_OO;
					break;
				case 'q':
					castlingRights |= BLACK_OOO;
					break;
				case 'K':
					castlingRights |= WHITE_OO;
					break;
				case 'Q':
					castlingRights |= WHITE_OOO;
					break;
				default:
					throw new IllegalStateException("Illegal castling rights character");
			}
			i++;
		}

		while (Character.isWhitespace(fenString.charAt(i))) i++;
		if (fenString.charAt(i) != '-') {
			String sName = fenString.substring(i, i + 2).toUpperCase(Locale.ROOT);
			Square s = Square.valueOf(sName);
			epSquare = s.id;
		} else epSquare = -1;
		//halfmove and fullmove clocks ignored for now

		checkers = attackersTo(getKingSquare(sideToMove.id), pieces()) & pieces(sideToMove.id ^ 1);
		prev = null;
	}


	public Color getSideToMove() {
		return sideToMove;
	}

	public void print() {
		for (PieceType pt : PieceType.values()) {
			System.out.println(pt.name());
			printBB(byTypeBB[pt.id]);
		}
		for (Color c : Color.values()) {
			System.out.println(c.name());
			printBB(byColorBB[c.id]);
		}
		for (int i = 8; i > 0; i--) {
			for (int j = 8; j > 0; j--) {
				System.out.print(board[8 * i - j].name().concat("         ").substring(0, 10) + "  ");
			}
			System.out.println();
		}
		System.out.println("CHECKERS");
		printBB(checkers);
		System.out.println("SIDE_TO_MOVE\n" + sideToMove.name());
		System.out.println("PINNED");
		printBB(pinned(getKingSquare(sideToMove.id), sideToMove));
		System.out.println("CASTLING RIGHTS");
		printBB(castlingRights);
	}

	public List<Move> getLegalMoves(Position position) {
		List<Move> legalFrom = new ArrayList<>();
		List<Move> legal = generateLegalMoves();
		for (Move m : legal)
			if (m.getFrom() == position.getSquareId())
				legalFrom.add(m);
		return legalFrom;
	}

	public List<Move> generateLegalMoves() {

		List<Move> legal = new ArrayList<>();

		int us = sideToMove.id;

		if (checkers != 0) {
			return generateEvasions();
		}

		long pieces = pieces(us);
		while (pieces != 0) {
			int from = get_lsb(pieces);
			long lsbBB = 1L << from;
			List<Move> pseudoMoves = generatePseudoMoves(from, board[from], pieces(us), pieces(us ^ 1), epSquare);
			for (Move move : pseudoMoves) {
				if (isLegal(move))
					if (isPawnPromotion(move)) {
						int pFrom = move.getFrom(), pTo = move.getTo();
						boolean capture = move.isCapture();
						legal.add(new Move(pFrom, pTo, capture ? KNIGHT_PROMO_CAPTURE.id : KNIGHT_PROMO.id));
						legal.add(new Move(pFrom, pTo, capture ? BISHOP_PROMO_CAPTURE.id : BISHOP_PROMO.id));
						legal.add(new Move(pFrom, pTo, capture ? ROOK_PROMO_CAPTURE.id : ROOK_PROMO.id));
						legal.add(new Move(pFrom, pTo, capture ? QUEEN_PROMO_CAPTURE.id : QUEEN_PROMO.id));
					} else legal.add(move);
			}
			if (board[from].pieceType == KING) {
				if ((castling(sideToMove, KING) & castlingRights) != 0 && (attackersToLine(kingPathBB(sideToMove, KING))
						& pieces(sideToMove.inverse())) == 0 && (castlingLineBB(sideToMove, KING) & pieces()) == pieces(sideToMove, KING)) {
					int to = sideToMove == WHITE ? G1.id : G8.id;
					legal.add(new Move(from, to, KINGSIDE_CASTLE));
				}

				if ((castling(sideToMove, QUEEN) & castlingRights) != 0 && (attackersToLine(kingPathBB(sideToMove, QUEEN)) & pieces(sideToMove.inverse())) == 0
						&& (castlingLineBB(sideToMove, QUEEN) & pieces()) == pieces(sideToMove, KING)) {
					int to = sideToMove == WHITE ? C1.id : C8.id;
					legal.add(new Move(from, to, QUEENSIDE_CASTLE));
				}
			}

			pieces ^= lsbBB;
		}
		return legal;
	}

	private boolean isLegal(Move move) {
		int from = move.getFrom();
		int to = move.getTo();
		long fromBB = 1L << from;
		long toBB = 1L << to;

		if (fromBB == pieces(sideToMove.id, KING)) {
			return (attackersTo(to, pieces() ^ fromBB) & pieces(sideToMove.id ^ 1)) == 0;
		}
		if (isDoublePawnPush(move) && ((betweenBB(from, to) | toBB) & pieces()) != 0) return false;
		if (move.isEpCapture()) {
			int captured = sideToMove == WHITE ? to - 8 : to + 8;
			long fromToBB = fromBB | toBB;
			if ((attackersTo(getKingSquare(sideToMove.id), pieces() ^ (squareBB(captured) | fromToBB)) & (pieces(sideToMove.id ^ 1) ^ squareBB(captured))) != 0)
				return false;
		}

		int kingSquare = getKingSquare(sideToMove.id);
		return ((pinned(kingSquare, sideToMove) & fromBB) == 0) || (lineBB(kingSquare, from) & toBB) != 0;
	}

	public List<Move> generateEvasions() {
		int us = sideToMove.id;
		if (checkers == 0)
			throw new IllegalStateException("Evasions can be generated only when sideToMove's king is in check");

		List<Move> evasions = new ArrayList<>();

		int kingSq = getKingSquare(us);
		long quietsKing = pseudoMovesBitboard(QUIET, sideToMove, KING, kingSq, pieces(us), pieces(us ^ 1));
		long capturesKing = pseudoMovesBitboard(CAPTURE, sideToMove, KING, kingSq, pieces(us), pieces(us ^ 1));
		evasions.addAll(BBtoLegalMoves(kingSq, quietsKing, QUIET));
		evasions.addAll(BBtoLegalMoves(kingSq, capturesKing, CAPTURE));
		// only king can move out of check
		if (more_than_one_bit(checkers)) {
			return evasions;
		}

		long checkerBB = get_lsbBB(checkers);
		int checkerSquare = get_lsb(checkers);
		long kingSqBB = 1L << kingSq;
		PieceType checkerType = board[checkerSquare].pieceType;
		long betweenBB = (checkerType == QUEEN || checkerType == ROOK || checkerType == BISHOP)
				? betweenBB(kingSqBB, checkerBB)
				: 0;

		// check blocking with other pieces and checker piece captures
		for (PieceType pieceType : PieceType.values()) {
			long piecesBB = pieces(us, pieceType);
			while (piecesBB != 0) {
				int lsb = get_lsb(piecesBB);
				long from = 1L << lsb;

				//king moves already generated
				if (board[lsb].pieceType == KING) {
					piecesBB ^= from;
					continue;
				}
				long movesBB = pseudoMovesBitboard(QUIET, sideToMove, pieceType, lsb, pieces(us), pieces(us ^ 1));
				movesBB &= betweenBB;
				while (movesBB != 0) {
					int to = get_lsb(movesBB);
					Move move = new Move(lsb, to, QUIET);
					if (isLegal(move)) {
						if (isPawnPromotion(move)) {
							int pFrom = move.getFrom(), pTo = move.getTo();
							boolean capture = move.isCapture();
							evasions.add(new Move(pFrom, pTo, capture ? KNIGHT_PROMO_CAPTURE.id : KNIGHT_PROMO.id));
							evasions.add(new Move(pFrom, pTo, capture ? BISHOP_PROMO_CAPTURE.id : BISHOP_PROMO.id));
							evasions.add(new Move(pFrom, pTo, capture ? ROOK_PROMO_CAPTURE.id : ROOK_PROMO.id));
							evasions.add(new Move(pFrom, pTo, capture ? QUEEN_PROMO_CAPTURE.id : QUEEN_PROMO.id));
						} else evasions.add(move);

					}
					movesBB ^= 1L << to;
				}

				movesBB = pseudoMovesBitboard(CAPTURE, sideToMove, pieceType, lsb, pieces(us), pieces(us ^ 1));
				while (movesBB != 0) {
					int to = get_lsb(movesBB);
					if (((1L << to) & checkerBB) != 0) {
						Move move = new Move(lsb, to, CAPTURE);
						if (isLegal(move)) {
							if (isPawnPromotion(move)) {
								int pFrom = move.getFrom(), pTo = move.getTo();
								boolean capture = move.isCapture();
								evasions.add(new Move(pFrom, pTo, capture ? KNIGHT_PROMO_CAPTURE.id : KNIGHT_PROMO.id));
								evasions.add(new Move(pFrom, pTo, capture ? BISHOP_PROMO_CAPTURE.id : BISHOP_PROMO.id));
								evasions.add(new Move(pFrom, pTo, capture ? ROOK_PROMO_CAPTURE.id : ROOK_PROMO.id));
								evasions.add(new Move(pFrom, pTo, capture ? QUEEN_PROMO_CAPTURE.id : QUEEN_PROMO.id));
							} else evasions.add(move);
						}
					}
					movesBB ^= 1L << to;
				}

				if (epSquare != -1 && board[lsb].pieceType == PAWN && (pawnAttacks(sideToMove.inverse(), epSquare) & from) != 0 &&
						(((squareBB(epSquare) & betweenBB) != 0) || checkerBB == pawnPush(sideToMove.inverse(), epSquare))) {
					Move move = new Move(lsb, epSquare, EP_CAPTURE);
					if (isLegal(move)) evasions.add(move);
				}

				piecesBB ^= from;
			}
		}
		return evasions;
	}


	public void makeMove(Move move) {
		prev = new StateInfo(epSquare, castlingRights, checkers, lastMove, capturedPiece, prev);
		lastMove = move;

		int from = move.getFrom();
		int to = move.getTo();
		if (move.isCapture() && !move.isEpCapture()) capturedPiece = board[to];
		else if (move.isEpCapture()) capturedPiece = sideToMove == WHITE ? B_PAWN : W_PAWN;

		boolean isDoublePawnPush = isDoublePawnPush(move);

		//utilize castling method to shorten expression
		if (board[from].pieceType == ROOK && (squareBB(from) & (sideToMove == WHITE ? whiteCornersBB : blackCornersBB)) != 0) {
			PieceType side = (squareBB(from) & FileABB) != 0 ? QUEEN : KING;
			castlingRights &= ~castle(sideToMove, side);
		} else if (board[from].pieceType == KING) {
			castlingRights &= ~anyCastling(sideToMove);
		}

		if (move.isCapture() && board[to].pieceType == ROOK) {
			switch (to) {
				case 0:
					castlingRights &= ~WHITE_OOO;
					break;
				case 7:
					castlingRights &= ~WHITE_OO;
					break;
				case 56:
					castlingRights &= ~BLACK_OOO;
					break;
				case 63:
					castlingRights &= ~BLACK_OO;
					break;
			}
		}
		movePiece(move);


		if (move.getMoveType() == KINGSIDE_CASTLE.id || move.getMoveType() == QUEENSIDE_CASTLE.id) {
			int rookFrom = sideToMove == WHITE ? (move.getMoveType() == KINGSIDE_CASTLE.id ? H1.id : A1.id) : (move.getMoveType() == KINGSIDE_CASTLE.id ? H8.id : A8.id);
			int rookTo = sideToMove == WHITE ? (move.getMoveType() == KINGSIDE_CASTLE.id ? F1.id : D1.id) : (move.getMoveType() == KINGSIDE_CASTLE.id ? F8.id : D8.id);
			if (board[rookFrom].pieceType != ROOK || board[rookTo] != NO_PIECE)
				throw new IllegalStateException("castling is not legal");
			//either of kingside and queenside movetypes could be passed
			movePiece(new Move(rookFrom, rookTo, KINGSIDE_CASTLE.id));
		}


		epSquare = -1;
		if (isDoublePawnPush) epSquare = sideToMove == WHITE ? to - 8 : to + 8;


		checkers = attackersTo(getKingSquare(sideToMove.id ^ 1), pieces()) & pieces(sideToMove.id);

		sideToMove = sideToMove.inverse();

		if ((pieces(0) & pieces(1)) != 0) {
			throw new IllegalStateException("Illegal Board State");
		}
	}

	public void undoLastMove() {
		int from = lastMove.getTo(), to = lastMove.getFrom();
		long fromBB = squareBB(from), toBB = squareBB(to), fromToBB = fromBB | toBB;
		if (board[to] != NO_PIECE || board[from] == NO_PIECE) throw new IllegalStateException("lastMove is invalid");

		int movingColor = sideToMove.id ^ 1;

		if (lastMove.isPromotion()) {
			int fPieceType = lastMove.getPromotionPieceType().id;
			byTypeBB[ALL_PIECES] ^= fromToBB;
			byColorBB[movingColor] ^= fromToBB;

			byTypeBB[fPieceType] ^= fromBB;
			byTypeBB[PAWN.id] ^= toBB;

			board[to] = movingColor == WHITE.id ? W_PAWN : B_PAWN;
		} else {
			int fPieceType = board[from].pieceType.id;
			byTypeBB[ALL_PIECES] ^= fromToBB;
			byTypeBB[fPieceType] ^= fromToBB;
			byColorBB[movingColor] ^= fromToBB;
			board[to] = board[from];
		}

		board[from] = NO_PIECE;
		//restore captured piece
		if (lastMove.isCapture() && !lastMove.isEpCapture()) {
			byTypeBB[ALL_PIECES] ^= fromBB;
			byTypeBB[capturedPiece.pieceType.id] ^= fromBB;
			byColorBB[movingColor ^ 1] ^= fromBB;
			board[from] = capturedPiece;
		}
		if (lastMove.isEpCapture()) {
			long capturedBB = pawnPush(sideToMove, from);
			if (board[get_lsb(capturedBB)] != NO_PIECE)
				throw new IllegalStateException("en passant captured square should be empty");
			byTypeBB[ALL_PIECES] ^= capturedBB;
			byTypeBB[capturedPiece.pieceType.id] ^= capturedBB;
			byColorBB[movingColor ^ 1] ^= capturedBB;
			board[get_lsb(capturedBB)] = capturedPiece;
		}

		if (lastMove.isCastling()) {
			int rFrom, rTo;
			if (lastMove.isKingsideCastling()) {
				rFrom = F1.id;
				rTo = H1.id;
			} else {
				rFrom = D1.id;
				rTo = A1.id;
			}
			if (movingColor == BLACK.id) {
				rFrom += 56;
				rTo += 56;
			}
			if (board[rFrom].pieceType != ROOK || board[rTo] != NO_PIECE)
				throw new IllegalStateException("can't undo castling board state is illegal");
			long rFromToBB = squareBB(rFrom) | squareBB(rTo);
			byTypeBB[ALL_PIECES] ^= rFromToBB;
			byTypeBB[ROOK.id] ^= rFromToBB;
			byColorBB[movingColor] ^= rFromToBB;
			board[rTo] = board[rFrom];
			board[rFrom] = NO_PIECE;

		}
		if ((pieces(0) & pieces(1)) != 0) {
			throw new IllegalStateException("Illegal Board State");
		}
		castlingRights = prev.castlingRights;
		epSquare = prev.epSquare;
		checkers = prev.checkers;
		lastMove = prev.prevMove;
		capturedPiece = prev.prevCaptured;
		sideToMove = sideToMove.inverse();
		prev = prev.prevSt;
	}


	private void movePiece(Move move) {
		int from = move.getFrom(), to = move.getTo();
		if (board[from] == NO_PIECE)
			throw new IllegalStateException("from square is empty");
		int us = sideToMove.id;
		if (sideToMove != board[from].color)
			throw new IllegalStateException("Illegal board State");
		if (move.isEpCapture()) {
			if (board[to] != NO_PIECE) throw new IllegalStateException("en passant square is not empty");
			if (sideToMove == WHITE && (squareBB(epSquare) & RANK6BB) == 0 || (sideToMove == BLACK && (squareBB(epSquare) & RANK3BB) == 0))
				throw new IllegalStateException("en passant square can't be on ranks other than 3 or 6");
			long capturedSquareBB = sideToMove == WHITE ? pawnPush(BLACK, epSquare) : pawnPush(WHITE, epSquare);
			int capturedSquare = get_lsb(capturedSquareBB);

			if (board[capturedSquare] == NO_PIECE)
				throw new IllegalStateException("en passant capture square is empty");

			int epPieceType = board[capturedSquare].pieceType.id;
			byTypeBB[ALL_PIECES] ^= capturedSquareBB;
			byTypeBB[epPieceType] ^= capturedSquareBB;
			byColorBB[us ^ 1] ^= capturedSquareBB;
			board[capturedSquare] = NO_PIECE;

		}

		int fPieceType = board[from].pieceType.id;
		long fromBB = 1L << from;
		long toBB = 1L << to;
		long fromTo = fromBB | toBB;

		if (board[to] != NO_PIECE) {
			int tPieceType = board[to].pieceType.id;
			byTypeBB[ALL_PIECES] ^= toBB;
			byTypeBB[tPieceType] ^= toBB;
			byColorBB[us ^ 1] ^= toBB;
		}

		if (move.isPromotion()) {
			PieceType promoType = move.getPromotionPieceType();
			byTypeBB[fPieceType] ^= fromBB;
			byTypeBB[promoType.id] ^= toBB;
			board[to] = Piece.getPiece(sideToMove, promoType);
		} else {
			byTypeBB[fPieceType] ^= fromTo;
			board[to] = board[from];
		}
		byTypeBB[ALL_PIECES] ^= fromTo;
		byColorBB[us] ^= fromTo;

		board[from] = NO_PIECE;
		if ((pieces(0) & squareBB(42)) != 0) {
			System.out.println();
		}
		if ((pieces(0) & pieces(1)) != 0)
			throw new IllegalStateException("Illegal Board State");
	}

	private byte castle(Color color, PieceType side) {
		return (byte) (color == WHITE ? (side == KING ? 1 : 2) : (side == KING ? 4 : 8));
	}

	private boolean isDoublePawnPush(Move move) {
		return board[move.getFrom()].pieceType == PAWN && maxDist(move.getFrom(), move.getTo()) == 2;
	}


	private long pieces(PieceType pieceType) {
		return byTypeBB[pieceType.id];
	}

	private long pieces() {
		return byColorBB[WHITE.id] | byColorBB[BLACK.id];
	}

	private long pieces(int color) {
		return byColorBB[color];
	}

	private long pieces(Color color) {
		return byColorBB[color.id];
	}

	private long pieces(int color, PieceType pieceType) {
		return byColorBB[color] & byTypeBB[pieceType.id];
	}

	private long pieces(Color color, PieceType pieceType) {
		return byColorBB[color.id] & byTypeBB[pieceType.id];
	}

	private int getKingSquare(int color) {
		return get_lsb(pieces(color, KING));
	}


	//squares that can't be attacked when king is castling
	static long kingPathBB(Color us, PieceType side) {
		return us == WHITE ? (side == KING ? betweenBB(D1.id, H1.id) : betweenBB(F1.id, B1.id)) : (side == KING ? betweenBB(D8.id, H8.id) : betweenBB(F8.id, B8.id));
	}

	//squares that can't be occupied when king is castling
	static long castlingLineBB(Color us, PieceType side) {
		return us == WHITE ? (side == KING ? betweenBB(D1.id, H1.id) : betweenBB(F1.id, A1.id)) : (side == KING ? betweenBB(D8.id, H8.id) : betweenBB(F8.id, A8.id));
	}

	//returns a bitboard of all pieces attacking a given line
	private long attackersToLine(long lineBB) {
		long attackers = 0;
		while (lineBB != 0) {
			int square = get_lsb(lineBB);
			attackers |= attackersTo(square, pieces());
			lineBB ^= squareBB(square);
		}
		return attackers;
	}


	private byte castling(Color us, PieceType side) {
		return us == WHITE ? (side == KING ? WHITE_OO : WHITE_OOO) : (side == KING ? BLACK_OO : BLACK_OOO);
	}

	private byte anyCastling(Color us) {
		return (byte) (us == WHITE ? 3 : 12);
	}


	private long attackersTo(int square, long occupiedBB) {
		return pawnAttacks(WHITE, square) & pieces(BLACK.id, PAWN)
				| pawnAttacks(BLACK, square) & pieces(WHITE.id, PAWN) | pseudoAttacks(KING, square) & pieces(KING)
				| pseudoAttacks(KNIGHT, square) & pieces(KNIGHT)
				| slidingPseudoAttacks(ROOK, square, occupiedBB, occupiedBB) & (pieces(ROOK) | pieces(QUEEN))
				| slidingPseudoAttacks(BISHOP, square, occupiedBB, occupiedBB) & (pieces(BISHOP) | pieces(QUEEN));
	}

	// returns a bitboard containing pieces of provided color that if removed expose
	// provided square for an attack
	private long pinned(int square, Color color) {
		int us = color.id;
		long attackers = 0;
		attackers |= pseudoAttacks(ROOK, square) & (pieces(us ^ 1, ROOK) | pieces(us ^ 1, QUEEN));
		attackers |= pseudoAttacks(BISHOP, square) & (pieces(us ^ 1, BISHOP) | pieces(us ^ 1, QUEEN));

		long pinned = 0;
		while (attackers != 0) {
			int attacker = get_lsb(attackers);
			long cut = betweenBB(square, attacker) & pieces();
			if (cut != 0 && !more_than_one_bit(cut))
				pinned |= cut;
			attackers ^= 1L << attacker;
		}
		return pinned;
	}


	private List<Move> BBtoLegalMoves(int from, long BB, MoveType moveType) {
		List<Move> legal = new ArrayList<>();
		while (BB != 0) {
			int lsb = get_lsb(BB);
			Move move = new Move(from, lsb, moveType);
			if (isLegal(move)) legal.add(move);
			BB ^= 1L << lsb;
		}
		return legal;
	}

	private boolean isPawnPromotion(Move move) {
		Piece pc = board[move.getFrom()];
		return pc.pieceType == PAWN && (squareBB(move.getTo()) & (pc.color == WHITE ? RANK8BB : RANK1BB)) != 0;
	}

	public String generateFen() {
		int blankSpaces = 0;
		StringBuilder fen = new StringBuilder();
		for (int rank = 7; rank >= 0; rank--) {
			for (int file = 0; file < 8; file++) {
				Piece piece = board[rank * 8 + file];
				if (piece == NO_PIECE) {
					blankSpaces++;
					continue;
				}
				if (blankSpaces != 0) {
					fen.append(blankSpaces);
					blankSpaces = 0;
				}
				String pieceChar = "";
				if (piece.pieceType == PAWN) {
					pieceChar = "p";
				} else if (piece.pieceType == KNIGHT) {
					pieceChar = "n";
				} else if (piece.pieceType == BISHOP) {
					pieceChar = "b";
				} else if (piece.pieceType == ROOK) {
					pieceChar = "r";
				} else if (piece.pieceType == KING) {
					pieceChar = "k";
				} else if (piece.pieceType == QUEEN) {
					pieceChar = "q";
				}
				if (piece.color == WHITE) {
					pieceChar = pieceChar.toUpperCase();
				}
				fen.append(pieceChar);
			}
			if (blankSpaces != 0) {
				fen.append(blankSpaces);
				blankSpaces = 0;
			}
			if (rank != 0) {
				fen.append("/");
			}
		}
		if (sideToMove == WHITE) {
			fen.append(" w ");
		} else {
			fen.append(" b ");
		}
		boolean castle = false;
		if ((castlingRights & WHITE_OO) == WHITE_OO) {
			fen.append("K");
			castle = true;
		}
		if ((castlingRights & WHITE_OOO) == WHITE_OOO) {
			fen.append("Q");
			castle = true;
		}
		if ((castlingRights & BLACK_OO) == BLACK_OO) {
			fen.append("k");
			castle = true;
		}
		if ((castlingRights & BLACK_OOO) == BLACK_OOO) {
			fen.append("q");
			castle = true;
		}
		if (!castle) {
			fen.append("-");
		}
		fen.append(" - 0 1");
		return fen.toString();
	}

	public Piece getPiece(Position position) {
		return board[position.getSquareId()];
	}

	public Optional<Position> getCheckedKing() {
		if (checkers == 0) {
			return Optional.empty();
		}
		return Optional.of(Position.fromSquareId(getKingSquare(sideToMove.id)));
	}
}
