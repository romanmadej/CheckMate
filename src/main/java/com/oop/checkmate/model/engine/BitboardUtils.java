package com.oop.checkmate.model.engine;

import static com.oop.checkmate.model.engine.EngineConstants.*;
import static java.lang.Math.abs;
import static java.lang.Math.max;

public class BitboardUtils {

	public static int maxDist(int aSquare, int bSquare) {
		return max(abs(bSquare / 8 - aSquare / 8), abs(bSquare % 8 - aSquare % 8));
	}

	public static boolean validStep(int id, int step) {
		return id + step >= 0 && id + step < 64;
	}

	public static long squareBB(int id) {
		return 1L << id;
	}

	public static long shift_N(long bb) {
		return bb << NORTH;
	}

	public static long shift_NE(long bb) {
		bb = bb & ~FileHBB;
		return bb << NORTH_EAST;
	}

	public static long shift_NW(long bb) {
		bb = bb & ~FileABB;
		return bb << NORTH_WEST;
	}

	public static long shift_S(long bb) {
		return bb >>> 8;
	}

	public static long shift_SE(long bb) {
		bb = bb & ~FileHBB;
		return bb >>> 7;
	}

	public static long shift_SW(long bb) {
		bb = bb & ~FileABB;
		return bb >>> 9;
	}

	public static long shift_E(long bb) {
		bb = bb & ~FileHBB;
		return bb << 1;
	}

	public static long shift_W(long bb) {
		bb = bb & ~FileABB;
		return bb >>> 1;
	}

	// print utils
	public static void printBBraw(long BB) {
		StringBuilder sb = new StringBuilder(Long.toBinaryString(BB));
		if (sb.length() < 64) {
			String tmp = "0".repeat(64 - sb.length());
			sb.insert(0, tmp);
		}
		System.out.println(sb.toString());
		System.out.println();
	}

	public static void printBB(long BB) {
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

	public static void printBBs(long... BBs) {
		for (long BB : BBs) {
			printBB(BB);
		}
	}
}
