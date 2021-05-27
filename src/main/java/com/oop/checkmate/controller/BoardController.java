package com.oop.checkmate.controller;

import static com.oop.checkmate.Constants.SQUARE_SIZE;
import static com.oop.checkmate.Constants.Color.WHITE;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.oop.checkmate.model.Position;
import com.oop.checkmate.model.Piece;
import com.oop.checkmate.model.engine.Move;
import com.oop.checkmate.model.engine.BoardState;
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

	private final BoardState boardState;

	public BoardController() {
		this.boardView = new BoardView();
		this.boardState = new BoardState();

		InputHandler inputHandler = new InputHandler();

		for (int y = 0; y < 8; ++y) {
			for (int x = 0; x < 8; ++x) {
				Position position = new Position(x, y);
				Piece piece = boardState.getPiece(position);
				if (piece != Piece.NO_PIECE) {
					PieceView pieceView = boardView.createPieceView(piece, position);
					pieceView.setOnMousePressed(inputHandler.mousePressedHandler(pieceView));
					pieceView.setOnMouseDragged(inputHandler.mouseDraggedHandler(pieceView));
					pieceView.setOnMouseReleased(inputHandler.mouseReleasedHandler(pieceView));
					pieceView.setPickOnBounds(true);
				}
			}
		}
	}

	// make sure before calling that every PieceView is aligned in its square
	private void visualizeMove(Move move) {
		if (move.isRegularCapture() || (move.isPromotion() && move.isCapture())) {
			boardView.getChildren().remove(boardView.getPieceView(move.getToPosition()));
		}
		if (move.isEpCapture()) {
			int capturedY = boardState.getSideToMove() == WHITE ? move.getToPosition().y + 1 : move.getToPosition().y - 1;
			int capturedX = move.getToPosition().x;
			boardView.getChildren().remove(boardView.getPieceView(new Position(capturedX, capturedY)));
		}
		if (move.isKingsideCastling()) {
			int rookY = boardState.getSideToMove() == WHITE ? 7 : 0;
			PieceView rookPieceView = boardView.getPieceView(new Position(7, rookY));
			rookPieceView.setPosition(new Position(5, rookY));
		}
		if (move.isQueensideCastling()) {
			int rookY = boardState.getSideToMove() == WHITE ? 7 : 0;
			PieceView rookPieceView = boardView.getPieceView(new Position(0, rookY));
			rookPieceView.setPosition(new Position(3, rookY));
		}
		boardState.makeMove(move);
		boardView.getPieceView(move.getFromPosition()).setPosition(move.getToPosition());
		boardView.highlightMove(move);
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
				legalMoves = boardState.getLegalMoves(initialPosition);
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

				// very important, has to be called before visualizeMove(...)
				// we dont want pieceView to collide in getPieceView(...)
				pieceView.setPosition(initialPosition);

				boardView.resetHighlight();
				pieceView.setViewOrder(0);

				Optional<Move> move = legalMoves.stream().filter(m -> m.getToPosition().equals(position)).findFirst();
				move.ifPresent(BoardController.this::visualizeMove);

				initialPosition = null;
				mouseOffsetX = mouseOffsetY = null;
				legalMoves = null;
			};
		}
	}
}
