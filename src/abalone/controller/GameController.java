package abalone.controller;

import abalone.model.Board;
import abalone.model.Color;
import abalone.model.Move;
import abalone.view.ConsoleView;

import java.util.Scanner;

public class GameController {
    private final Board board;
    private final ConsoleView view;
    private Color currentTurn;
    private boolean running;

    public GameController(Board board, ConsoleView view) {
        this.board = board;
        this.view = view;
        this.currentTurn = Color.BLACK; // Black starts usually
        this.running = true;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (running) {
            view.printBoard(board);
            Move move = view.getUserMove(scanner, currentTurn);

            if (move == null) {
                view.displayMessage("Invalid input, try again.");
                continue;
            }

            try {
                // Pre-validation (logic is in Board, but exceptions catch meaningful errors)
                // Board.executeMove checks validateMove internally or we call it explicitly.
                // Our executeMove throws IllegalArgumentException if invalid.
                board.executeMove(move);

                // Check win condition (optional for basic loop)
                // Need to count pieces removed?
                // Or just loop.

                switchTurn();
            } catch (IllegalArgumentException e) {
                view.displayMessage("Error: " + e.getMessage());
            } catch (Exception e) {
                view.displayMessage("Unexpected error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void switchTurn() {
        currentTurn = currentTurn.opposite();
    }
}
