package com.oop.checkmate.model.engine;

import static com.oop.checkmate.model.engine.EngineConstants.*;
import static java.lang.Math.abs;
import static java.lang.Math.max;

class BitboardUtils {
	private BitboardUtils() {
	}


	static final long lsb_magic = 0x022fdd63cc95386dL; // the 4061955.

	static final int[] lsb_magictable = new int[]{0, 1, 2, 53, 3, 7, 54, 27, 4, 38, 41, 8, 34, 55, 48, 28, 62, 5, 39, 46, 44, 42,
			22, 9, 24, 35, 59, 56, 49, 18, 29, 11, 63, 52, 6, 26, 37, 40, 33, 47, 61, 45, 43, 21, 23, 58, 17, 10, 51,
			25, 36, 32, 60, 20, 57, 16, 50, 31, 19, 15, 30, 14, 13, 12,};

	// returns log2(lsb) i.e. index of lsb
	static int get_lsb(long BB) {
		return lsb_magictable[(int) (((BB & -BB) * lsb_magic) >>> 58)];
	}

	static long get_lsbBB(long BB) {
		return 1L << lsb_magictable[(int) (((BB & -BB) * lsb_magic) >>> 58)];
	}

	static boolean more_than_one_bit(long BB) {
		return (BB & ~get_lsbBB(BB)) != 0;
	}

	static int maxDist(int aSquare, int bSquare) {
		return max(abs(bSquare / 8 - aSquare / 8), abs(bSquare % 8 - aSquare % 8));
	}

	static boolean validStep(int id, int step) {
		return id + step >= 0 && id + step < 64;
	}

	static long squareBB(int id) {
		return 1L << id;
	}

	static long shift_N(long bb) {
		return bb << NORTH;
	}

	static long shift_NE(long bb) {
		bb = bb & ~FileHBB;
		return bb << NORTH_EAST;
	}

	static long shift_NW(long bb) {
		bb = bb & ~FileABB;
		return bb << NORTH_WEST;
	}

	static long shift_S(long bb) {
		return bb >>> 8;
	}

	static long shift_SE(long bb) {
		bb = bb & ~FileHBB;
		return bb >>> 7;
	}

	static long shift_SW(long bb) {
		bb = bb & ~FileABB;
		return bb >>> 9;
	}

	static long shift_E(long bb) {
		bb = bb & ~FileHBB;
		return bb << 1;
	}

	static long shift_W(long bb) {
		bb = bb & ~FileABB;
		return bb >>> 1;
	}

	// print utils
	static void printBBraw(long BB) {
		StringBuilder sb = new StringBuilder(Long.toBinaryString(BB));
		if (sb.length() < 64) {
			String tmp = "0".repeat(64 - sb.length());
			sb.insert(0, tmp);
		}
		System.out.println(sb.toString());
		System.out.println();
	}

	static void printBB(long BB) {
		for (int i = 7; i >= 0; i--) {

			StringBuilder sb = new StringBuilder(Long.toBinaryString(BB & Ranks[i]));
			if (sb.length() < 64) {
				String tmp = "0".repeat(64 - sb.length());
				sb.insert(0, tmp);
			} // sb is in 64 fixed width format now
			sb = sb.reverse();
			System.out.println(sb.substring(i * 8, i * 8 + 8));
		}
		System.out.println("--------");
	}

	static void printBBs(long... BBs) {
		for (long BB : BBs) {
			printBB(BB);
		}
	}
}
