package com.oop.checkmate.controller;

import static com.oop.checkmate.Constants.SQUARE_SIZE;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.oop.checkmate.model.Board;
import com.oop.checkmate.model.Piece;
import com.oop.checkmate.model.Position;
import com.oop.checkmate.model.engine.Move;
import com.oop.checkmate.view.BoardView;
import com.oop.checkmate.view.PieceView;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class BoardController {
	private final BoardView boardView;

	public Parent getView() {
		return boardView;
	}

	private final Board boardModel;

	public BoardController() {
		this.boardModel = new Board();
		this.boardView = new BoardView();

		InputHandler inputHandler = new InputHandler();

		for (int y = 0; y < 8; ++y) {
			for (int x = 0; x < 8; ++x) {
				Position position = new Position(x, y);
				Piece piece = boardModel.getPiece(position);
				if (piece != null) {
					PieceView pieceView = boardView.createPieceView(piece, position);
					pieceView.setOnMousePressed(inputHandler.mousePressedHandler(pieceView));
					pieceView.setOnMouseDragged(inputHandler.mouseDraggedHandler(pieceView));
					pieceView.setOnMouseReleased(inputHandler.mouseReleasedHandler(pieceView));
					pieceView.setPickOnBounds(true);
				}
			}
		}
	}

	private class InputHandler {
		private Position initialPosition;
		private Double mouseOffsetX;
		private Double mouseOffsetY;
		private List<Move> legalMoves;

		EventHandler<MouseEvent> mousePressedHandler(PieceView pieceView) {
			return event -> {
				if (event.getButton() != MouseButton.PRIMARY) {
					return;
				}
				initialPosition = pieceView.getPosition();
				mouseOffsetX = event.getX() - pieceView.getX();
				mouseOffsetY = event.getY() - pieceView.getY();
				System.out.println("source: " + initialPosition);

				boardView.resetHighlight();
				legalMoves = boardModel.getLegalMoves(initialPosition);
				boardView.highlightTiles(legalMoves.stream().map(Move::getToPosition).collect(Collectors.toList()));
				pieceView.setViewOrder(-1);
			};
		}

		EventHandler<MouseEvent> mouseDraggedHandler(PieceView pieceView) {
			return event -> {
				if (event.getButton() != MouseButton.PRIMARY) {
					return;
				}
				pieceView.setXY(event.getX() - mouseOffsetX, event.getY() - mouseOffsetY);
			};
		}

		EventHandler<MouseEvent> mouseReleasedHandler(PieceView pieceView) {
			return event -> {
				if (event.getButton() != MouseButton.PRIMARY) {
					return;
				}
				// target position is determined by image center
				double centerX = event.getX() - mouseOffsetX + SQUARE_SIZE / 2.0;
				double centerY = event.getY() - mouseOffsetY + SQUARE_SIZE / 2.0;
				int x = (int) Double.valueOf(centerX / SQUARE_SIZE).longValue();
				int y = (int) Double.valueOf(centerY / SQUARE_SIZE).longValue();
				Position position = new Position(x, y);
				System.out.println("target: " + position + " (" + centerX + ", " + centerY + ")");

				boardView.resetHighlight();

				Optional<Move> move = legalMoves.stream().filter(m -> m.getToPosition().equals(position)).findFirst();
				if (move.isPresent()) {
					boardModel.makeMove(move.get());
					if (move.get().isCapture()) {
						boardView.getChildren().remove(pieceView);
						boardView.getChildren().remove(boardView.getPieceView(position));
						boardView.getChildren().add(pieceView);
					}
					pieceView.setPosition(position);
					boardView.highlightMove(move.get());
				} else {
					pieceView.setPosition(initialPosition);
				}

				pieceView.setViewOrder(0);
				initialPosition = null;
				mouseOffsetX = mouseOffsetY = null;
				legalMoves = null;
			};
		}
	}
}
