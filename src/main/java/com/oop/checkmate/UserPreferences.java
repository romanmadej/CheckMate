package com.oop.checkmate;

import java.util.prefs.Preferences;

import javafx.scene.paint.Color;

public final class UserPreferences {
	private UserPreferences() {
	}

	private static final Preferences preferences;

	private enum Keys {
		PIECE_SET, LIGHT_TILE_COLOR, DARK_TILE_COLOR
	}

	public enum PieceSet {
		MERIDA, CASES
	}

	static {
		preferences = Preferences.userNodeForPackage(UserPreferences.class);
		if (PieceSet.values().length == 0) {
			throw new RuntimeException("No defined piece sets");
		}
	}

	public static PieceSet getPieceSet() {
		String dbg = preferences.get(Keys.PIECE_SET.name(),null);
		return PieceSet.valueOf(preferences.get(Keys.PIECE_SET.name(), PieceSet.values()[0].name()));
	}

	public static void setPieceSet(PieceSet pieceSet) {
		preferences.put(Keys.PIECE_SET.name(), pieceSet.name());
	}

	public static Color getLightTileColor() {
		return Color.valueOf(preferences.get(Keys.LIGHT_TILE_COLOR.name(), "#ffffff"));
	}

	public static void setLightTileColor(Color color) {
		preferences.put(Keys.LIGHT_TILE_COLOR.name(), toHexString(color));
	}

	public static Color getDarkTileColor() {
		return Color.valueOf(preferences.get(Keys.DARK_TILE_COLOR.name(), "#e0e1e5"));
	}

	public static void setDarkTileColor(Color color) {
		preferences.put(Keys.DARK_TILE_COLOR.name(), toHexString(color));
	}

	private static String format(double value) {
		String number = Integer.toHexString((int) Math.round(value * 255));
		return number.length() == 1 ? "0" + number : number;
	}

	private static String toHexString(Color value) {
		return "#" + format(value.getRed()) + format(value.getGreen()) + format(value.getBlue());
	}
}
