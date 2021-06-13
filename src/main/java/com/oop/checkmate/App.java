package com.oop.checkmate;

import static com.oop.checkmate.Constants.SQUARE_SIZE;

import java.io.IOException;

import com.oop.checkmate.view.BoardView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {
	@Override
	public void start(Stage stage) throws ClassNotFoundException, IOException {
		Class.forName("com.oop.checkmate.model.engine.MagicBitboards");

		Scene scene = Navigator.createNamedScene("/MenuView.fxml", null);
		stage.setTitle("CheckMate");
		stage.setScene(scene);
		stage.setWidth(8 * SQUARE_SIZE);
		stage.setHeight(6 * SQUARE_SIZE);
		stage.setResizable(false);
		stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
		stage.show();
	}

	private void closeWindowEvent(WindowEvent event) {
		Stage stage = (Stage) event.getSource();
		if (!(stage.getScene().getRoot() instanceof BoardView)) {
			return;
		}
		event.consume();

		Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION,
				"Quit to the main menu? Your game will be lost.", ButtonType.YES, ButtonType.NO);

		confirmationAlert.showAndWait();
		if (confirmationAlert.getResult() == ButtonType.YES) {
			try {
				Navigator.of(stage).setNamed("/MenuView.fxml", null);
				stage.setHeight(6 * SQUARE_SIZE);
				stage.setY(stage.getY() + SQUARE_SIZE);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void main(String[] args) {
		launch();
	}
}
