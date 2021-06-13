package com.oop.checkmate;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Stack;

import com.oop.checkmate.controller.BasicController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Navigator {
	private static final Map<Stage, Navigator> navigators = new IdentityHashMap<>();

	public static Navigator of(Stage stage) {
		return navigators.computeIfAbsent(stage, Navigator::new);
	}

	public static Scene createNamedScene(String route, Map<String, Object> args) throws IOException {
		return new Scene(loadRoute(route, args));
	}

	private static Parent loadRoute(String route, Map<String, Object> args) throws IOException {
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

	private final Scene scene;
	private final Stack<Parent> pageStack;

	private Navigator(Stage stage) {
		this.scene = stage.getScene();
		pageStack = new Stack<>();
		pageStack.push(scene.getRoot());
	}

	public void set(Parent parent) {
		pageStack.clear();
		pageStack.push(parent);
		scene.setRoot(parent);
	}

	public void setNamed(String route, Map<String, Object> args) throws IOException {
		set(loadRoute(route, args));
	}

	public void push(Parent parent) {
		BorderPane borderPane = new BorderPane(parent);

		Button backButton = new Button();
		backButton.setText("←");
		backButton.setOnMouseClicked(e -> pop());
		borderPane.setTop(backButton);

		pageStack.push(borderPane);
		scene.setRoot(borderPane);
	}

	public void pushNamed(String route, Map<String, Object> args) throws IOException {
		push(loadRoute(route, args));
	}

	public void pop() throws UnsupportedOperationException {
		pageStack.pop();
		if (pageStack.empty()) {
			((Stage) scene.getWindow()).close();
		} else {
			scene.setRoot(pageStack.peek());
		}
	}
}
