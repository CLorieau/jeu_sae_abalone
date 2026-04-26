package abalone.ai;

import abalone.model.Board;
import abalone.model.Color;
import abalone.model.HexCoordinate;
import abalone.model.Piece;

import java.util.Map;

public final class Heuristic {
    // Win = 6 ejections. Make captures dwarf positional terms.
    private static final double EJECTION_WEIGHT = 1000.0;
    private static final double CENTER_WEIGHT = 4.0;
    private static final double COHESION_WEIGHT = 2.0;
    private static final double WIN_SCORE = 1_000_000.0;

    private Heuristic() {}

    public static double evaluate(Board board, Color me) {
        Color opp = me.opposite();
        int myLost = (me == Color.BLACK) ? board.getBlackLost() : board.getWhiteLost();
        int oppLost = (me == Color.BLACK) ? board.getWhiteLost() : board.getBlackLost();

        if (oppLost >= 6) return WIN_SCORE;
        if (myLost >= 6) return -WIN_SCORE;

        double score = EJECTION_WEIGHT * (oppLost - myLost);

        Map<HexCoordinate, Piece> pieces = board.getPieces();
        for (Map.Entry<HexCoordinate, Piece> e : pieces.entrySet()) {
            HexCoordinate c = e.getKey();
            Color color = e.getValue().getColor();
            int centerProximity = Board.RADIUS - distanceFromCenter(c);
            int cohesion = countFriendlyNeighbors(pieces, c, color);

            double pieceScore = CENTER_WEIGHT * centerProximity + COHESION_WEIGHT * cohesion;
            if (color == me) {
                score += pieceScore;
            } else {
                score -= pieceScore;
            }
        }
        return score;
    }

    private static int distanceFromCenter(HexCoordinate c) {
        return (Math.abs(c.q()) + Math.abs(c.r()) + Math.abs(c.q() + c.r())) / 2;
    }

    private static int countFriendlyNeighbors(Map<HexCoordinate, Piece> pieces, HexCoordinate c, Color color) {
        int count = 0;
        for (int i = 0; i < 6; i++) {
            Piece n = pieces.get(c.neighbor(i));
            if (n != null && n.getColor() == color) count++;
        }
        return count;
    }
}
