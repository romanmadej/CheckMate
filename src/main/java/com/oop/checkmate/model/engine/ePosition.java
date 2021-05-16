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

import java.util.ArrayList;
import java.util.List;

import com.oop.checkmate.model.Piece;

public class ePosition {
	Color sideToMove;
	long[] byTypeBB;
	long[] byColorBB;
	ePiece[] board;
	ePosition prev;
	long checkers; // opposing color pieces giving check
	List<List<Move>> legalMoves;
	static final List<Move> EMPTY_LIST = new ArrayList<>();
	int epSquare;
	//
	long castlingRights;

	// starting position constructor
	public ePosition() {
		byTypeBB = new long[PIECE_TYPE_N + 1];
		byColorBB = new long[COLOR_N];
		checkers = 0;
		sideToMove = WHITE;
		legalMoves = new ArrayList<>();
		for (int i = 0; i < 64; i++)
			legalMoves.add(new ArrayList<>());
		epSquare = -1;

		byColorBB[WHITE.id] = RANK1BB | RANK2BB;
		byColorBB[BLACK.id] = RANK7BB | RANK8BB;


		byTypeBB[PAWN.id] = RANK2BB | RANK7BB;
		byTypeBB[ROOK.id] = A1.BB | H1.BB | A8.BB | H8.BB;
		byTypeBB[KNIGHT.id] = B1.BB | G1.BB | B8.BB | G8.BB;
		byTypeBB[BISHOP.id] = C1.BB | F1.BB | C8.BB | F8.BB;
		byTypeBB[QUEEN.id] = D1.BB | D8.BB;
		byTypeBB[KING.id] = E1.BB | E8.BB;

		byTypeBB[ALL_PIECES] = byColorBB[WHITE.id] | byColorBB[BLACK.id];

		board = new ePiece[]{W_ROOK, W_KNIGHT, W_BISHOP, W_QUEEN, W_KING, W_BISHOP, W_KNIGHT, W_ROOK, W_PAWN, W_PAWN,
				W_PAWN, W_PAWN, W_PAWN, W_PAWN, W_PAWN, W_PAWN, NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE,
				NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE,
				NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE,
				NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE, NO_PIECE, B_PAWN, B_PAWN, B_PAWN, B_PAWN,
				B_PAWN, B_PAWN, B_PAWN, B_PAWN, B_ROOK, B_KNIGHT, B_BISHOP, B_QUEEN, B_KING, B_BISHOP, B_KNIGHT,
				B_ROOK};

		this.prev = null;
		generateLegalMoves();
	}

	public List<Move> getLegalMoves(int square) {

		if (board[square].color != sideToMove)
			return EMPTY_LIST;
		return legalMoves.get(square);
	}

	public ePosition make_move(Move move) {
		ePosition p = new ePosition(this);
		int from = move.getFrom();
		int to = move.getTo();
		boolean isDoublePawnPush = isDoublePawnPush(move);
		System.out.println("isDoublePawnPush: " + isDoublePawnPush);
		p.move_piece(from, to, move.getMoveType());
		p.epSquare = -1;
		if (isDoublePawnPush) p.epSquare = sideToMove == WHITE ? to - 8 : to + 8;
		p.generateLegalMoves();
		return p;
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
	}

	private boolean isDoublePawnPush(Move move) {
		return board[move.getFrom()].pieceType == PAWN && maxDist(move.getFrom(), move.getTo()) == 2;
	}

	private ePosition(ePosition p) {
		sideToMove = p.sideToMove;
		byTypeBB = p.byTypeBB;
		byColorBB = p.byColorBB;
		board = p.board;
		checkers = p.checkers;
		epSquare = p.epSquare;
		legalMoves = new ArrayList<>();
		for (int i = 0; i < 64; i++)
			legalMoves.add(new ArrayList<>());
		prev = p;
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

	private long pieces(int color, PieceType pieceType) {
		return byColorBB[color] & byTypeBB[pieceType.id];
	}

	private long pieces(Color color, PieceType pieceType) {
		return byColorBB[color.id] & byTypeBB[pieceType.id];
	}

	private int getKingSquare(int color) {
		return get_lsb(pieces(color, KING));
	}

	private void generateLegalMoves() {

		int us = sideToMove.id;

		if (checkers != 0) {
			generateEvasions();
			return;
		}

		long pieces = pieces();
		while (pieces != 0) {
			int lsb = get_lsb(pieces);
			long lsbBB = 1L << lsb;
			List<Move> pseudoMoves = generatePseudoMoves(lsb, new Piece(board[lsb].pieceType, board[lsb].color),
					pieces(us), pieces(us ^ 1), epSquare);
			for (Move move : pseudoMoves) {
				if (isLegal(move)) {
					legalMoves.get(move.getFrom()).add(move);
				}
			}

			pieces ^= lsbBB;
		}

	}


	private void move_piece(int from, int to, int moveType) {
		if (board[from] == NO_PIECE)
			throw new IllegalStateException("from square is empty");
		int us = sideToMove.id;
		if (moveType == EP_CAPTURE.id) {
			if (board[to] != NO_PIECE) throw new IllegalStateException("en passant square is not empty");
			if (sideToMove == WHITE && (squareBB(epSquare) & RANK6BB) == 0 || (sideToMove == BLACK && (squareBB(epSquare) & RANK3BB) == 0))
				throw new IllegalStateException("en passant square can't be on ranks other than 2 or 7");
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

		checkers = attackersTo(getKingSquare(us ^ 1), pieces()) & pieces(us);

		sideToMove = sideToMove.inverse();
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

	private boolean isLegal(Move move) {
		int from = move.getFrom();
		int to = move.getTo();
		long fromBB = 1L << from;
		long toBB = 1L << to;

		if (fromBB == pieces(sideToMove.id, KING)) {
			return (attackersTo(to, pieces() ^ fromBB) & pieces(sideToMove.id ^ 1)) == 0;
		}

		int kingSquare = getKingSquare(sideToMove.id);
		return ((pinned(kingSquare, sideToMove) & fromBB) == 0) || (lineBB(kingSquare, from) & toBB) != 0;
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

}
