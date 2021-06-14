package com.oop.checkmate.model;

import com.oop.checkmate.model.engine.BoardState;
import com.oop.checkmate.model.engine.Move;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BoardModel extends BoardState {
	private int position = 0;
	public final ObservableList<StateInfo> states = FXCollections.observableArrayList();

	public BoardModel() {
		this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
	}

	public BoardModel(String fenString) {
		super(fenString);
		states.add(super.toStateInfo());
	}

	public void changeState(Move move) {
		super.makeMove(move);
		if (++position < states.size()) {
			states.subList(position, states.size()).clear();
		}
		states.add(super.toStateInfo());
	}

	public void selectState(int index) {
		if (index < 0 || index >= states.size()) {
			throw new IndexOutOfBoundsException();
		}
		for (int i = position; i > index; --i) {
			super.undoLastMove();
		}
		for (int i = position; i < index; ++i) {
			super.makeMove(states.get(i + 1).getLastMove());
		}
		position = index;
	}

	public Move getLastMove() {
		return states.get(position).getLastMove();
	}
}
