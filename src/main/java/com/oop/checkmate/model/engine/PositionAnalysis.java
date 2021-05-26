package com.oop.checkmate.model.engine;

import java.util.List;

import com.oop.checkmate.Constants;


public class PositionAnalysis {
    public ePosition CurrentPosition;

    public PositionAnalysis(ePosition board){
        CurrentPosition = new ePosition(board.generateFen());
    }

    public int Evaluation(ePosition ePos){
        ePos.generateLegalMoves();
        boolean checkmate = false;
        List <Move> legal = ePos.generateLegalMoves();
        if(legal.isEmpty()){
            checkmate = true;
        }
        if(checkmate){
            return -200;
        }
        int player = 0;
        int opponent = 0;
        for(int i=0 ; i <64; i++){
            EngineConstants.ePiece piece= ePos.board[i];
            if(piece == EngineConstants.ePiece.NO_PIECE){
                continue;
            }
            if(piece.color == ePos.sideToMove){
                player+=PieceValues(piece);
            }
            else{
                opponent+=PieceValues(piece);
            }
        }
        return player - opponent;
    }
    int PieceValues(EngineConstants.ePiece piece){
        if(piece.pieceType == Constants.PieceType.PAWN){
            return 1;
        }
        if(piece.pieceType == Constants.PieceType.KNIGHT){
            return 3;
        }
        if(piece.pieceType == Constants.PieceType.BISHOP){
            return 3;
        }
        if(piece.pieceType == Constants.PieceType.ROOK){
            return 5;
        }
        if(piece.pieceType == Constants.PieceType.QUEEN){
            return 10;
        }
        return 0;
    }
    public Move Analysis(ePosition ePos){
        List <Move> Candidates = ePos.generateLegalMoves();
        Move BestMove = null;
        int eval = -201;
        for(Move move :Candidates){
            ePos.make_move(move);
            int res = -Analyse(0, ePos);
            if(res > eval){
                System.out.println(res);
                eval = res;
                BestMove = move;
            }
            ePos.undoLastMove();
        }
        return BestMove;
    }
    public int Analyse(int depth, ePosition ePos){
        int eval = -201;
        if(depth == 2){
            return Evaluation(ePos);
        }
        List <Move> Candidates = ePos.generateLegalMoves();
        if(Candidates.isEmpty()){
            return -200;
        }
        for(Move move : Candidates){
            ePos.make_move(move);
            int res = -Analyse(depth+1, ePos);
            if(res >= eval){
                eval = res;
            }
            ePos.undoLastMove();
        }
        return eval;
    }
}
