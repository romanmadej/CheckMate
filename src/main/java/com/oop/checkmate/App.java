package com.oop.checkmate;

import static com.oop.checkmate.Constants.SQUARE_SIZE;

import java.io.IOException;

import com.oop.checkmate.view.BoardView;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {
	@Override
	public void start(Stage stage) throws ClassNotFoundException, IOException {
		Class.forName("com.oop.checkmate.UserPreferences");
		Class.forName("com.oop.checkmate.model.engine.MagicBitboards");

		Parent parent = Navigator.loadView("/MenuView.fxml", null);
		stage.setScene(new Scene(parent, 8 * SQUARE_SIZE, 6 * SQUARE_SIZE));
		stage.sizeToScene();
		stage.setTitle("CheckMate");
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
		try {
			Navigator.navigateToMenu(stage);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		launch();
	}
}
