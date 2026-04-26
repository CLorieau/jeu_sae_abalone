package abalone.ai;

import abalone.model.Board;
import abalone.model.Color;
import abalone.model.Move;
import abalone.model.Player;

public class AIPlayer extends Player {
    private final Difficulty difficulty;
    private final AlphaBetaStrategy strategy;

    public AIPlayer(String name, Color color, Difficulty difficulty) {
        super(name, color);
        this.difficulty = difficulty;
        this.strategy = new AlphaBetaStrategy(difficulty.getDepth());
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Move chooseMove(Board board) {
        return strategy.chooseMove(board, getColor());
    }
}
