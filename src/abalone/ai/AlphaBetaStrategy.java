package abalone.ai;

import abalone.model.Board;
import abalone.model.Color;
import abalone.model.Move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AlphaBetaStrategy {
    private final int depth;
    private final Random random = new Random();

    public AlphaBetaStrategy(int depth) {
        this.depth = depth;
    }

    public Move chooseMove(Board board, Color aiColor) {
        List<Move> moves = board.generateLegalMoves(aiColor);
        if (moves.isEmpty()) return null;
        Collections.shuffle(moves, random); // randomize among equal-scored moves

        Move best = moves.get(0);
        double bestVal = Double.NEGATIVE_INFINITY;
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;

        for (Move m : moves) {
            Board next = board.copy();
            next.executeMove(m);
            next.setCurrentTurn(aiColor.opposite());
            double v = minimax(next, depth - 1, alpha, beta, false, aiColor);
            if (v > bestVal) {
                bestVal = v;
                best = m;
            }
            if (bestVal > alpha) alpha = bestVal;
        }
        return best;
    }

    private double minimax(Board board, int depth, double alpha, double beta,
                           boolean maximizing, Color aiColor) {
        if (depth == 0 || board.getBlackLost() >= 6 || board.getWhiteLost() >= 6) {
            return Heuristic.evaluate(board, aiColor);
        }

        Color toPlay = board.getCurrentTurn();
        List<Move> moves = board.generateLegalMoves(toPlay);
        if (moves.isEmpty()) {
            return Heuristic.evaluate(board, aiColor);
        }

        if (maximizing) {
            double best = Double.NEGATIVE_INFINITY;
            for (Move m : orderedMoves(moves)) {
                Board next = board.copy();
                next.executeMove(m);
                next.setCurrentTurn(toPlay.opposite());
                double v = minimax(next, depth - 1, alpha, beta, false, aiColor);
                if (v > best) best = v;
                if (best > alpha) alpha = best;
                if (alpha >= beta) break;
            }
            return best;
        } else {
            double best = Double.POSITIVE_INFINITY;
            for (Move m : orderedMoves(moves)) {
                Board next = board.copy();
                next.executeMove(m);
                next.setCurrentTurn(toPlay.opposite());
                double v = minimax(next, depth - 1, alpha, beta, true, aiColor);
                if (v < best) best = v;
                if (best < beta) beta = best;
                if (alpha >= beta) break;
            }
            return best;
        }
    }

    /**
     * Try larger groups first - they tend to lead to pushes/captures, which
     * improves alpha-beta pruning by surfacing strong moves early.
     */
    private List<Move> orderedMoves(List<Move> moves) {
        List<Move> ordered = new ArrayList<>(moves);
        ordered.sort((a, b) -> Integer.compare(b.getMarbles().size(), a.getMarbles().size()));
        return ordered;
    }
}
