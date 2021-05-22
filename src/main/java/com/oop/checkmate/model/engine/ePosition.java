package com.oop.checkmate.model.engine;

import static com.oop.checkmate.Constants.*;
import static com.oop.checkmate.Constants.Color.BLACK;
import static com.oop.checkmate.Constants.Color.WHITE;
import static com.oop.checkmate.Constants.PieceType.*;
import static com.oop.checkmate.model.engine.BitboardUtils.*;
import static com.oop.checkmate.model.engine.Bitboards.*;
import static com.oop.checkmate.model.engine.EngineConstants.*;
import static com.oop.checkmate.model.engine.EngineConstants.MoveType.*;
import static com.oop.checkmate.model.engine.EngineConstants.Square.*;
import static com.oop.checkmate.model.engine.EngineConstants.ePiece.*;

import java.util.*;

import com.oop.checkmate.model.Piece;

public class ePosition {
	Color sideToMove;
	long[] byTypeBB;
	long[] byColorBB;
	ePiece[] board;
	int epSquare;
	byte castlingRights;

	ePosition prev;
	long checkers; // opposing color pieces giving check
	List<List<Move>> legalMoves;
	static final List<Move> EMPTY_LIST = new ArrayList<>();

	// starting position constructor
	public ePosition() {
		this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
	}

	private ePosition(ePosition p) {
		sideToMove = p.sideToMove;

		byColorBB = new long[COLOR_N];
		byColorBB[WHITE.id] = p.byColorBB[WHITE.id];
		byColorBB[BLACK.id] = p.byColorBB[BLACK.id];

		byTypeBB = new long[PIECE_TYPE_N + 1];
		System.arraycopy(p.byTypeBB, 0, byTypeBB, 0, PIECE_TYPE_N + 1);

		board = new ePiece[SQUARE_N];
		System.arraycopy(p.board, 0, board, 0, SQUARE_N);

		checkers = p.checkers;
		epSquare = p.epSquare;
		castlingRights = p.castlingRights;
		legalMoves = new ArrayList<>();
		for (int i = 0; i < 64; i++)
			legalMoves.add(new ArrayList<>());
		prev = p;
	}

