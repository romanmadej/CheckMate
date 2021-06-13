package com.oop.checkmate;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.Map;

import com.oop.checkmate.controller.BasicController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

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
}
