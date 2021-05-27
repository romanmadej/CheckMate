package com.oop.checkmate.view;

import com.oop.checkmate.Constants;
import com.oop.checkmate.model.Piece;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class PromotionDialog extends Dialog<Constants.PieceType> {
	private Constants.PieceType selectedType = null;

	public PromotionDialog(Constants.Color color) {
		setTitle("Promote");
		setResultConverter(buttonType -> selectedType);
		HBox hbox = new HBox();
		hbox.getChildren().add(new PromotionButton(Piece.getPiece(color, Constants.PieceType.KNIGHT)));
		hbox.getChildren().add(new PromotionButton(Piece.getPiece(color, Constants.PieceType.BISHOP)));
		hbox.getChildren().add(new PromotionButton(Piece.getPiece(color, Constants.PieceType.ROOK)));
		hbox.getChildren().add(new PromotionButton(Piece.getPiece(color, Constants.PieceType.QUEEN)));
		getDialogPane().setContent(hbox);
		getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
	}

	private class PromotionButton extends Label {
		private final Constants.PieceType pieceType;

		PromotionButton(Piece piece) {
			this.pieceType = piece.pieceType;
			setGraphic(new PieceView(piece));
			setOnMouseClicked(this::onMousePressed);
		}

		private void onMousePressed(MouseEvent e) {
			selectedType = pieceType;
			close();
			e.consume();
		}
	}
}