	public ePosition(String fenString) {
		byTypeBB = new long[PIECE_TYPE_N + 1];
		byColorBB = new long[COLOR_N];
		board = new ePiece[64];
		Arrays.fill(board, NO_PIECE);
//		for(int i=0;i<64;i++) board[i]=NO_PIECE;

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
			ePiece pc = get_ePiece(c);
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
		while (!Character.isWhitespace(fenString.charAt(i))) {
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
		legalMoves = new ArrayList<>();
		for (int j = 0; j < 64; j++)
			legalMoves.add(new ArrayList<>());
		generateLegalMoves();
		prev = null;
	}

	public List<Move> getLegalMoves(int square) {

		if (board[square].color != sideToMove)
			return EMPTY_LIST;
		return legalMoves.get(square);
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
		System.out.println("LEGAL MOVES");
		for (List<Move> moves : legalMoves) {
			for (Move move : moves)
				System.out.println(move);
		}
	}

	private void generateLegalMoves() {

		int us = sideToMove.id;

		if (checkers != 0) {
			generateEvasions();
			return;
		}

		long pieces = pieces(us);
		while (pieces != 0) {
			int from = get_lsb(pieces);
			long lsbBB = 1L << from;
			List<Move> pseudoMoves = generatePseudoMoves(from, new Piece(board[from].pieceType, board[from].color),
					pieces(us), pieces(us ^ 1), epSquare);
			for (Move move : pseudoMoves) {
				if (isLegal(move)) {
					legalMoves.get(move.getFrom()).add(move);
				}
			}
			if (board[from].pieceType == KING) {
				if ((castling(sideToMove, KING) & castlingRights) != 0 && (attackersToLine(kingPathBB(sideToMove, KING))
						& pieces(sideToMove.inverse())) == 0 && (castlingLineBB(sideToMove, KING) & pieces()) == pieces(sideToMove, KING)) {
					int to = sideToMove == WHITE ? G1.id : G8.id;
					legalMoves.get(from).add(new Move(from, to, KINGSIDE_CASTLE));
				}

				if ((castling(sideToMove, QUEEN) & castlingRights) != 0 && (attackersToLine(kingPathBB(sideToMove, QUEEN)) & pieces(sideToMove.inverse())) == 0
						&& (castlingLineBB(sideToMove, QUEEN) & pieces()) == pieces(sideToMove, KING)) {
					int to = sideToMove == WHITE ? C1.id : C8.id;
					legalMoves.get(from).add(new Move(from, to, QUEENSIDE_CASTLE));
				}
			}

			pieces ^= lsbBB;
		}

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

		int kingSquare = getKingSquare(sideToMove.id);
		return ((pinned(kingSquare, sideToMove) & fromBB) == 0) || (lineBB(kingSquare, from) & toBB) != 0;
	}

	public void generateEvasions() {
		int us = sideToMove.id;
		if (checkers == 0)
			throw new IllegalStateException("Evasions can be generated only when sideToMove's king is in check");

		int kingSq = getKingSquare(us);
		long quietsKing = pseudoMovesBitboard(QUIET, sideToMove, KING, kingSq, pieces(us), pieces(us ^ 1));
		long capturesKing = pseudoMovesBitboard(CAPTURE, sideToMove, KING, kingSq, pieces(us), pieces(us ^ 1));
		BBtoLegalMoves(kingSq, quietsKing, QUIET);
		BBtoLegalMoves(kingSq, capturesKing, CAPTURE);
		// only king can move out of check
		if (more_than_one_bit(checkers)) {
			return;
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
					if (isLegal(move))
						legalMoves.get(move.getFrom()).add(move);
					movesBB ^= 1L << to;
				}

				movesBB = pseudoMovesBitboard(CAPTURE, sideToMove, pieceType, lsb, pieces(us), pieces(us ^ 1));
				while (movesBB != 0) {
					int to = get_lsb(movesBB);
					if (((1L << to) & checkerBB) != 0) {
						Move move = new Move(lsb, to, CAPTURE);
						if (isLegal(move)) {
							legalMoves.get(move.getFrom()).add(move);
						}
					}
					movesBB ^= 1L << to;
				}

				if (epSquare != -1 && board[lsb].pieceType == PAWN && (pawnAttacks(sideToMove.inverse(), epSquare) & from) != 0 &&
						(((squareBB(epSquare) & betweenBB) != 0) || checkerBB == pawnPush(sideToMove.inverse(), epSquare))) {
					Move move = new Move(lsb, epSquare, EP_CAPTURE);
					if (isLegal(move)) legalMoves.get(move.getFrom()).add(move);
				}

				piecesBB ^= from;
			}
		}
	}

	public ePosition make_move(Move move) {
		ePosition p = new ePosition(this);
		int from = move.getFrom();
		int to = move.getTo();

		boolean isDoublePawnPush = isDoublePawnPush(move);

		//utilize castling method to shorten expression
		if (p.board[from].pieceType == ROOK) {
			PieceType side = (squareBB(from) & FileABB) != 0 ? QUEEN : KING;
			p.castlingRights &= ~castle(sideToMove, side);
		} else if (p.board[from].pieceType == KING) {
			p.castlingRights &= ~anyCastling(sideToMove);
		}
		p.move_piece(from, to, move.getMoveType());


		if (move.getMoveType() == KINGSIDE_CASTLE.id || move.getMoveType() == QUEENSIDE_CASTLE.id) {
			int rookFrom = p.sideToMove == WHITE ? (move.getMoveType() == KINGSIDE_CASTLE.id ? H1.id : A1.id) : (move.getMoveType() == KINGSIDE_CASTLE.id ? H8.id : A8.id);
			int rookTo = p.sideToMove == WHITE ? (move.getMoveType() == KINGSIDE_CASTLE.id ? F1.id : D1.id) : (move.getMoveType() == KINGSIDE_CASTLE.id ? F8.id : D8.id);
			if (p.board[rookFrom].pieceType != ROOK || p.board[rookTo] != NO_PIECE)
				throw new IllegalStateException("castling is not legal");
			//either of kingside and queenside movetypes could be passed
			p.move_piece(rookFrom, rookTo, (int) KINGSIDE_CASTLE.id);
		}


		p.epSquare = -1;
		if (isDoublePawnPush) p.epSquare = sideToMove == WHITE ? to - 8 : to + 8;

		p.checkers = p.attackersTo(p.getKingSquare(sideToMove.id ^ 1), p.pieces()) & p.pieces(sideToMove.id);

		p.sideToMove = p.sideToMove.inverse();
		p.generateLegalMoves();
		return p;
	}


	private void move_piece(int from, int to, int moveType) {
		if (board[from] == NO_PIECE)
			throw new IllegalStateException("from square is empty");
		int us = sideToMove.id;
		if (moveType == EP_CAPTURE.id) {
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
		byTypeBB[ALL_PIECES] ^= fromTo;
		byTypeBB[fPieceType] ^= fromTo;
		byColorBB[us] ^= fromTo;

		board[to] = board[from];
		board[from] = NO_PIECE;


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


	private void BBtoLegalMoves(int from, long BB, MoveType moveType) {
		while (BB != 0) {
			int lsb = get_lsb(BB);
			Move move = new Move(from, lsb, moveType);
			if (isLegal(move))
				legalMoves.get(move.getFrom()).add(move);
			BB ^= 1L << lsb;
		}
	}


}
