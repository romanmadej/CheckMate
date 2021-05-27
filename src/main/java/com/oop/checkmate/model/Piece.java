package com.oop.checkmate.model;

import static com.oop.checkmate.Constants.Color.BLACK;
import static com.oop.checkmate.Constants.Color.WHITE;
import static com.oop.checkmate.Constants.PieceType.*;

import com.oop.checkmate.Constants;

public enum Piece {
	NO_PIECE(null, null), W_PAWN(WHITE, PAWN), W_KNIGHT(WHITE, KNIGHT), W_BISHOP(WHITE, BISHOP), W_ROOK(WHITE, ROOK),
	W_QUEEN(WHITE, QUEEN), W_KING(WHITE, KING), B_PAWN(BLACK, PAWN), B_KNIGHT(BLACK, KNIGHT), B_BISHOP(BLACK, BISHOP),
	B_ROOK(BLACK, ROOK), B_QUEEN(BLACK, QUEEN), B_KING(BLACK, KING);

	public static Piece getPiece(char typeChar) {
		switch (typeChar) {
		case 'p':
			return B_PAWN;
		case 'n':
			return B_KNIGHT;
		case 'b':
			return B_BISHOP;
		case 'r':
			return B_ROOK;
		case 'q':
			return B_QUEEN;
		case 'k':
			return B_KING;
		case 'P':
			return W_PAWN;
		case 'N':
			return W_KNIGHT;
		case 'B':
			return W_BISHOP;
		case 'R':
			return W_ROOK;
		case 'Q':
			return W_QUEEN;
		case 'K':
			return W_KING;
		default:
			throw new IllegalArgumentException(String.valueOf(typeChar));
		}
	}

	public static Piece getPiece(Constants.Color color, Constants.PieceType pieceType) {
		switch (pieceType) {
		case KNIGHT:
			return color == WHITE ? W_KNIGHT : B_KNIGHT;
		case PAWN:
			return color == WHITE ? W_PAWN : B_PAWN;
		case BISHOP:
			return color == WHITE ? W_BISHOP : B_BISHOP;
		case ROOK:
			return color == WHITE ? W_ROOK : B_ROOK;
		case KING:
			return color == WHITE ? W_KING : B_KING;
		case QUEEN:
			return color == WHITE ? W_QUEEN : B_QUEEN;
		}
		throw new IllegalStateException("couldn't find ePiece");
	}

	public final Constants.Color color;
	public final Constants.PieceType pieceType;

	Piece(Constants.Color color, Constants.PieceType pieceType) {
		this.color = color;
		this.pieceType = pieceType;
	}
}
