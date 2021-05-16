package com.oop.checkmate.model.engine;

import static com.oop.checkmate.Constants.Color.BLACK;
import static com.oop.checkmate.Constants.Color.WHITE;
import static com.oop.checkmate.Constants.PieceType.KING;
import static com.oop.checkmate.Constants.PieceType.QUEEN;
import static com.oop.checkmate.model.engine.BitboardUtils.get_lsb;
import static com.oop.checkmate.model.engine.BitboardUtils.squareBB;
import static com.oop.checkmate.model.engine.Bitboards.pseudoMovesBitboard;
import static com.oop.checkmate.model.engine.EngineConstants.Square.*;
import static com.oop.checkmate.model.engine.ePosition.castlingLineBB;

import org.junit.Test;

import com.oop.checkmate.Constants;

import junit.framework.TestCase;

public class MoveGeneratingTest {
	@Test
	public void PrintTest() {
		long alliesBB = 0b0000000000000000000000000000000000000000000000001111111111111111L;
		long opponentsBB = 0b1111111111111111000000000000000000000000000000000000000000000000L;

		System.out.println("white pieces:");
		BitboardUtils.printBB(alliesBB);
		System.out.println("black pieces:");
		BitboardUtils.printBB(opponentsBB);
	}

	@Test
	public void QueenTest() {
		long alliesBB = 0b0000000000000000000000000010000000000000000000000000000000000000L;
		long opponentsBB = 0b0000000000000000000000000000000000000000000000000000000000000000L;

		long movesBB = pseudoMovesBitboard(EngineConstants.MoveType.QUIET, WHITE,
				Constants.PieceType.QUEEN, EngineConstants.Square.F5.id, alliesBB, opponentsBB);

		System.out.println("white queen moves from F5:");
		BitboardUtils.printBB(movesBB);

		TestCase.assertEquals(2641485286422881314L, movesBB);
	}

	@Test
	public void BitboardUtilsTest() {

		TestCase.assertEquals(0, get_lsb(0x1));
		TestCase.assertEquals(63, get_lsb(0x8000000000000000L));

	}

	@Test
	public void castlingLineBBTest() {

		long whiteKingSideLine = squareBB(E1.id) | squareBB(F1.id) | squareBB(G1.id);
		long whiteQueenSideLine = squareBB(E1.id) | squareBB(D1.id) | squareBB(C1.id) | squareBB(B1.id);
		long blackKingSideLine = squareBB(E8.id) | squareBB(F8.id) | squareBB(G8.id);
		long blackQueenSideLine = squareBB(E8.id) | squareBB(D8.id) | squareBB(C8.id) | squareBB(B8.id);

		TestCase.assertEquals(whiteKingSideLine, castlingLineBB(WHITE, KING));
		TestCase.assertEquals(whiteQueenSideLine, castlingLineBB(WHITE, QUEEN));
		TestCase.assertEquals(blackKingSideLine, castlingLineBB(BLACK, KING));
		TestCase.assertEquals(blackQueenSideLine, castlingLineBB(BLACK, QUEEN));

	}

}

