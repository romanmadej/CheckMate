package com.oop.checkmate.controller;

import static com.oop.checkmate.Constants.SQUARE_SIZE;
import static com.oop.checkmate.Constants.Color.BLACK;
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
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class BoardController {
	private final BoardView boardView;

	private final boolean againstAI;
	private final boolean whiteBottom;
	private final Constants.Color bottomColor;

	public BoardView getView() {
		return boardView;
	}

	public BoardModel getModel() {
		return boardModel;
	}

	private final BoardModel boardModel;

	public BoardController(String fenString, boolean againstAI, boolean whiteBottom) {
		this.whiteBottom = whiteBottom;
		this.bottomColor = whiteBottom ? WHITE : BLACK;
		this.boardView = new BoardView(whiteBottom);
		this.boardModel = new BoardModel(fenString);
		this.againstAI = againstAI;
		draw(boardModel);
		if (againstAI && !whiteBottom) {
			makeAIMove();
		}
	}

	private void draw(BoardState state) {
		boardView.getChildren().removeIf(node -> node instanceof PieceView);
		InputHandler inputHandler = new InputHandler();
		for (int y = 0; y < 8; ++y) {
			for (int x = 0; x < 8; ++x) {
				Position position = new Position(x, y);
				Piece piece = state.getPiece(position.rotate(!whiteBottom));
				if (piece != Piece.NO_PIECE) {
					PieceView pieceView = boardView.createPieceView(piece, position);
					if (!againstAI || piece.color == bottomColor) {
						pieceView.setOnMousePressed(inputHandler.mousePressedHandler(pieceView));
						pieceView.setOnMouseDragged(inputHandler.mouseDraggedHandler(pieceView));
						pieceView.setOnMouseReleased(inputHandler.mouseReleasedHandler(pieceView));
						pieceView.setPickOnBounds(true);
						pieceView.setCursor(Cursor.OPEN_HAND);
					}
				}
			}
		}
		boardView.resetHighlight();
		Move move = boardModel.getLastMove();
		if (move != null) {
			boardView.highlightDoneMove(move);
			boardModel.getCheckedKing().ifPresent(boardView::highlightWarning);
		}
	}

	private void makeMove(Move move) {
		boardModel.changeState(move);
		draw(boardModel);
		Optional<Position> checkedKing = boardModel.getCheckedKing();
		if (boardModel.generateLegalMoves().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setHeaderText(checkedKing.isPresent() ? "Checkmate!" : "Stalemate!");
			alert.show();
		}
	}

	private void makeAIMove() {
		makeMove(new PositionAnalysis().Analysis(boardModel, 3, boardModel.getSideToMove()));
	}

	public void selectState(int index) {
		boardModel.selectState(index);
		draw(boardModel);
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
				legalMoves = boardModel.getLegalMoves(initialPosition.rotate(!whiteBottom));
				boardView.highlightPossibleMoves(
						legalMoves.stream().map(Move::getToPosition).collect(Collectors.toList()));
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

				List<Move> moves = legalMoves.stream()
						.filter(m -> m.getToPosition().equals(position.rotate(!whiteBottom)))
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
