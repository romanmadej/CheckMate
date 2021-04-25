package com.oop.checkmate.model.engine;

import org.junit.Test;

import com.oop.checkmate.Constants;

import junit.framework.TestCase;

public class MoveGeneratorTest {
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

		long movesBB = MoveGenerator.generatePseudoMovesBitboard(EngineConstants.MoveType.QUIET, Constants.Color.WHITE,
				Constants.PieceType.QUEEN, EngineConstants.Square.F5.id, alliesBB, opponentsBB);

		System.out.println("white queen moves from F5:");
		BitboardUtils.printBB(movesBB);

		TestCase.assertEquals(2641485286422881314L, movesBB);
	}
}
