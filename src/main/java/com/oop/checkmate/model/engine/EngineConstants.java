package com.oop.checkmate.model.engine;

import static com.oop.checkmate.Constants.Color;
import static com.oop.checkmate.Constants.Color.BLACK;
import static com.oop.checkmate.Constants.Color.WHITE;
import static com.oop.checkmate.Constants.PieceType.*;
import static com.oop.checkmate.model.engine.BitboardUtils.squareBB;
import static com.oop.checkmate.model.engine.EngineConstants.Square.*;

import com.oop.checkmate.Constants;

public final class EngineConstants {
	private EngineConstants() {
	}

	// cardinalities
	final static int PIECE_TYPE_N = 6, SQUARE_N = 64, COLOR_N = 2;

	final static int NORTH = 8, EAST = 1, SOUTH = -NORTH, WEST = -EAST, NORTH_EAST = NORTH + EAST,
			NORTH_WEST = NORTH + WEST, SOUTH_EAST = SOUTH + EAST, SOUTH_WEST = SOUTH + WEST;

	final static long FileABB = 0x0101010101010101L;
	final static long FileBBB = FileABB << EAST, FileCBB = FileBBB << EAST, FileDBB = FileCBB << EAST,
			FileEBB = FileDBB << EAST, FileFBB = FileEBB << EAST, FileGBB = FileFBB << EAST, FileHBB = FileGBB << EAST;

	final static long RANK1BB = 0x00000000000000FFL, RANK2BB = RANK1BB << NORTH, RANK3BB = RANK2BB << NORTH,
			RANK4BB = RANK3BB << NORTH, RANK5BB = RANK4BB << NORTH, RANK6BB = RANK5BB << NORTH,
			RANK7BB = RANK6BB << NORTH, RANK8BB = RANK7BB << NORTH;


	final static long[] Ranks = new long[]{RANK1BB, RANK2BB, RANK3BB, RANK4BB, RANK5BB, RANK6BB, RANK7BB,
			RANK8BB};

	final static byte WHITE_OO = 1, WHITE_OOO = 2, BLACK_OO = 4, BLACK_OOO = 8;

