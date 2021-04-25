package com.oop.checkmate.model.engine;

import static com.oop.checkmate.model.engine.EngineConstants.*;
import static java.lang.Math.abs;
import static java.lang.Math.max;

class BitboardUtils {
	private BitboardUtils() {
	}

	protected static int maxDist(int aSquare, int bSquare) {
		return max(abs(bSquare / 8 - aSquare / 8), abs(bSquare % 8 - aSquare % 8));
	}

	protected static boolean validStep(int id, int step) {
		return id + step >= 0 && id + step < 64;
	}

	protected static long squareBB(int id) {
		return 1L << id;
	}

	protected static long shift_N(long bb) {
		return bb << NORTH;
	}

	protected static long shift_NE(long bb) {
		bb = bb & ~FileHBB;
		return bb << NORTH_EAST;
	}

	protected static long shift_NW(long bb) {
		bb = bb & ~FileABB;
		return bb << NORTH_WEST;
	}

	protected static long shift_S(long bb) {
		return bb >>> 8;
	}

	protected static long shift_SE(long bb) {
		bb = bb & ~FileHBB;
		return bb >>> 7;
	}

	protected static long shift_SW(long bb) {
		bb = bb & ~FileABB;
		return bb >>> 9;
	}

	protected static long shift_E(long bb) {
		bb = bb & ~FileHBB;
		return bb << 1;
	}

	protected static long shift_W(long bb) {
		bb = bb & ~FileABB;
		return bb >>> 1;
	}

	// print utils
	protected static void printBBraw(long BB) {
		StringBuilder sb = new StringBuilder(Long.toBinaryString(BB));
		if (sb.length() < 64) {
			String tmp = "0".repeat(64 - sb.length());
			sb.insert(0, tmp);
		}
		System.out.println(sb.toString());
		System.out.println();
	}

	protected static void printBB(long BB) {
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

	protected static void printBBs(long... BBs) {
		for (long BB : BBs) {
			printBB(BB);
		}
	}
}
