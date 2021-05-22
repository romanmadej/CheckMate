package com.oop.checkmate.model;

import static com.oop.checkmate.Constants.Color;

import java.util.List;

import com.oop.checkmate.model.engine.Move;
import com.oop.checkmate.model.engine.ePosition;

public class Board {
	private final Piece[][] chessBoard = new Piece[8][8];

	public Piece getPiece(Position position) {
		return chessBoard[position.y][position.x];
	}

	private Board(String fenString) {
		int i = 0;
		for (char c : fenString.toCharArray()) {
			if (c == '/') {
				continue;
			}
			if (Character.isDigit(c)) {
				i += Character.getNumericValue(c);
				continue;
			}

			if (i >= 64) break;
			chessBoard[i / 8][i % 8] = Piece.fromFenChar(c);

			i++;
		}
	}

	public Board() {
		this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
	}

	public static Board fromFenString(String fenString) {
		return new Board(fenString);
	}

	private long getBitboard(Color color) {
		long bitBoard = 0;
		for (int y = 0; y < 8; ++y) {
			for (int x = 0; x < 8; ++x) {
				if (chessBoard[y][x] != null && chessBoard[y][x].getColor() == color) {
					int shift = new Position(x, y).getSquareId();
					bitBoard |= (1L << shift);
				}
			}
		}
		return bitBoard;
	}

	public List<Move> getLegalMoves(Position position, ePosition ePos) {
		return ePos.getLegalMoves(position.getSquareId());
	}

	public void makeMove(Move m) {
		Position from = m.getFromPosition();
		Position to = m.getToPosition();
		chessBoard[to.y][to.x] = chessBoard[from.y][from.x];
		chessBoard[from.y][from.x] = null;
	}
}
