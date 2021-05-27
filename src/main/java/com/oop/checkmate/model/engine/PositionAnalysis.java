package com.oop.checkmate.model.engine;

import static com.oop.checkmate.model.engine.EngineConstants.*;

import java.util.List;

import com.oop.checkmate.Constants;
import com.oop.checkmate.model.ePiece;


public class PositionAnalysis {
    public ePosition CurrentPosition;

    public PositionAnalysis(ePosition board){
        CurrentPosition = new ePosition(board.generateFen());
    }


    public Move Analysis(ePosition ePos){
        List <Move> Candidates = ePos.generateLegalMoves();
        Move BestMove = null;
		double eval = -2010;
        for(Move move :Candidates){
            ePos.make_move(move);
			double res = -Analyse(0, ePos);
            if(res > eval){
                System.out.println(res);
                eval = res;
                BestMove = move;
            }
            ePos.undoLastMove();
        }
        return BestMove;
    }

	public double Analyse(int depth, ePosition ePos) {
		double eval = -2010;
        if(depth == 2){
            return Evaluation(ePos);
        }
        List <Move> Candidates = ePos.generateLegalMoves();
        if(Candidates.isEmpty()){
			return -2000;
        }
        for(Move move : Candidates){
            ePos.make_move(move);
			double res = -Analyse(depth + 1, ePos);
            if(res >= eval){
                eval = res;
            }
            ePos.undoLastMove();
        }
        return eval;
	}

	public double Evaluation(ePosition ePos) {
		ePos.generateLegalMoves();
		boolean checkmate = false;
		List<Move> legal = ePos.generateLegalMoves();
		if (legal.isEmpty()) {
			checkmate = true;
		}
		if (checkmate) {
			return -2000;
		}
		double player = 0;
		double opponent = 0;
		for (int i = 0; i < 64; i++) {
			ePiece piece = ePos.board[i];
			if (piece == ePiece.NO_PIECE) {
				continue;
			}
			if (piece.color == ePos.sideToMove) {
				player += PieceValues(piece, i);
			} else {
				opponent += PieceValues(piece, i);
			}
		}
		return player - opponent;
	}

	double PieceValues(ePiece piece, int position) {
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
