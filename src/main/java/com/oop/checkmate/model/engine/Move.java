package com.oop.checkmate.model.engine;

import java.util.Objects;

import com.oop.checkmate.model.Position;

public class Move {
	/*
	 * 32 bit words are used for representing a move bits: 0-5 from square 6-11
	 * destination 12-15 move type move types according to
	 * https://www.chessprogramming.org/Encoding_Moves remaining 16 bits will come
	 * in handy as project develops for e.g. pieces involved in move for easy move
	 * unmaking, zobrist hash related info
	 */

	private long moveBB;

	public Move(int from, int to, long moveType) {
		moveBB = from;
		moveBB |= (to << 6);
		moveBB |= (moveType << 12);
	}

	public Move(int from, int to, EngineConstants.MoveType moveType) {
		moveBB = from;
		moveBB |= (to << 6);
		moveBB |= (moveType.id << 12);
	}

	int getFrom() {
		return (int) (moveBB & ((1L << 6) - 1));
	}
	int getTo() {
		return (int) ((moveBB >>> 6) & ((1L << 6) - 1));
	}
	int getMoveType() {
		return (int) ((moveBB >>> 12) & ((1L << 6) - 1));
	}

	public boolean isCapture() {
		return (moveBB >>> 12) == 4;
	}

	public Position getFromPosition() {
		int id = (int) (moveBB & ((1L << 6) - 1));
		return Position.fromSquareId(id);
	}

	public Position getToPosition() {
		int id = (int) ((moveBB >>> 6) & ((1L << 6) - 1));
		return Position.fromSquareId(id);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Move)) {
			return false;
		}
		Move move = (Move) o;
		return moveBB == move.moveBB;
	}

	@Override
	public int hashCode() {
		return Objects.hash(moveBB);
	}

	@Override
	public String toString() {
		return this.getFrom() + " -> " + this.getTo() + "   " + this.getMoveType();
	}
}
