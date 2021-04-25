package com.oop.checkmate.model.engine;

import static com.oop.checkmate.Constants.Color.BLACK;
import static com.oop.checkmate.Constants.Color.WHITE;
import static com.oop.checkmate.Constants.PieceType.*;
import static com.oop.checkmate.model.engine.BitboardUtils.*;
import static com.oop.checkmate.model.engine.EngineConstants.*;
import static com.oop.checkmate.model.engine.EngineConstants.Square.A1;

import com.oop.checkmate.Constants;

/*
Bitboard constants precalculated
 */

class Bitboards {
	private Bitboards() {
	}

	// have to be 0 initialized
	protected static final long[][] pawnAttacks = new long[COLOR_N][SQUARE_N];
	protected static final long[][] pawnPush = new long[COLOR_N][SQUARE_N];
	protected static final long[][] pseudoAttacks = new long[PIECE_TYPE_N][SQUARE_N];

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
		}
	}

	protected static long slidingPseudoAttacks(Constants.PieceType pt, int square, long occupiedBB, long opponentsBB) {
		if (pt != Constants.PieceType.ROOK && pt != Constants.PieceType.BISHOP)
			throw new RuntimeException("method accepts only Rooks and Bishops");

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
