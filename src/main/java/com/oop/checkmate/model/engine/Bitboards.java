package com.oop.checkmate.model.engine;

import static com.oop.checkmate.Constants.Color.BLACK;
import static com.oop.checkmate.Constants.Color.WHITE;
import static com.oop.checkmate.Constants.PieceType.*;
import static com.oop.checkmate.model.engine.BitboardUtils.*;
import static com.oop.checkmate.model.engine.EngineConstants.*;
import static com.oop.checkmate.model.engine.EngineConstants.MoveType.CAPTURE;
import static com.oop.checkmate.model.engine.EngineConstants.MoveType.QUIET;
import static com.oop.checkmate.model.engine.EngineConstants.Square.A1;

import java.util.ArrayList;
import java.util.List;

import com.oop.checkmate.Constants;
import com.oop.checkmate.model.Piece;

/*
Bitboard constants precalculated
 */

class Bitboards {
	private Bitboards() {
	}

	// have to be 0 initialized
	static final long[][] pawnAttacks = new long[COLOR_N][SQUARE_N];
	static final long[][] pawnPush = new long[COLOR_N][SQUARE_N];
	static final long[][] pseudoAttacks = new long[PIECE_TYPE_N][SQUARE_N];
	static final long[][] betweenBB = new long[SQUARE_N][SQUARE_N];

	static {
		init();

	}

	private static void init() {
		for (int square = A1.id; square < SQUARE_N; square++) {
			long sqBB = (1L << square);
			pawnPush[WHITE.id][square] = shift_N(sqBB);
			if(square/8 == 1){
				pawnPush[WHITE.id][square] |= shift_N(shift_N(sqBB));
			}
			pawnPush[BLACK.id][square] = shift_S(sqBB);
			if(square/8 == 6){
				pawnPush[BLACK.id][square] |= shift_S(shift_S(sqBB));
			}

			pawnAttacks[WHITE.id][square] = shift_NE(sqBB) | shift_NW(sqBB);
			pawnAttacks[BLACK.id][square] = shift_SE(sqBB) | shift_SW(sqBB);

			for (int step : new int[] { 1, -9, -8, -7, -1, 7, 8, 9 }) {
				if (validStep(square, step) && maxDist(square, square + step) <= 2)
					pseudoAttacks[KING.id][square] |= squareBB(square + step);
			}
			for (int step : new int[] { -6, -15, -17, -10, 6, 15, 17, 10 })
				if (validStep(square, step) && maxDist(square, square + step) <= 2)
					pseudoAttacks[KNIGHT.id][square] |= squareBB(square + step);

			pseudoAttacks[QUEEN.id][square] |= pseudoAttacks[ROOK.id][square] = slidingPseudoAttacks(
					Constants.PieceType.ROOK, square, 0, 0);
			pseudoAttacks[QUEEN.id][square] |= pseudoAttacks[BISHOP.id][square] = slidingPseudoAttacks(
					Constants.PieceType.BISHOP, square, 0, 0);

			// init betweenBB
			int s1 = square;
			for (int s2 = 0; s2 < 64; s2++) {
				if ((slidingPseudoAttacks(BISHOP, s1, squareBB(s2), squareBB(s2)) & squareBB(s2)) != 0)
					betweenBB[s1][s2] = slidingPseudoAttacks(BISHOP, s1, squareBB(s2), squareBB(s2))
							& slidingPseudoAttacks(BISHOP, s2, squareBB(s1), squareBB(s1));
				if ((slidingPseudoAttacks(ROOK, s1, squareBB(s2), squareBB(s2)) & squareBB(s2)) != 0)
					betweenBB[s1][s2] = slidingPseudoAttacks(ROOK, s1, squareBB(s2), squareBB(s2))
							& slidingPseudoAttacks(ROOK, s2, squareBB(s1), squareBB(s1));
			}
		}
	}

	// works under assumption that QUIET and CAPTURE are only valid MoveTypes
	public static long pseudoMovesBitboard(MoveType moveType, Constants.Color color, Constants.PieceType pieceType,
			int squareId, long alliesBB, long opponentsBB) {
		if (pieceType != ROOK && pieceType != BISHOP && pieceType != QUEEN) {

			if (pieceType == PAWN)
				return moveType == QUIET
						? pawnPush[color.id][squareId] & ~(alliesBB | opponentsBB)
						: pawnAttacks[color.id][squareId] & opponentsBB;

			return pseudoAttacks[pieceType.id][squareId]
					& (moveType == QUIET ? ~(alliesBB | opponentsBB) : opponentsBB);
		}
		if (pieceType == QUEEN)
			return slidingPseudoAttacks(ROOK, squareId, alliesBB | opponentsBB, opponentsBB)
					& (moveType == QUIET ? ~(alliesBB | opponentsBB) : opponentsBB)
					| slidingPseudoAttacks(BISHOP, squareId, alliesBB | opponentsBB, opponentsBB)
							& (moveType == QUIET ? ~(alliesBB | opponentsBB) : opponentsBB);

		return slidingPseudoAttacks(pieceType, squareId, alliesBB | opponentsBB, opponentsBB)
				& (moveType == QUIET ? ~(alliesBB | opponentsBB) : opponentsBB);
	}

	public static List<Move> generatePseudoMoves(int squareId, Piece piece, long alliesBitboard,
			long opponentsBitboard) {
		List<Move> moves = new ArrayList<>();

		// using naive scan for now
		long movesBB = pseudoMovesBitboard(QUIET, piece.getColor(), piece.getPieceType(), squareId, alliesBitboard,
				opponentsBitboard);
		while (movesBB != 0) {
			int lsb = get_lsb(movesBB);
			moves.add(new Move(squareId, lsb, QUIET.id));
			movesBB &= ~(1L << lsb);
		}

		movesBB = pseudoMovesBitboard(CAPTURE, piece.getColor(), piece.getPieceType(), squareId, alliesBitboard,
				opponentsBitboard);
		while (movesBB != 0) {
			int lsb = get_lsb(movesBB);
			moves.add(new Move(squareId, lsb, CAPTURE.id));
			movesBB &= ~(1L << lsb);
		}
		return moves;
	}

	static long slidingPseudoAttacks(Constants.PieceType pt, int square, long occupiedBB, long opponentsBB) {
		if (pt != Constants.PieceType.ROOK && pt != Constants.PieceType.BISHOP)
			throw new IllegalStateException("method accepts only Rooks and Bishops");

		int[] directions = pt == Constants.PieceType.ROOK ? new int[] { NORTH, EAST, SOUTH, WEST }
				: new int[] { NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST };

		long attacksBB = 0;

		for (int step : directions) {
			int i = 1;
			while (validStep(square, step * i) && maxDist(square + step * (i - 1), square + step * i) == 1
					&& (occupiedBB & squareBB(square + step * i)) == 0) {
				attacksBB |= squareBB(square + step * i);
				i++;
			}
			// add occupied square attack
			if (validStep(square, step * i) && maxDist(square + step * (i - 1), square + step * i) == 1
					&& (opponentsBB & squareBB(square + step * i)) != 0)
				attacksBB |= squareBB(square + step * i);
		}
		return attacksBB;
	}

}
