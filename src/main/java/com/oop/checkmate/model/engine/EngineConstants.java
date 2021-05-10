package com.oop.checkmate.model.engine;

import static com.oop.checkmate.Constants.Color;
import static com.oop.checkmate.Constants.Color.BLACK;
import static com.oop.checkmate.Constants.Color.WHITE;
import static com.oop.checkmate.Constants.PieceType.*;
import static com.oop.checkmate.model.engine.BitboardUtils.squareBB;

import com.oop.checkmate.Constants;

public final class EngineConstants {
	private EngineConstants() {
	}

	// cardinalities
	public final static int PIECE_TYPE_N = 6, SQUARE_N = 64, COLOR_N = 2;

	public final static int NORTH = 8, EAST = 1, SOUTH = -NORTH, WEST = -EAST, NORTH_EAST = NORTH + EAST,
			NORTH_WEST = NORTH + WEST, SOUTH_EAST = SOUTH + EAST, SOUTH_WEST = SOUTH + WEST;

	public final static long FileABB = 0x0101010101010101L;
	public final static long FileBBB = FileABB << EAST, FileCBB = FileBBB << EAST, FileDBB = FileCBB << EAST,
			FileEBB = FileDBB << EAST, FileFBB = FileEBB << EAST, FileGBB = FileFBB << EAST, FileHBB = FileGBB << EAST;

	public final static long Rank1BB = 0x00000000000000FFL, Rank2BB = Rank1BB << NORTH, Rank3BB = Rank2BB << NORTH,
			Rank4BB = Rank3BB << NORTH, Rank5BB = Rank4BB << NORTH, Rank6BB = Rank5BB << NORTH,
			Rank7BB = Rank6BB << NORTH, Rank8BB = Rank7BB << NORTH;

	public final static long[] Ranks = new long[] { Rank1BB, Rank2BB, Rank3BB, Rank4BB, Rank5BB, Rank6BB, Rank7BB,
			Rank8BB };

	public enum MoveType {
		QUIET(0), CAPTURE(1L << 2), EVASION(1);

		public final long id;

		MoveType(long id) {
			this.id = id;
		}
	}

	public enum Square {
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

	public enum ePiece {
		NO_PIECE(null, null), W_PAWN(WHITE, PAWN), W_KNIGHT(WHITE, KNIGHT), W_BISHOP(WHITE, BISHOP), W_ROOK(WHITE,
				ROOK), W_QUEEN(WHITE, QUEEN), W_KING(WHITE, KING), B_PAWN(BLACK, PAWN), B_KNIGHT(BLACK,
						KNIGHT), B_BISHOP(BLACK,
								BISHOP), B_ROOK(BLACK, ROOK), B_QUEEN(BLACK, QUEEN), B_KING(BLACK, KING);

		Color color;
		Constants.PieceType pieceType;

		ePiece(Color c, Constants.PieceType pt) {
			this.color = c;
			this.pieceType = pt;
		}

	}
}
