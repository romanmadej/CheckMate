package com.oop.checkmate.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.oop.checkmate.Navigator;
import com.oop.checkmate.UserPreferences;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class OptionsController implements BasicController {
	@FXML
	private ChoiceBox<UserPreferences.PieceSet> pieceSetChoiceBox;
	@FXML
	private ColorPicker lightColorPicker;
	@FXML
	private ColorPicker darkColorPicker;
	@FXML
	private Button saveChangesButton;
	@FXML
	private Button restoreButton;
	@FXML
	private Button doneButton;

	private final SimpleObjectProperty<UserPreferences.PieceSet> currentPieceSet = new SimpleObjectProperty<>();
	private final SimpleObjectProperty<Color> currentLightTileColor = new SimpleObjectProperty<>();
	private final SimpleObjectProperty<Color> currentDarkTileColor = new SimpleObjectProperty<>();

	@Override
	public void initialize(Map<String, Object> args) {
		pieceSetChoiceBox.setItems(FXCollections.observableList(Arrays.asList(UserPreferences.PieceSet.values())));
		pieceSetChoiceBox.setConverter(new StringConverter<>() {
			@Override
			public String toString(UserPreferences.PieceSet object) {
				return StringUtils.capitalize(object.name().toLowerCase());
			}

			@Override
			public UserPreferences.PieceSet fromString(String string) {
				return UserPreferences.PieceSet.valueOf(string.toUpperCase());
			}
		});

		currentPieceSet.setValue(UserPreferences.getPieceSet());
		pieceSetChoiceBox.setValue(currentPieceSet.getValue());

		currentLightTileColor.setValue(UserPreferences.getLightTileColor());
		lightColorPicker.getCustomColors().clear();
		lightColorPicker.getCustomColors().add(currentLightTileColor.getValue());
		lightColorPicker.setValue(currentLightTileColor.getValue());

		currentDarkTileColor.setValue(UserPreferences.getDarkTileColor());
		darkColorPicker.getCustomColors().clear();
		darkColorPicker.getCustomColors().add(currentDarkTileColor.getValue());
		darkColorPicker.setValue(currentDarkTileColor.getValue());

		// prevent transparent colors from being chosen
		lightColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.isOpaque()) {
				lightColorPicker.setValue(Color.color(newValue.getRed(), newValue.getGreen(), newValue.getBlue()));
			}
		});
		darkColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.isOpaque()) {
				darkColorPicker.setValue(Color.color(newValue.getRed(), newValue.getGreen(), newValue.getBlue()));
			}
		});

		pieceSetChoiceBox.prefWidthProperty().bind(lightColorPicker.widthProperty());

		BooleanBinding settingsChangedBinding = new SimpleBooleanProperty(false)
				.or(pieceSetChoiceBox.valueProperty().isNotEqualTo(currentPieceSet))
				.or(lightColorPicker.valueProperty().isNotEqualTo(currentLightTileColor))
				.or(darkColorPicker.valueProperty().isNotEqualTo(currentDarkTileColor));

		saveChangesButton.disableProperty().bind(settingsChangedBinding.not());
		restoreButton.disableProperty().bind(settingsChangedBinding.not());
	}

	// https://en.wikipedia.org/wiki/YUV#Conversion_to/from_RGB
	private double perceivedBrightness(Color color) {
		return 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();
	}

	public void saveChangesButtonOnClicked(MouseEvent mouseEvent) {
		if (mouseEvent.getSource() != saveChangesButton) {
			throw new UnsupportedOperationException("Incorrect button assigned");
		}
		if (mouseEvent.getButton() != MouseButton.PRIMARY) {
			return;
		}

		double brightnessDifference = perceivedBrightness(lightColorPicker.getValue())
				- perceivedBrightness(darkColorPicker.getValue());

		if (brightnessDifference < 0) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("The light tile color must be brighter than the dark tile color.");
			alert.showAndWait();
			return;
		}
		if (brightnessDifference < 0.1) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("The contrast between the colors is too low.");
			alert.showAndWait();
			return;
		}

		currentPieceSet.setValue(pieceSetChoiceBox.getValue());
		currentLightTileColor.setValue(lightColorPicker.getValue());
		currentDarkTileColor.setValue(darkColorPicker.getValue());
		UserPreferences.setPieceSet(currentPieceSet.getValue());
		UserPreferences.setLightTileColor(currentLightTileColor.getValue());
		UserPreferences.setDarkTileColor(currentDarkTileColor.getValue());
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setContentText("Your changes have been saved.");
		alert.showAndWait();
	}

	public void restoreButtonOnClicked(MouseEvent mouseEvent) {
		if (mouseEvent.getSource() != restoreButton) {
			throw new UnsupportedOperationException("Incorrect button assigned");
		}
		if (mouseEvent.getButton() != MouseButton.PRIMARY) {
			return;
		}
		pieceSetChoiceBox.setValue(currentPieceSet.getValue());
		lightColorPicker.getCustomColors().set(0, currentLightTileColor.getValue());
		lightColorPicker.setValue(currentLightTileColor.getValue());
		lightColorPicker.getCustomColors().set(0, currentDarkTileColor.getValue());
		darkColorPicker.setValue(currentDarkTileColor.getValue());
	}

	public void doneButtonOnClicked(MouseEvent mouseEvent) throws IOException {
		if (mouseEvent.getSource() != doneButton) {
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
