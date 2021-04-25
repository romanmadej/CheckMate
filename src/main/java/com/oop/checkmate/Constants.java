package com.oop.checkmate;

public class Constants {
	public static final int SQUARE_SIZE = 64;

	public enum PieceType {
		PAWN(), KNIGHT(), BISHOP(), ROOK(), QUEEN(), KING();

		public final int id;

		PieceType() {
			this.id = this.ordinal();
		}
	}

	public enum Color {
		WHITE(), BLACK();

		public final int id;

		Color() {
			this.id = this.ordinal();
		}

		public Color inverse() {
			return this == WHITE ? BLACK : WHITE;
		}
	}
}
