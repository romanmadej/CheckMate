package com.oop.checkmate;

import static com.oop.checkmate.Constants.SQUARE_SIZE;

import com.oop.checkmate.controller.BoardController;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
	@Override
	public void start(Stage stage) {
		int size = 8 * SQUARE_SIZE;
		BoardController boardController = new BoardController();
		Scene scene = new Scene(boardController.getView(), size, size);
		stage.setTitle("CheckMate");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}
