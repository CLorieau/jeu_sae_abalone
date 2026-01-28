package abalone.controller;

import abalone.model.Board;
import abalone.model.Color;
import abalone.model.Direction;
import abalone.model.HexCoordinate;
import abalone.model.Move;
import abalone.model.Piece;

import java.util.ArrayList;
import java.util.List;

public class GuiController {
    private final Board board;
    private Color currentTurn;
    private final List<HexCoordinate> selectedMarbles;
    private String message;

    public GuiController(Board board) {
        this.board = board;
        this.currentTurn = Color.BLACK;
        this.selectedMarbles = new ArrayList<>();
        this.message = "Turn: " + currentTurn;
    }

    public Color getCurrentTurn() {
        return currentTurn;
    }

    public String getMessage() {
        return message;
    }

    public List<HexCoordinate> getSelectedMarbles() {
        return new ArrayList<>(selectedMarbles);
    }

    public void handleHashClick(HexCoordinate coord) {
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
    }

    private boolean checkWin() {
        if (board.getWhiteLost() >= 6) {
            updateMessage("GAME OVER! BLACK WINS! (White lost 6)");
            return true;
        }
        if (board.getBlackLost() >= 6) {
            updateMessage("GAME OVER! WHITE WINS! (Black lost 6)");
            return true;
        }
        return false;
    }

    private void updateStatus() {
        message = String.format("Turn: %s | Lost: W=%d B=%d",
                currentTurn, board.getWhiteLost(), board.getBlackLost());
    }

    private void updateMessage(String msg) {
        this.message = msg;
    }
}
