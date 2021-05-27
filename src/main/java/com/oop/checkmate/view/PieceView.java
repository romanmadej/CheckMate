package com.oop.checkmate.view;

import static com.oop.checkmate.Constants.SQUARE_SIZE;

import com.oop.checkmate.Constants;
import com.oop.checkmate.model.Position;
import com.oop.checkmate.model.Piece;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PieceView extends ImageView {
	private static final Image[][] images = new Image[2][6];

	static {
		for (Constants.PieceType pieceType : Constants.PieceType.values()) {
			for (Constants.Color color : Constants.Color.values()) {
				images[color.ordinal()][pieceType.ordinal()] = new Image(
						"/" + color.name().toLowerCase() + "_" + pieceType.name().toLowerCase() + ".png");
			}
		}
	}

	public PieceView(Piece piece) {
		super();
		this.setImage(images[piece.color.ordinal()][piece.pieceType.ordinal()]);
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
