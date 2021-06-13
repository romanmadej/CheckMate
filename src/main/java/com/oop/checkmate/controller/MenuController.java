package com.oop.checkmate.controller;

import static com.oop.checkmate.Constants.SQUARE_SIZE;

import java.io.IOException;
import java.util.Map;

import com.oop.checkmate.Navigator;
import com.oop.checkmate.view.BoardView;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MenuController extends BasicController {
	@FXML
	private ImageView logoImageView;
	@FXML
	private Button playHotseatButton;
	@FXML
	private Button playComputerButton;
	@FXML
	private Button optionsButton;
	@FXML
	private Button exitButton;

	@Override
	public void initialize(Map<String, Object> args) {
		logoImageView.setFitWidth(SQUARE_SIZE * 4);
		playHotseatButton.prefWidthProperty().bind(playComputerButton.widthProperty());
		optionsButton.prefWidthProperty().bind(playComputerButton.widthProperty());
		exitButton.prefWidthProperty().bind(playComputerButton.widthProperty());
	}

	private void navigateToBoardView(Stage stage, BoardView boardView) {
		Navigator.of(stage).set(boardView);
		double titleBarHeight = stage.getHeight() - stage.getScene().getHeight();
		stage.setHeight(SQUARE_SIZE * 8 + titleBarHeight);
		stage.setY(stage.getY() - SQUARE_SIZE);
		stage.setResizable(false);
	}

	@FXML
	private void playHotseatButtonOnClicked(MouseEvent mouseEvent) {
		if (mouseEvent.getSource() != playHotseatButton) {
			throw new UnsupportedOperationException("Incorrect button assigned");
		}
		if (mouseEvent.getButton() != MouseButton.PRIMARY) {
			return;
		}
		Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
		BoardController boardController = new BoardController(false);
		navigateToBoardView(stage, (BoardView) boardController.getView());
	}

	@FXML
	private void playComputerButtonOnClicked(MouseEvent mouseEvent) {
		if (mouseEvent.getSource() != playComputerButton) {
			throw new UnsupportedOperationException("Incorrect button assigned");
		}
		if (mouseEvent.getButton() != MouseButton.PRIMARY) {
			return;
		}
		Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
		BoardController boardController = new BoardController(true);
		navigateToBoardView(stage, (BoardView) boardController.getView());
	}

	@FXML
	private void optionsButtonOnClicked(MouseEvent mouseEvent) throws IOException {
		if (mouseEvent.getSource() != optionsButton) {
			throw new UnsupportedOperationException("Incorrect button assigned");
		}
		if (mouseEvent.getButton() != MouseButton.PRIMARY) {
			return;
		}
		Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
		Navigator.of(stage).pushNamed("/OptionsView.fxml", null);
	}

	public void exitButtonOnClicked(MouseEvent mouseEvent) {
		if (mouseEvent.getSource() != exitButton) {
			throw new UnsupportedOperationException("Incorrect button assigned");
		}
		if (mouseEvent.getButton() != MouseButton.PRIMARY) {
			return;
		}
		Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
		stage.close();
	}
}
