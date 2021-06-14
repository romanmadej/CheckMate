package com.oop.checkmate.controller;

import static com.oop.checkmate.Constants.SQUARE_SIZE;

import java.io.IOException;
import java.util.Map;

import com.oop.checkmate.Navigator;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
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
	}

	@FXML
	private void playHotseatButtonOnClicked(MouseEvent mouseEvent) throws IOException {
		if (mouseEvent.getSource() != playHotseatButton) {
			throw new UnsupportedOperationException("Incorrect button assigned");
		}
		if (mouseEvent.getButton() != MouseButton.PRIMARY) {
			return;
		}
		Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
		Parent parent = Navigator.loadView("/GameLaunchView.fxml", Map.of("againstAI", false));
		stage.getScene().setRoot(parent);
	}

	@FXML
	private void playComputerButtonOnClicked(MouseEvent mouseEvent) throws IOException {
		if (mouseEvent.getSource() != playComputerButton) {
			throw new UnsupportedOperationException("Incorrect button assigned");
		}
		if (mouseEvent.getButton() != MouseButton.PRIMARY) {
			return;
		}
		Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
		Parent parent = Navigator.loadView("/GameLaunchView.fxml", Map.of("againstAI", true));
		stage.getScene().setRoot(parent);
	}

	@FXML
	private void optionsButtonOnClicked(MouseEvent mouseEvent) throws IOException {
		if (mouseEvent.getSource() != optionsButton) {
			throw new UnsupportedOperationException("Incorrect button assigned");
		}
		if (mouseEvent.getButton() != MouseButton.PRIMARY) {
			return;
		}
		Parent parent = Navigator.loadView("/OptionsView.fxml", null);
		Stage optionsStage = new Stage();
		optionsStage.setScene(new Scene(parent, 6 * SQUARE_SIZE, 4 * SQUARE_SIZE));
		optionsStage.sizeToScene();
		optionsStage.setTitle("Options");
		optionsStage.setResizable(false);
		optionsStage.initModality(Modality.APPLICATION_MODAL);
		optionsStage.show();
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
