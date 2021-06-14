package com.oop.checkmate.controller;

import static com.oop.checkmate.Constants.SQUARE_SIZE;

import java.io.IOException;
import java.util.Map;

import com.oop.checkmate.Navigator;
import com.oop.checkmate.model.engine.BoardState;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class HistoryController extends BasicController {
	@FXML
	private Button prevButton;
	@FXML
	private Button nextButton;
	@FXML
	private Button exitButton;
	@FXML
	private ListView<BoardState.StateInfo> historyListView;

	@Override
	public void initialize(Map<String, Object> args) {
		BoardController boardController = (BoardController) args.get("boardController");
		historyListView.setItems(boardController.getModel().states);
		historyListView.setPrefHeight(8 * SQUARE_SIZE - 50);

		historyListView.setCellFactory(listView -> new ListCell<>() {
			@Override
			protected void updateItem(BoardState.StateInfo item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					this.setText(null);
					this.setOnMousePressed(null);
					return;
				}
				if (item.getLastMove() == null) {
					this.setText("Game start");
					return;
				}
				String text = this.getIndex() + ". " + item.getLastMove();
				if (item.isCheck()) {
					text += " check";
				}
				this.setText(text);
			}
		});

		historyListView.getItems().addListener(
				(ListChangeListener<BoardState.StateInfo>) c -> historyListView.getSelectionModel().selectLast());

		historyListView.getSelectionModel().selectedIndexProperty()
				.addListener((observable, oldValue, newValue) -> boardController.selectState(newValue.intValue()));

		historyListView.getSelectionModel().selectFirst();
		prevButton.disableProperty().bind(historyListView.getSelectionModel().selectedIndexProperty().isEqualTo(0));
		nextButton.disableProperty().bind(historyListView.getSelectionModel().selectedIndexProperty()
				.greaterThanOrEqualTo(Bindings.size(historyListView.getItems()).subtract(1)));
	}

	@FXML
	private void prevButtonOnClicked(MouseEvent mouseEvent) {
		if (mouseEvent.getSource() != prevButton) {
			throw new UnsupportedOperationException("Incorrect button assigned");
		}
		if (mouseEvent.getButton() != MouseButton.PRIMARY) {
			return;
		}
		int selectedIndex = historyListView.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 1) {
			historyListView.getSelectionModel().select(selectedIndex - 1);
		}
	}

	@FXML
	private void nextButtonOnClicked(MouseEvent mouseEvent) {
		if (mouseEvent.getSource() != nextButton) {
			throw new UnsupportedOperationException("Incorrect button assigned");
		}
		if (mouseEvent.getButton() != MouseButton.PRIMARY) {
			return;
		}
		int selectedIndex = historyListView.getSelectionModel().getSelectedIndex();
		if (selectedIndex < historyListView.getItems().size() - 1) {
			historyListView.getSelectionModel().select(selectedIndex + 1);
		}
	}

	@FXML
	private void exitButtonOnClicked(MouseEvent mouseEvent) throws IOException {
		if (mouseEvent.getSource() != exitButton) {
			throw new UnsupportedOperationException("Incorrect button assigned");
		}
		if (mouseEvent.getButton() != MouseButton.PRIMARY) {
			return;
		}
		Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
		Navigator.navigateToMenu(stage);
	}
}
