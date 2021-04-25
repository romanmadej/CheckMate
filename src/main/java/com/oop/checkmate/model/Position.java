package com.oop.checkmate.model;

import java.util.Objects;

public class Position {
	public final int x;
	public final int y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public static Position fromSquareId(int id) {
		return new Position(id % 8, 7 - id / 8);
	}

	public int getSquareId() {
		return (7 - y) * 8 + x;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Position)) {
			return false;
		}
		return x == ((Position) o).x && y == ((Position) o).y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public String toString() {
		return "Position{" + "x=" + x + ", y=" + y + '}';
	}
}