	final static char[] Letters = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};

	final static long whiteCornersBB = squareBB(A1.id) | squareBB(H1.id),
			blackCornersBB = squareBB(A8.id) | squareBB(H8.id);
	final static double[] wPawnValue = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0.5, 1, 1, -2, -2,
			1, 1, 0.5, 0.5, -0.5, -1, 0, 0, -1, -0.5, 0.5, 0.5, 0.5, 1, 2.5, 2.5, 1, 0.5, 0.5, 1, 1, 2, 3, 3, 2, 1, 1,
			5, 5, 5, 5, 5, 5, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0 };
	final static double[] bPawnValue = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 5, 5, 5, 5, 5, 1, 1, 2, 3, 3, 2,
			1, 1, 0.5, 0.5, 1, 2.5, 2.5, 1, 0.5, 0.5, 0.5, -0.5, -1, 0, 0, -1, -0.5, 0.5, 0.5, 1, 1, -2, -2, 1, 1, 0.5,
			0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	final static double[] wKnightValue = new double[] { -5, -4, -3, -3, -3, -3, -4, -5, -4, -2, 0, 0, 0, 0, -2, -4, -3,
			0, 1, 1.5, 1.5, 1, 0, -3, -3, 0.5, 1.5, 2, 2, 1.5, 0.5, -3, -3, 0.5, 1.5, 2, 2, 1.5, 0.5, -3, -3, 0, 1, 1.5,
			1.5, 1, 0, -3, -4, -2, 0, 0, 0, 0, -2, -4, -5, -4, -3, -3, -3, -3, -4, -5 };
	final static double[] bKnightValue = new double[] { -5, -4, -3, -3, -3, -3, -4, -5, -4, -2, 0, 0, 0, 0, -2, -4, -3,
			0, 1, 1.5, 1.5, 1, 0, -3, -3, 0.5, 1.5, 2, 2, 1.5, 0.5, -3, -3, 0.5, 1.5, 2, 2, 1.5, 0.5, -3, -3, 0, 1, 1.5,
			1.5, 1, 0, -3, -4, -2, 0, 0, 0, 0, -2, -4, -5, -4, -3, -3, -3, -3, -4, -5 };
	final static double[] wBishopValue = new double[] { -2, -1, -1, -1, -1, -1, -1, -2, -1, 0.5, 0, 0, 0, 0, 0.5, -1,
			-1, 1, 1, 1, 1, 1, 1, -1, -1, 0, 1, 1, 1, 1, 0, -1, -1, 0.5, 0.5, 1, 1, 0.5, 0.5, -1, -1, 0, 0.5, 1, 1, 0.5,
			0, -1, -1, 0, 0, 0, 0, 0, 0, -1, -2, -1, -1, -1, -1, -1, -1, -2 };
	final static double[] bBishopValue = new double[] { -2, -1, -1, -1, -1, -1, -1, -2, -1, 0.5, 0, 0, 0, 0, 0.5, -1,
			-1, 1, 1, 1, 1, 1, 1, -1, -1, 0, 1, 1, 1, 1, 0, -1, -1, 0.5, 0.5, 1, 1, 0.5, 0.5, -1, -1, 0, 0.5, 1, 1, 0.5,
			0, -1, -1, 0, 0, 0, 0, 0, 0, -1, -2, -1, -1, -1, -1, -1, -1, -2 };
	final static double[] wRookValue = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, -0.5, 0, 0, 0, 0, 0, 0, -0.5, -0.5, 0, 0,
			0, 0, 0, 0, -0.5, -0.5, 0, 0, 0, 0, 0, 0, -0.5, -0.5, 0, 0, 0, 0, 0, 0, -0.5, -0.5, 0, 0, 0, 0, 0, 0, -0.5,
			0.5, 1, 1, 1, 1, 1, 1, 0.5, 0, 0, 0, 0.5, 0.5, 0, 0, 0 };
	final static double[] bRookValue = new double[] { 0, 0, 0, 0.5, 0.5, 0, 0, 0, 0.5, 1, 1, 1, 1, 1, 1, 0.5, -0.5, 0,
			0, 0, 0, 0, 0, -0.5, -0.5, 0, 0, 0, 0, 0, 0, -0.5, -0.5, 0, 0, 0, 0, 0, 0, -0.5, -0.5, 0, 0, 0, 0, 0, 0,
			-0.5, -0.5, 0, 0, 0, 0, 0, 0, -0.5, 0, 0, 0, 0, 0, 0, 0, 0 };
	final static double[] wQueenValue = new double[] { -2, -1, -1, -0.5, -0.5, -1, -1, -2, -1, 0, 0.5, 0, 0, 0, 0, -1,
			-1, 0.5, 0.5, 0.5, 0.5, 0.5, 0, -1, 0, 0, 0.5, 0.5, 0.5, 0.5, 0, -0.5, -0.5, 0, 0.5, 0.5, 0.5, 0.5, 0, -0.5,
			-1, 0, 0.5, 0.5, 0.5, 0.5, 0, -1, -1, 0, 0, 0, 0, 0, 0, -1, -2, -1, -1, -0.5, -0.5, -1, -1, -2 };
	final static double[] bQueenValue = new double[] { -2, -1, -1, -0.5, -0.5, -1, -1, -2, -1, 0, 0, 0, 0, 0, 0, -1, 1,
			0, 0.5, 0.5, 0.5, 0.5, 0, -1, -0.5, 0, 0.5, 0.5, 0.5, 0.5, 0, -0.5, 0, 0, 0.5, 0.5, 0.5, 0.5, 0, -0.5, -1,
			0.5, 0.5, 0.5, 0.5, 0.5, 0, -1, -1, 0, 0.5, 0, 0, 0, 0, -1, -2, -1, -1, -0.5, -0.5, -1, -1, -2, };
	final static double[] wKingValue = new double[] { 2, 3, 1, 0, 0, 1, 3, 2, 2, 2, 0, 0, 0, 0, 2, 2, -1, -2, -2, -2,
			-2, -2, -2, -1, -2, -3, -3, -4, -5, -3, -3, -2, -3, -4, -4, -5, -5, -4, -4, -3, -3, -4, -4, -5, -5, -4, -4,
			-3, -3, -4, -4, -5, -5, -4, -4, -3, -3, -4, -4, -5, -5, -4, -4, -3 };
	final static double[] bKingValue = new double[] { -3, -4, -4, -5, -5, -4, -4, -3, -3, -4, -4, -5, -5, -4, -4, -3,
			-3, -4, -4, -5, -5, -4, -4, -3, -3, -4, -4, -5, -5, -4, -4, -3, -2, -3, -3, -4, -5, -3, -3, -2, -1, -2, -2,
			-2, -2, -2, -2, -1, 2, 2, 0, 0, 0, 0, 2, 2, 2, 3, 1, 0, 0, 1, 3, 2 };
	enum MoveType {
		//move encoding based on https://www.chessprogramming.org/Encoding_Moves
		QUIET(0), KINGSIDE_CASTLE(2), QUEENSIDE_CASTLE(3), CAPTURE(4), EP_CAPTURE(5),
		KNIGHT_PROMO(8), BISHOP_PROMO(9), ROOK_PROMO(10), QUEEN_PROMO(11), KNIGHT_PROMO_CAPTURE(12), BISHOP_PROMO_CAPTURE(13), ROOK_PROMO_CAPTURE(14), QUEEN_PROMO_CAPTURE(15);

		public final int id;

		MoveType(int id) {
			this.id = id;
		}
	}

	enum Square {
		A1(), B1(), C1(), D1(), E1(), F1(), G1(), H1(), A2(), B2(), C2(), D2(), E2(), F2(), G2(), H2(), A3(), B3(),
		C3(), D3(), E3(), F3(), G3(), H3(), A4(), B4(), C4(), D4(), E4(), F4(), G4(), H4(), A5(), B5(), C5(), D5(),
		E5(), F5(), G5(), H5(), A6(), B6(), C6(), D6(), E6(), F6(), G6(), H6(), A7(), B7(), C7(), D7(), E7(), F7(),
		G7(), H7(), A8(), B8(), C8(), D8(), E8(), F8(), G8(), H8();

		public final int id;
		public final long BB;

		Square() {
			this.id = this.ordinal();
			this.BB = squareBB(this.id);
		}
	}

	enum ePiece {
		NO_PIECE(null, null), W_PAWN(WHITE, PAWN), W_KNIGHT(WHITE, KNIGHT), W_BISHOP(WHITE, BISHOP), W_ROOK(WHITE,
				ROOK), W_QUEEN(WHITE, QUEEN), W_KING(WHITE, KING), B_PAWN(BLACK, PAWN), B_KNIGHT(BLACK,
				KNIGHT), B_BISHOP(BLACK,
				BISHOP), B_ROOK(BLACK, ROOK), B_QUEEN(BLACK, QUEEN), B_KING(BLACK, KING);
		static ePiece get_ePiece(char typeChar) {
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

		static ePiece get_ePiece(Color color, Constants.PieceType pieceType) {
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

		Color color;
		Constants.PieceType pieceType;

		ePiece(Color c, Constants.PieceType pt) {
			this.color = c;
			this.pieceType = pt;
		}

	}
}
