package com.oop.checkmate.model.engine;

import static com.oop.checkmate.Constants.PieceType.*;
import static com.oop.checkmate.model.engine.Bitboards.*;
import static com.oop.checkmate.model.engine.EngineConstants.MoveType;
import static com.oop.checkmate.model.engine.EngineConstants.MoveType.CAPTURE;
import static com.oop.checkmate.model.engine.EngineConstants.MoveType.QUIET;

import java.util.ArrayList;
import java.util.List;

import com.oop.checkmate.Constants;
import com.oop.checkmate.model.Piece;

public class MoveGenerator {
	// works under assumption that QUIET and CAPTURE are only valid MoveTypes
	public static long generatePseudoMovesBitboard(MoveType moveType, Constants.Color color,
			Constants.PieceType pieceType, int squareId, long alliesBB, long opponentsBB) {
		if (pieceType != ROOK && pieceType != BISHOP && pieceType != QUEEN) {

			if (pieceType == PAWN)
				return moveType == QUIET ? pawnPush[color.id][squareId] & ~(alliesBB | opponentsBB)
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
		long movesBB = generatePseudoMovesBitboard(QUIET, piece.getColor(), piece.getPieceType(), squareId,
				alliesBitboard, opponentsBitboard);
		BitboardUtils.printBB(movesBB);
		for (long i = 0; i < 64; i++) {
			if (((1L << i) & movesBB) != 0) {
				moves.add(new Move(squareId, i, QUIET.id));
			}
		}

		movesBB = generatePseudoMovesBitboard(CAPTURE, piece.getColor(), piece.getPieceType(), squareId, alliesBitboard,
				opponentsBitboard);
		BitboardUtils.printBB(movesBB);
		for (long i = 0; i < 64; i++) {
			if (((1L << i) & movesBB) != 0) {
				moves.add(new Move(squareId, i, CAPTURE.id));
			}
		}
		return moves;
	}
}
