package com.oop.checkmate.model.engine;

import static com.oop.checkmate.Constants.Color.BLACK;
import static com.oop.checkmate.Constants.Color.WHITE;
import static com.oop.checkmate.Constants.PieceType.KING;
import static com.oop.checkmate.Constants.PieceType.QUEEN;
import static com.oop.checkmate.model.engine.BitboardUtils.get_lsb;
import static com.oop.checkmate.model.engine.BitboardUtils.squareBB;
import static com.oop.checkmate.model.engine.Bitboards.pseudoMovesBitboard;
import static com.oop.checkmate.model.engine.EngineConstants.MoveType.*;
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

	@Test
	public void squareNamesTest() {
		for (EngineConstants.Square s : EngineConstants.Square.values()) {
			Move m = new Move(s.ordinal(), 0, 0);
			TestCase.assertEquals(s, m.getFromSquare());
		}
	}

	@Test
	public void isPromotionTest() {
		TestCase.assertTrue(new Move(0, 0, KNIGHT_PROMO).isPromotion());
		TestCase.assertTrue(new Move(0, 0, BISHOP_PROMO).isPromotion());
		TestCase.assertTrue(new Move(0, 0, ROOK_PROMO).isPromotion());
		TestCase.assertTrue(new Move(0, 0, QUEEN_PROMO).isPromotion());

		TestCase.assertTrue(new Move(0, 0, KNIGHT_PROMO_CAPTURE).isPromotion());
		TestCase.assertTrue(new Move(0, 0, BISHOP_PROMO_CAPTURE).isPromotion());
		TestCase.assertTrue(new Move(0, 0, ROOK_PROMO_CAPTURE).isPromotion());
		TestCase.assertTrue(new Move(0, 0, QUEEN_PROMO_CAPTURE).isPromotion());


		TestCase.assertFalse(new Move(0, 0, QUIET).isPromotion());
		TestCase.assertFalse(new Move(0, 0, KINGSIDE_CASTLE).isPromotion());
		TestCase.assertFalse(new Move(0, 0, CAPTURE).isPromotion());
		TestCase.assertFalse(new Move(0, 0, EP_CAPTURE).isPromotion());

	}


	@Test
	public void startingPositionTest() {

		TestCase.assertEquals(1, Perft.dfs(new ePosition(), 0, 0, null));
		TestCase.assertEquals(20, Perft.dfs(new ePosition(), 0, 1, null));
		TestCase.assertEquals(400, Perft.dfs(new ePosition(), 0, 2, null));
		TestCase.assertEquals(8902, Perft.dfs(new ePosition(), 0, 3, null));
		TestCase.assertEquals(197281, Perft.dfs(new ePosition(), 0, 4, null));
		TestCase.assertEquals(4865609, Perft.dfs(new ePosition(), 0, 5, null));
		TestCase.assertEquals(119060324, Perft.dfs(new ePosition(), 0, 6, null));

	}

	//tested positions can be found on https://www.chessprogramming.org/Perft_Results
	@Test
	public void kiwiPete() {
		long nodes1 = Perft.dfs(new ePosition("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -"), 0, 1, null);
		TestCase.assertEquals(48, nodes1);

		long nodes2 = Perft.dfs(new ePosition("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -"), 0, 2, null);
		TestCase.assertEquals(2039, nodes2);

		long nodes3 = Perft.dfs(new ePosition("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -"), 0, 3, null);
		TestCase.assertEquals(97862, nodes3);

		long nodes4 = Perft.dfs(new ePosition("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -"), 0, 4, null);
		TestCase.assertEquals(4085603, nodes4);

		long nodes5 = Perft.dfs(new ePosition("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -"), 0, 5, null);
		TestCase.assertEquals(193690690, nodes5);

	}

	@Test
	public void Position3() {
		long nodes1 = Perft.dfs(new ePosition("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -"), 0, 1, null);
		TestCase.assertEquals(14, nodes1);

		long nodes2 = Perft.dfs(new ePosition("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -"), 0, 2, null);
		TestCase.assertEquals(191, nodes2);

		long nodes3 = Perft.dfs(new ePosition("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -"), 0, 3, null);
		TestCase.assertEquals(2812, nodes3);

		long nodes4 = Perft.dfs(new ePosition("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -"), 0, 4, null);
		TestCase.assertEquals(43238, nodes4);

		long nodes5 = Perft.dfs(new ePosition("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -"), 0, 5, null);
		TestCase.assertEquals(674624, nodes5);

	}

	@Test
	public void Position4() {
		long nodes1 = Perft.dfs(new ePosition("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1"), 0, 1, null);
		TestCase.assertEquals(6, nodes1);

		long nodes2 = Perft.dfs(new ePosition("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1"), 0, 2, null);
		TestCase.assertEquals(264, nodes2);

		long nodes3 = Perft.dfs(new ePosition("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1"), 0, 3, null);
		TestCase.assertEquals(9467, nodes3);

		long nodes4 = Perft.dfs(new ePosition("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1"), 0, 4, null);
		TestCase.assertEquals(422333, nodes4);

		long nodes5 = Perft.dfs(new ePosition("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1"), 0, 5, null);
		TestCase.assertEquals(15833292, nodes5);

	}

	@Test
	public void Position5() {
		long nodes1 = Perft.dfs(new ePosition("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8"), 0, 1, null);
		TestCase.assertEquals(44, nodes1);

		long nodes2 = Perft.dfs(new ePosition("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8"), 0, 2, null);
		TestCase.assertEquals(1486, nodes2);

		long nodes3 = Perft.dfs(new ePosition("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8"), 0, 3, null);
		TestCase.assertEquals(62379, nodes3);

		long nodes4 = Perft.dfs(new ePosition("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8"), 0, 4, null);
		TestCase.assertEquals(2103487, nodes4);

		long nodes5 = Perft.dfs(new ePosition("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8"), 0, 5, null);
		TestCase.assertEquals(89941194, nodes5);


	}

	@Test
	public void Position6() {
		long nodes1 = Perft.dfs(new ePosition("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10"), 0, 1, null);
		TestCase.assertEquals(46, nodes1);

		long nodes2 = Perft.dfs(new ePosition("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10"), 0, 2, null);
		TestCase.assertEquals(2079, nodes2);

		long nodes3 = Perft.dfs(new ePosition("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10"), 0, 3, null);
		TestCase.assertEquals(89890, nodes3);

		long nodes4 = Perft.dfs(new ePosition("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10"), 0, 4, null);
		TestCase.assertEquals(3894594, nodes4);

		long nodes5 = Perft.dfs(new ePosition("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10"), 0, 5, null);
		TestCase.assertEquals(164075551, nodes5);
	}

}