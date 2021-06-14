package com.oop.checkmate.controller;

import static com.oop.checkmate.Constants.SQUARE_SIZE;
import static com.oop.checkmate.Constants.Color.WHITE;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.oop.checkmate.Constants;
import com.oop.checkmate.model.BoardModel;
import com.oop.checkmate.model.Piece;
import com.oop.checkmate.model.Position;
import com.oop.checkmate.model.engine.BoardState;
import com.oop.checkmate.model.engine.Move;
import com.oop.checkmate.model.engine.PositionAnalysis;
import com.oop.checkmate.view.BoardView;
import com.oop.checkmate.view.PieceView;
import com.oop.checkmate.view.PromotionDialog;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class BoardController {
	private final BoardView boardView;

	private final boolean againstAI;

	public BoardView getView() {
		return boardView;
	}

	public BoardModel getModel() {
		return boardModel;
	}

	private final BoardModel boardModel;

	public BoardController(boolean againstAI) {
		this.boardView = new BoardView();
		this.boardModel = new BoardModel();
		this.againstAI = againstAI;
		draw(boardModel);
	}

	public void selectState(int index) {
		boardModel.selectState(index);
		draw(boardModel);
		boardView.resetHighlight();

		Move move = boardModel.getLastMove();
		if (move != null) {
			boardView.highlightDoneMove(move);
			boardModel.getCheckedKing().ifPresent(boardView::highlightWarning);
		}
	}

	private void draw(BoardState state) {
		boardView.getChildren().removeIf(node -> node instanceof PieceView);
		InputHandler inputHandler = new InputHandler();
		for (int y = 0; y < 8; ++y) {
			for (int x = 0; x < 8; ++x) {
				Position position = new Position(x, y);
				Piece piece = state.getPiece(position);
				if (piece != Piece.NO_PIECE) {
					PieceView pieceView = boardView.createPieceView(piece, position);

					// todo add starting as black
					if (!againstAI || piece.color == Constants.Color.WHITE) {
						pieceView.setOnMousePressed(inputHandler.mousePressedHandler(pieceView));
						pieceView.setOnMouseDragged(inputHandler.mouseDraggedHandler(pieceView));
						pieceView.setOnMouseReleased(inputHandler.mouseReleasedHandler(pieceView));
						pieceView.setPickOnBounds(true);
						pieceView.setCursor(Cursor.OPEN_HAND);
					}
				}
			}
		}
	}

	// make sure before calling that every PieceView is aligned in its square
	private void makeMove(Move move) {
		// board state before move
		if (move.isRegularCapture() || (move.isPromotion() && move.isCapture())) {
			boardView.getChildren().remove(boardView.getPieceView(move.getToPosition()));
		}
		if (move.isEpCapture()) {
			int capturedY = boardModel.getSideToMove() == WHITE ? move.getToPosition().y + 1 : move.getToPosition().y - 1;
			int capturedX = move.getToPosition().x;
			boardView.getChildren().remove(boardView.getPieceView(new Position(capturedX, capturedY)));
		}
		if (move.isKingsideCastling()) {
			int rookY = boardModel.getSideToMove() == WHITE ? 7 : 0;
			PieceView rookPieceView = boardView.getPieceView(new Position(7, rookY));
			rookPieceView.setPosition(new Position(5, rookY));
		}
		if (move.isQueensideCastling()) {
			int rookY = boardModel.getSideToMove() == WHITE ? 7 : 0;
			PieceView rookPieceView = boardView.getPieceView(new Position(0, rookY));
			rookPieceView.setPosition(new Position(3, rookY));
		}
		PieceView pieceView = boardView.getPieceView(move.getFromPosition());
		pieceView.setPosition(move.getToPosition());
		boardView.highlightDoneMove(move);

		boardModel.changeState(move);

		// board state after move
		if (move.isPromotion()) {
			pieceView.setPiece(boardModel.getPiece(move.getToPosition()));
		}
		boardModel.getCheckedKing().ifPresent(boardView::highlightWarning);
	}

	private void makeAIMove() {
		boardView.resetHighlight();
		makeMove(new PositionAnalysis().Analysis(boardModel, 3, boardModel.getSideToMove()));
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
				pieceView.setCursor(Cursor.CLOSED_HAND);

				initialPosition = pieceView.getPosition();
				mouseOffsetX = event.getX() - pieceView.getX();
				mouseOffsetY = event.getY() - pieceView.getY();
				System.out.println("source: " + initialPosition);

				boardView.resetHighlight();
				legalMoves = boardModel.getLegalMoves(initialPosition);
				boardView.highlightPossibleMoves(legalMoves.stream().map(Move::getToPosition).collect(Collectors.toList()));
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
				pieceView.setCursor(Cursor.OPEN_HAND);

				// target position is determined by image center
				double centerX = event.getX() - mouseOffsetX + SQUARE_SIZE / 2.0;
				double centerY = event.getY() - mouseOffsetY + SQUARE_SIZE / 2.0;
				int x = (int) Double.valueOf(centerX / SQUARE_SIZE).longValue();
				int y = (int) Double.valueOf(centerY / SQUARE_SIZE).longValue();
				Position position = new Position(x, y);
				System.out.println("target: " + position + " (" + centerX + ", " + centerY + ")");

				List<Move> moves = legalMoves.stream().filter(m -> m.getToPosition().equals(position))
						.collect(Collectors.toList());

				Move move = null;
				if (!moves.isEmpty()) {
					if (!moves.get(0).isPromotion()) {
						move = moves.get(0);
					} else {
						Optional<Constants.PieceType> result = new PromotionDialog(boardModel.getSideToMove())
								.showAndWait();
						if (result.isPresent()) {
							move = moves.stream().filter(m -> m.getPromotionPieceType() == result.get()).findFirst()
									.orElseGet(() -> null);
						}
					}
				}

				// very important, has to be called before visualizeMove(...)
				// we dont want pieceView to collide in getPieceView(...)
				pieceView.setPosition(initialPosition);

				boardView.resetHighlight();
				pieceView.setViewOrder(0);
				initialPosition = null;
				mouseOffsetX = mouseOffsetY = null;
				legalMoves = null;

				if (move != null) {
					makeMove(move);
					if (againstAI) {
						makeAIMove();
					}
				}
			};
		}
	}
}
