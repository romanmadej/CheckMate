package com.oop.checkmate.view;

import static com.oop.checkmate.Constants.SQUARE_SIZE;

import java.util.List;
import java.util.NoSuchElementException;

import com.oop.checkmate.model.Piece;
import com.oop.checkmate.model.Position;
import com.oop.checkmate.model.engine.Move;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BoardView extends Group {
	private final Rectangle[][] overlayTiles = new Rectangle[8][8];

	public BoardView() {
		final Color light = Color.valueOf("#FFFFFF");
		final Color dark = Color.valueOf("#E0E1E5");

		GridPane board = new GridPane();
		for (int y = 0; y < 8; ++y) {
			for (int x = 0; x < 8; ++x) {
				Color color = (y + x) % 2 == 0 ? light : dark;
				board.add(new Rectangle(SQUARE_SIZE, SQUARE_SIZE, color), x, y);
			}
		}
		this.getChildren().add(board);

		GridPane highlightBoard = new GridPane();
		for (int y = 0; y < 8; ++y) {
			for (int x = 0; x < 8; ++x) {
				overlayTiles[y][x] = new Rectangle(SQUARE_SIZE, SQUARE_SIZE, Color.TRANSPARENT);
				highlightBoard.add(overlayTiles[y][x], x, y);
			}
		}
		this.getChildren().add(highlightBoard);
	}

	public PieceView createPieceView(Piece piece, Position position) {
		PieceView pieceView = new PieceView(piece);
		pieceView.setPosition(position);
		this.getChildren().add(pieceView);
		return pieceView;
	}

	public PieceView getPieceView(Position position) {
		for (Node node : this.getChildren()) {
			if (!(node instanceof PieceView)) {
				continue;
			}
			PieceView pieceView = (PieceView) node;
			if (pieceView.getPosition().equals(position)) {
				System.out.println("found " + pieceView.getImage().getUrl());
				return pieceView;
			}
		}
		throw new NoSuchElementException();
	}

	private void highlightTiles(Iterable<Position> positions, Color color) {
		positions.forEach(p -> overlayTiles[p.y][p.x].setFill(color.deriveColor(0, 1, 1, 0.2)));
	}

	public void highlightTiles(Iterable<Position> positions) {
		highlightTiles(positions, Color.GREEN);
	}

	public void highlightMove(Move move) {
		highlightTiles(List.of(move.getFromPosition(), move.getToPosition()), Color.YELLOW);
	}

	public void resetHighlight() {
		for (Rectangle[] tileRow : overlayTiles) {
			for (Rectangle tile : tileRow) {
				tile.setFill(Color.TRANSPARENT);
			}
		}
	}
}
