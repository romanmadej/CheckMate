package com.oop.checkmate.model.engine;

import static com.oop.checkmate.model.engine.EngineConstants.*;

import java.util.List;

import com.oop.checkmate.Constants;
import com.oop.checkmate.model.Piece;


public class PositionAnalysis {

	public Move Analysis(BoardState boardState, int depth, Constants.Color maximizingPlayer) {
        List <Move> Candidates = boardState.generateLegalMoves();
        Move BestMove = null;
		double eval;
		double alpha = -999999;
		double beta = 999999;
		if (maximizingPlayer == boardState.sideToMove) {
			eval = -99999;
			for (Move move : Candidates) {
				boardState.makeMove(move);
				double current = Analyse(boardState, depth - 1, alpha, beta, maximizingPlayer);
				boardState.undoLastMove();
				if (alpha < current) {
					alpha = current;
				}
				if (beta <= alpha) {
					break;
				}
				if (current > eval) {
					eval = current;
					BestMove = move;
				}
			}
			return BestMove;
		} else {
			eval = 999999;
			for (Move move : Candidates) {
				boardState.makeMove(move);
				double current = Analyse(boardState, depth - 1, alpha, beta, maximizingPlayer);
				boardState.undoLastMove();
				if (current < eval) {
					eval = current;
					BestMove = move;
				}
				if (beta > current) {
					beta = current;
				}
				if (beta <= alpha) {
					break;
				}
			}
			return BestMove;
		}
    }

	public double Analyse(BoardState boardState, int depth, double alpha, double beta,
			Constants.Color maximizingPlayer) {

		List<Move> Candidates = boardState.generateLegalMoves();
		if (depth == 0 || Candidates.isEmpty()) {
			return Evaluation(boardState, maximizingPlayer);
		}
		Move BestMove = null;
		double eval;
		if (maximizingPlayer == boardState.sideToMove) {
			eval = -99999;
			for (Move move : Candidates) {
				boardState.makeMove(move);
				double current = Analyse(boardState, depth - 1, alpha, beta, maximizingPlayer);
				boardState.undoLastMove();
				if (current > eval) {
					eval = current;
					BestMove = move;
				}
				if (alpha < current) {
					alpha = current;
				}
				if (beta <= alpha) {
					break;
				}
			}
			return eval;
		} else {
			eval = 999999;
			for (Move move : Candidates) {
				boardState.makeMove(move);
				double current = Analyse(boardState, depth - 1, alpha, beta, maximizingPlayer);
				boardState.undoLastMove();
				if (current < eval) {
					eval = current;
					BestMove = move;
				}
				if (beta > current) {
					beta = current;
				}
				if (beta <= alpha) {
					break;
				}
			}
			return eval;
		}
	}

	public double Evaluation(BoardState boardState, Constants.Color maximizingPlayer) {
		boardState.generateLegalMoves();
		boolean checkmate = false;
		List<Move> legal = boardState.generateLegalMoves();
		if (legal.isEmpty()) {
			checkmate = true;
		}
		if (checkmate) {
			return -2000;
		}
		double player = 0;
		double opponent = 0;
		for (int i = 0; i < 64; i++) {
			Piece piece = boardState.board[i];
			if (piece == Piece.NO_PIECE) {
				continue;
			}
			if (piece.color == maximizingPlayer) {
				player += PieceValues(piece, i);
			} else {
				opponent += PieceValues(piece, i);
			}
		}
		return player - opponent;
	}

	double PieceValues(Piece piece, int position) {
		if (piece.color == Constants.Color.WHITE) {
			if (piece.pieceType == Constants.PieceType.PAWN) {
				return 10 + wPawnValue[position];
			}
			if (piece.pieceType == Constants.PieceType.KNIGHT) {
				return 30 + wKnightValue[position];
			}
			if (piece.pieceType == Constants.PieceType.BISHOP) {
				return 30 + wBishopValue[position];
			}
			if (piece.pieceType == Constants.PieceType.ROOK) {
				return 50 + wRookValue[position];
			}
			if (piece.pieceType == Constants.PieceType.QUEEN) {
				return 100 + wQueenValue[position];
			}
			if (piece.pieceType == Constants.PieceType.KING) {
				return wKingValue[position];
			}
		}
		if (piece.color == Constants.Color.BLACK) {
			if (piece.pieceType == Constants.PieceType.PAWN) {
				return 10 + bPawnValue[position];
			}
			if (piece.pieceType == Constants.PieceType.KNIGHT) {
				return 30 + bKnightValue[position];
			}
			if (piece.pieceType == Constants.PieceType.BISHOP) {
				return 30 + bBishopValue[position];
			}
			if (piece.pieceType == Constants.PieceType.ROOK) {
				return 50 + bRookValue[position];
			}
			if (piece.pieceType == Constants.PieceType.QUEEN) {
				return 100 + bQueenValue[position];
			}
			if (piece.pieceType == Constants.PieceType.KING) {
				return bKingValue[position];
			}
		}
		return 0;
    }

}
