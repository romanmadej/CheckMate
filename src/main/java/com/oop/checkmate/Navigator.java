package com.oop.checkmate;

import static com.oop.checkmate.Constants.SQUARE_SIZE;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.Map;

import com.oop.checkmate.controller.BasicController;
import com.oop.checkmate.controller.BoardController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class Navigator {
	public static Parent loadView(String route, Map<String, Object> args) throws IOException {
		FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(route));
		try {
			Parent parent = loader.load();
			loader.<BasicController>getController().initialize(args);
			return parent;
		} catch (ClassCastException e) {
			throw new InvalidClassException("Class must extend BasicController");
		} catch (Exception e) {
			throw new IOException("Invalid route: " + route);
		}
	}

	public static void navigateToBoardView(Stage stage, String fenString, boolean againstAI, boolean whiteBottom)
			throws IOException {
		BoardController boardController = new BoardController(fenString, againstAI, whiteBottom);

		if (againstAI) {
			stage.setY(stage.getY() - SQUARE_SIZE);
			stage.setScene(new Scene(boardController.getView(), 8 * SQUARE_SIZE, 8 * SQUARE_SIZE));
			stage.sizeToScene();
			return;
		}

		Parent historyView = Navigator.loadView("/HistoryView.fxml", Map.of("boardController", boardController));
		historyView.setTranslateX(8 * SQUARE_SIZE);
		boardController.getView().getChildren().add(historyView);

		stage.setY(stage.getY() - SQUARE_SIZE);
		stage.setX(stage.getX() - 100);
		stage.setScene(new Scene(boardController.getView(), 8 * SQUARE_SIZE + 200, 8 * SQUARE_SIZE));
		stage.sizeToScene();
	}

	public static void navigateToMenu(Stage stage) throws IOException {
		Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION,
				"Quit to the main menu? Your game will be lost.", ButtonType.YES, ButtonType.NO);

		confirmationAlert.showAndWait();
		if (confirmationAlert.getResult() != ButtonType.YES) {
			return;
		}
		Parent parent = Navigator.loadView("/MenuView.fxml", null);

		stage.setY(stage.getY() + SQUARE_SIZE);
		double shift = (stage.getScene().getWidth() - 8 * SQUARE_SIZE) / 2;
		stage.setX(stage.getX() + shift);
		stage.setScene(new Scene(parent, 8 * SQUARE_SIZE, 6 * SQUARE_SIZE));
		stage.sizeToScene();
	}
}
