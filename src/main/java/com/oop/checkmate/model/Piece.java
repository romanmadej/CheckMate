package com.oop.checkmate.model;

import static com.oop.checkmate.Constants.Color;
import static com.oop.checkmate.Constants.PieceType;

public class Piece {
	private final Color color;
	private final PieceType pieceType;

	public Color getColor() {
		return color;
	}

	public PieceType getPieceType() {
		return pieceType;
	}

	public Piece(PieceType pieceType, Color color) {
		this.pieceType = pieceType;
		this.color = color;
	}

	private static Piece createPiece(char typeChar, Color color) {
		switch (typeChar) {
		case 'p':
			return new Piece(PieceType.PAWN, color);
		case 'n':
			return new Piece(PieceType.KNIGHT, color);
		case 'b':
			return new Piece(PieceType.BISHOP, color);
		case 'r':
			return new Piece(PieceType.ROOK, color);
		case 'q':
			return new Piece(PieceType.QUEEN, color);
		case 'k':
			return new Piece(PieceType.KING, color);
		}
		throw new IllegalArgumentException(String.valueOf(typeChar));
	}

	public static Piece fromFenChar(char fenChar) {
		return createPiece(Character.toLowerCase(fenChar), Character.isLowerCase(fenChar) ? Color.BLACK : Color.WHITE);
	}
}
