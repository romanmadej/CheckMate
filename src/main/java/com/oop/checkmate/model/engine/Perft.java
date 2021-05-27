package com.oop.checkmate.model.engine;

import java.util.List;

class MoveHistory {
    Move move;
    MoveHistory prev;

    MoveHistory(Move move, MoveHistory prev) {
        this.move = move;
        this.prev = prev;
    }

    private static String traverse(MoveHistory mh) {
        String s = mh.move.getFromSquare().name().toLowerCase() + mh.move.getToSquare().name().toLowerCase() + (mh.move.isPromotion() ? mh.move.getPromotionTypechar() : "");
        if (mh.prev == null) return s;
        return traverse(mh.prev) + "  " + s;

    }

    @Override
    public String toString() {
        return (traverse(this));
    }

}


public class Perft {
    static long captures = 0, checks = 0;

    static long dfs(BoardState pos, int depth, int maxDepth, MoveHistory mh) {
        if (depth == maxDepth && pos.checkers != 0)
            checks++;
        if (depth == maxDepth) {
            return 1;
        }
        long nodes = 0;
        List<Move> legalMoves = pos.generateLegalMoves();
        if (depth == maxDepth - 1) {
            nodes += legalMoves.size();
            return nodes;
        }
        for (Move move : legalMoves) {
            if (depth == maxDepth - 1 && move.isCapture() || move.isEpCapture())
                captures++;
            MoveHistory nmh = new MoveHistory(move, mh);

            pos.makeMove(move);
            nodes += dfs(pos, depth + 1, maxDepth, nmh);
            pos.undoLastMove();

        }
        if (depth == 1)
            System.out.println(mh.move.getFromSquare().name().toLowerCase() + mh.move.getToSquare().name().toLowerCase() + ": " + nodes);
        return nodes;
    }
}
