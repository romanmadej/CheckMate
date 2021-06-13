package com.oop.checkmate.view;

import static com.oop.checkmate.Constants.SQUARE_SIZE;

import com.oop.checkmate.Constants;
import com.oop.checkmate.UserPreferences;
import com.oop.checkmate.model.Piece;
import com.oop.checkmate.model.Position;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PieceView extends ImageView {
	private static final Image[][][] cachedImages = new Image[UserPreferences.PieceSet.values().length][2][6];

	static {
		for (UserPreferences.PieceSet pieceSet : UserPreferences.PieceSet.values()) {
			for (Constants.PieceType pieceType : Constants.PieceType.values()) {
				for (Constants.Color color : Constants.Color.values()) {
					String url = String
							.format("/piecesets/%s/%s_%s.png", pieceSet.name(), color.name(), pieceType.name())
							.toLowerCase();
					cachedImages[pieceSet.ordinal()][color.ordinal()][pieceType.ordinal()] = new Image(url);
				}
			}
		}
	}

	public PieceView(Piece piece) {
		super();
		this.setPiece(piece);
	}

	public void setPiece(Piece piece) {
		Image image = cachedImages[UserPreferences.getPieceSet().ordinal()][piece.color.ordinal()][piece.pieceType
				.ordinal()];
		this.setImage(image);
		this.setFitWidth(SQUARE_SIZE);
		this.setFitHeight(SQUARE_SIZE);
	}

	public void setXY(double x, double y) {
		this.setX(x);
		this.setY(y);
	}

	public Position getPosition() {
		int x = (int) Double.valueOf(this.getX() / SQUARE_SIZE).longValue();
		int y = (int) Double.valueOf(this.getY() / SQUARE_SIZE).longValue();
		return new Position(x, y);
	}

	public void setPosition(Position position) {
		this.setXY(position.x * SQUARE_SIZE, position.y * SQUARE_SIZE);
	}
}
