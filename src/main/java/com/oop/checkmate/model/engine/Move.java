package com.oop.checkmate.model.engine;

public class Move {
	/*
	 * 32 bit words are used for representing a move bits: 0-5 from square 6-11
	 * destination 12-15 move type move types according to
	 * https://www.chessprogramming.org/Encoding_Moves remaining 16 bits will come
	 * in handy as project develops for e.g. pieces involved in move for easy move
	 * unmaking, zobrist hash related info
	 */

	private long moveBB;

	public Move(long from, long to, long moveType) {
		moveBB |= from;
		moveBB |= (to << 6);
		moveBB |= (moveType << 12);
	}

	public boolean isCapture() {
		return (moveBB >>> 12) == 4;
	}
}
