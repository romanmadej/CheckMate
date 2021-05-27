package com.oop.checkmate.model.engine;

import static com.oop.checkmate.model.engine.EngineConstants.*;

import java.util.List;

import com.oop.checkmate.Constants;
import com.oop.checkmate.model.Piece;


public class PositionAnalysis {
    public BoardState CurrentPosition;

    public PositionAnalysis(BoardState board){
        CurrentPosition = new BoardState(board.generateFen());
    }


    public Move Analysis(BoardState boardState){
        List <Move> Candidates = boardState.generateLegalMoves();
        Move BestMove = null;
		double eval = -2010;
        for(Move move :Candidates){
            boardState.makeMove(move);
			double res = -Analyse(0, boardState);
            if(res > eval){
                System.out.println(res);
                eval = res;
                BestMove = move;
            }
            boardState.undoLastMove();
        }
        return BestMove;
    }

	public double Analyse(int depth, BoardState boardState) {
		double eval = -2010;
        if(depth == 2){
            return Evaluation(boardState);
        }
        List <Move> Candidates = boardState.generateLegalMoves();
        if(Candidates.isEmpty()){
			return -2000;
        }
        for(Move move : Candidates){
            boardState.makeMove(move);
			double res = -Analyse(depth + 1, boardState);
            if(res >= eval){
                eval = res;
            }
            boardState.undoLastMove();
        }
        return eval;
	}

	public double Evaluation(BoardState boardState) {
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
			if (piece.color == boardState.sideToMove) {
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

	double PositionValues(int position, Constants.PieceType type) {
		return 0;
	}
}
