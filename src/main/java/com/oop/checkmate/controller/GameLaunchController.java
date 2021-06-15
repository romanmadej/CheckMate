package com.oop.checkmate.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import com.oop.checkmate.Navigator;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class GameLaunchController implements BasicController {
	@FXML
	private ChoiceBox<String> pieceColorChoiceBox;
	@FXML
	private ChoiceBox<String> gameModeChoiceBox;
	@FXML
	private Button playButton;
	@FXML
	private Button backButton;

	private boolean againstAI;

	@Override
	public void initialize(Map<String, Object> args) {
		againstAI = (boolean) args.get("againstAI");
		pieceColorChoiceBox.setItems(FXCollections.observableArrayList("White", "Black"));
		pieceColorChoiceBox.getSelectionModel().select(0);
		gameModeChoiceBox.setItems(FXCollections.observableArrayList("Standard", "Chess960"));
		gameModeChoiceBox.getSelectionModel().select(0);
	}

	private String generateChess960FenString() {
		char[] ch = "rnbqkbnr".toCharArray();
		Random generator = new Random();
		StringBuilder strbuilder = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			int pom = generator.nextInt(8 - i);
			char sup = ch[pom];
			ch[pom] = ch[7 - i];
			ch[7 - i] = sup;
			strbuilder.append(ch[7 - i]);
		}
		String result = strbuilder.toString();
		strbuilder = new StringBuilder();
		strbuilder.append(result);
		strbuilder.append("/pppppppp/8/8/8/8/PPPPPPPP/");
		strbuilder.append(result.toUpperCase());
		strbuilder.append(" w - - 0 1");
		return strbuilder.toString();
	}

	@FXML
	private void playButtonOnClicked(MouseEvent mouseEvent) throws IOException {
		if (mouseEvent.getSource() != playButton) {
			throw new UnsupportedOperationException("Incorrect button assigned");
		}
		if (mouseEvent.getButton() != MouseButton.PRIMARY) {
			return;
		}
		boolean whiteBottom = pieceColorChoiceBox.getSelectionModel().getSelectedIndex() == 0;
		boolean standard = gameModeChoiceBox.getSelectionModel().getSelectedIndex() == 0;
		String fenString;
		if (standard) {
			fenString = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		} else {
			fenString = generateChess960FenString();
		}
		Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
		Navigator.navigateToBoardView(stage, fenString, againstAI, whiteBottom);
	}

	@FXML
	private void backButtonOnClicked(MouseEvent mouseEvent) throws IOException {
		if (mouseEvent.getSource() != backButton) {
			throw new UnsupportedOperationException("Incorrect button assigned");
		}
		if (mouseEvent.getButton() != MouseButton.PRIMARY) {
			return;
		}
		Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
		Parent parent = Navigator.loadView("/MenuView.fxml", null);
		stage.getScene().setRoot(parent);
	}
}
