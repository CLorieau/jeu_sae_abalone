package abalone.controller;

import abalone.ai.AIPlayer;
import abalone.model.Board;
import abalone.model.Color;
import abalone.model.Direction;
import abalone.model.HexCoordinate;
import abalone.model.Move;
import abalone.model.Piece;
import abalone.model.Player;

import javax.swing.SwingWorker;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GuiController {
    private final Board board;
    private Color currentTurn;
    private final List<HexCoordinate> selectedMarbles;
    private String message;
    private Runnable onGameEnd;
    private Runnable onUpdate;
    private AIPlayer aiPlayer;
    private boolean aiThinking = false;

    public GuiController(Board board) {
        this.board = board;
        this.currentTurn = board.getCurrentTurn();
        this.selectedMarbles = new ArrayList<>();
        this.message = "Turn: " + currentTurn;
    }

    public Color getCurrentTurn() {
        return currentTurn;
    }

    public String getMessage() {
        return message;
    }

    public void setOnGameEnd(Runnable onGameEnd) {
        this.onGameEnd = onGameEnd;
    }

    public void setOnUpdate(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }

    public void setPlayerNames(String blackName, String whiteName) {
        board.setPlayers(new Player(blackName, Color.BLACK), new Player(whiteName, Color.WHITE));
        updateStatus();
    }

    public void setPlayers(Player black, Player white) {
        board.setPlayers(black, white);
        if (black instanceof AIPlayer) {
            this.aiPlayer = (AIPlayer) black;
        } else if (white instanceof AIPlayer) {
            this.aiPlayer = (AIPlayer) white;
        }
        updateStatus();
        // If AI plays first, kick it off.
        maybeTriggerAI();
    }

    public boolean isAITurn() {
        return aiPlayer != null && aiPlayer.getColor() == currentTurn;
    }

    public List<HexCoordinate> getSelectedMarbles() {
        return new ArrayList<>(selectedMarbles);
    }

    /**
     * Returns the set of cells the player can click to commit a legal move with
     * the current selection. Cells already occupied by the selection itself are
     * filtered out (they are not a useful click target).
     */
    public Set<HexCoordinate> getPossibleDestinations() {
        Set<HexCoordinate> result = new HashSet<>();
        if (selectedMarbles.isEmpty())
            return result;

        Set<HexCoordinate> selSet = new HashSet<>(selectedMarbles);
        for (Move m : board.generateLegalMoves(currentTurn)) {
            if (m.getMarbles().size() != selSet.size())
                continue;
            if (!new HashSet<>(m.getMarbles()).equals(selSet))
                continue;
            HexCoordinate dir = m.getDirection();
            for (HexCoordinate s : selectedMarbles) {
                HexCoordinate dest = s.add(dir);
                if (!selSet.contains(dest))
                    result.add(dest);
            }
        }
        return result;
    }

    public void handleHashClick(HexCoordinate coord) {
        // Block input while it's the AI's turn.
        if (isAITurn() || aiThinking) {
            return;
        }
        if (!board.isValid(coord)) {
            // Clicked outside board? Deselect?
            selectedMarbles.clear();
            updateStatus();
            return;
        }

        Piece p = board.getPieceAt(coord);

        // Strategy:
        // 1. If clicking own marble -> Select/Toggle
        // 2. If valid neighbor of selection -> MOVE
        // 3. Else -> Clear

        if (p != null && p.getColor() == currentTurn) {
            if (selectedMarbles.contains(coord)) {
                selectedMarbles.remove(coord);
            } else {
                if (selectedMarbles.size() < 3) {
                    // Check linearity if adding? Or just allow adding and validate later?
                    // Better to allow picking 3 and then checking?
                    // Let's just add for now.
                    selectedMarbles.add(coord);
                } else {
                    updateStatus(); // Refresh msg to clear error
                }
            }
        } else {
            // Clicked empty or opponent
            // Check if it's a move direction?
            if (!selectedMarbles.isEmpty()) {
                // Determine direction
                // If single marble selected: direction is coord - selected.
                // If multiple: Determine direction based on *one* of them?
                // Usually, we click the destination of the "Head".

                // Let's try to infer direction from *any* selected marble to this coord.
                Direction dir = null;
                for (HexCoordinate s : selectedMarbles) {
                    // Check neighbors
                    for (int i = 0; i < 6; i++) {
                        if (s.neighbor(i).equals(coord)) {
                            // This is a neighbor of s.
                            // Direction is i.
                            dir = Direction.fromHex(HexCoordinate.DIRECTIONS[i]);
                            break;
                        }
                    }
                    if (dir != null)
                        break;
                }

                if (dir != null) {
                    // Try to execute move
                    try {
                        Move move = new Move(selectedMarbles, dir.toHex());
                        // Validate logic is in Broadside check inside Board?
                        // Board.executeMove handles it.

                        board.executeMove(move);
                        selectedMarbles.clear();

                        // Check Win Condition
                        if (checkWin()) {
                            return; // Game Over
                        }

                        switchTurn();
                        updateStatus();
                        maybeTriggerAI();
                    } catch (IllegalArgumentException e) {
                        updateMessage("Invalid Move: " + e.getMessage());
                    } catch (Exception e) {
                        updateMessage("Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    selectedMarbles.clear();
                    updateStatus();
                }
            }
        }
    }

    private void switchTurn() {
        currentTurn = currentTurn.opposite();
        board.setCurrentTurn(currentTurn);
    }

    private boolean checkWin() {
        if (board.getWhiteLost() >= 6) {
            updateMessage("GAME OVER! BLACK WINS! (White lost 6)");
            if (onGameEnd != null)
                onGameEnd.run();
            return true;
        }
        if (board.getBlackLost() >= 6) {
            updateMessage("GAME OVER! WHITE WINS! (Black lost 6)");
            if (onGameEnd != null)
                onGameEnd.run();
            return true;
        }
        return false;
    }

    private void updateStatus() {
        Player p = (currentTurn == Color.BLACK) ? board.getBlackPlayer() : board.getWhitePlayer();
        String currentName = p.getName();
        message = String.format("Au tour de: %s (%s) | Éjectées: B=%d W=%d",
                currentName, currentTurn, board.getBlackLost(), board.getWhiteLost());
    }

    private void updateMessage(String msg) {
        this.message = msg;
    }

    private void maybeTriggerAI() {
        if (!isAITurn() || aiThinking) return;
        aiThinking = true;
        Player p = (currentTurn == Color.BLACK) ? board.getBlackPlayer() : board.getWhitePlayer();
        message = "L'IA (" + p.getName() + ") réfléchit...";
        fireUpdate();

        final AIPlayer ai = aiPlayer;
        new SwingWorker<Move, Void>() {
            @Override
            protected Move doInBackground() {
                return ai.chooseMove(board);
            }

            @Override
            protected void done() {
                aiThinking = false;
                try {
                    Move move = get();
                    if (move == null) {
                        updateMessage("L'IA n'a pas pu trouver de coup.");
                        fireUpdate();
                        return;
                    }
                    board.executeMove(move);
                    if (checkWin()) {
                        fireUpdate();
                        return;
                    }
                    switchTurn();
                    updateStatus();
                    fireUpdate();
                } catch (Exception ex) {
                    updateMessage("Erreur IA: " + ex.getMessage());
                    ex.printStackTrace();
                    fireUpdate();
                }
            }
        }.execute();
    }

    private void fireUpdate() {
        if (onUpdate != null) onUpdate.run();
    }
}
