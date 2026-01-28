package abalone.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class Board {
    public static final int RADIUS = 4;
    private final Map<HexCoordinate, Piece> pieces = new HashMap<>();

    public Board() {
        initBoard();
    }

    private void initBoard() {
        pieces.clear();
        createStandardSetup();
    }

    // Standard setup logic separated for clarity
    private void createStandardSetup() {
        // WHITE (Top/North)
        for (int q = 0; q <= 4; q++)
            place(new HexCoordinate(q, -4), Color.WHITE);
        for (int q = -1; q <= 4; q++)
            place(new HexCoordinate(q, -3), Color.WHITE);
        for (int q = 0; q <= 2; q++)
            place(new HexCoordinate(q, -2), Color.WHITE);

        // BLACK (Bottom/South)
        for (int q = -4; q <= 0; q++)
            place(new HexCoordinate(q, 4), Color.BLACK);
        for (int q = -4; q <= 1; q++)
            place(new HexCoordinate(q, 3), Color.BLACK);
        for (int q = -2; q <= 0; q++)
            place(new HexCoordinate(q, 2), Color.BLACK);
    }

    public void place(HexCoordinate coord, Color color) {
        if (isValid(coord)) {
            pieces.put(coord, new Piece(color));
        }
    }

    public Piece getPieceAt(HexCoordinate coord) {
        return pieces.get(coord);
    }

    public void removePieceAt(HexCoordinate coord) {
        pieces.remove(coord);
    }

    /*
     * Check if a coordinate is strictly within the board boundaries.
     */
    public boolean isValid(HexCoordinate coord) {
        int q = coord.q();
        int r = coord.r();
        int s = -q - r;
        return (Math.abs(q) + Math.abs(r) + Math.abs(s)) / 2 <= RADIUS;
    }

    public Map<HexCoordinate, Piece> getPieces() {
        return new HashMap<>(pieces);
    }

    // --- Move Logic ---

    private int blackLost = 0;
    private int whiteLost = 0;

    public int getBlackLost() {
        return blackLost;
    }

    public int getWhiteLost() {
        return whiteLost;
    }

    public void executeMove(Move move) throws IllegalArgumentException {
        // We assume validateMove is called before, but we can re-check or assume
        // safety.
        // For CLI, we should probably validate here to be safe.
        // But validateMove requires 'playerColor', here we can derive it from the
        // marble.
        if (move.getMarbles().isEmpty())
            return;

        Piece first = getPieceAt(move.getMarbles().get(0));
        if (first == null)
            throw new IllegalArgumentException("No piece at start position.");
        Color color = first.getColor();

        if (!validateMove(move, color)) {
            throw new IllegalArgumentException("Invalid move.");
        }

        // Execute
        HexCoordinate dir = move.getDirection();

        if (isBroadside(move)) {
            // Broadside: simple shift of all marbles
            // We must remove all old positions first to avoid self-collision issues (though
            // broadside never collides self)
            // But conceptually, we pick them up and place them down.
            // Map updates:
            // Since broadside to empty spots, we can just move them.
            // We need to keep the object or just create new ones.
            List<Piece> movingPieces = new ArrayList<>();
            for (HexCoordinate c : move.getMarbles()) {
                movingPieces.add(pieces.remove(c));
            }
            for (int i = 0; i < move.getMarbles().size(); i++) {
                HexCoordinate dest = move.getMarbles().get(i).add(dir);
                pieces.put(dest, movingPieces.get(i));
            }
        } else {
            // Inline
            // 1. Identify Head (leading marble)
            List<HexCoordinate> sorted = sortMarblesAlongDirection(move.getMarbles(), dir);
            HexCoordinate head = sorted.get(sorted.size() - 1); // Last one is head in direction?
            // Wait, sort logic depends on direction.
            // Let's implement a specific sorter or just find the one where head + dir is
            // NOT in marbles.

            // Actually, if we sort by projection onto direction...
            // Simple check: The one that has a neighbor in 'dir' direction that is NOT in
            // the group is the head.

            // Check if pushing
            HexCoordinate target = head.add(dir);
            Piece targetPiece = getPieceAt(target);

            if (targetPiece == null) {
                // Simple move into empty space
                // Move each marble 1 step.
                // Order matters if we update map in place?
                // Remove all, then put all.
                List<Piece> movingPieces = new ArrayList<>();
                for (HexCoordinate c : move.getMarbles()) {
                    movingPieces.add(pieces.remove(c));
                }
                for (int i = 0; i < move.getMarbles().size(); i++) {
                    pieces.put(move.getMarbles().get(i).add(dir), movingPieces.get(i));
                }
            } else if (targetPiece.getColor() != color) {
                // Sumito Push
                // 1. Find line of opponents
                List<HexCoordinate> opponents = new ArrayList<>();
                HexCoordinate current = target;
                while (true) {
                    Piece p = getPieceAt(current);
                    if (p == null || p.getColor() == color)
                        break;
                    opponents.add(current);
                    current = current.add(dir);
                }

                // 2. Shift opponents (back to front to avoid overwrite)
                // The last opponent moves to 'current' (which is empty or off-board).
                // If 'current' is off-board, piece dies.
                // If 'current' is on-board, put piece there.

                // Remove all opponents
                List<Piece> oppPieces = new ArrayList<>();
                for (HexCoordinate c : opponents) {
                    oppPieces.add(pieces.remove(c));
                }

                // Place opponents shifted (except ejected ones)
                for (int i = 0; i < opponents.size(); i++) {
                    HexCoordinate dest = opponents.get(i).add(dir);
                    if (isValid(dest)) {
                        pieces.put(dest, oppPieces.get(i));
                    } else {
                        // Ejected!
                        Piece lostPiece = oppPieces.get(i);
                        if (lostPiece.getColor() == Color.WHITE) {
                            whiteLost++;
                        } else {
                            blackLost++;
                        }
                    }
                }

                // 3. Move own pieces
                List<Piece> myPieces = new ArrayList<>();
                for (HexCoordinate c : move.getMarbles()) {
                    myPieces.add(pieces.remove(c));
                }
                for (int i = 0; i < move.getMarbles().size(); i++) {
                    pieces.put(move.getMarbles().get(i).add(dir), myPieces.get(i));
                }
            }
        }
    }

    public boolean validateMove(Move move, Color playerColor) {
        if (move.getMarbles().isEmpty())
            return false;

        // 1. Ownership & Existence
        for (HexCoordinate c : move.getMarbles()) {
            Piece p = getPieceAt(c);
            if (p == null || p.getColor() != playerColor)
                return false;
        }

        if (!move.isLinear())
            return false; // Should be checked by constructor but good to be safe

        HexCoordinate dir = move.getDirection();

        if (isBroadside(move)) {
            // Broadside
            for (HexCoordinate c : move.getMarbles()) {
                HexCoordinate dest = c.add(dir);
                // Dest must be empty and VALID (on board)
                // "You cannot move a marble off the board" - standard rule?
                // Usually you can't suicide.
                if (!isValid(dest))
                    return false;
                if (getPieceAt(dest) != null)
                    return false;
            }
            return true;
        } else {
            // Inline
            List<HexCoordinate> sorted = sortMarblesAlongDirection(move.getMarbles(), dir);
            HexCoordinate head = sorted.get(sorted.size() - 1);
            HexCoordinate target = head.add(dir);

            if (!isValid(target)) {
                // Trying to move off board?
                // Self-ejection is typically forbidden.
                return false;
            }

            Piece targetPiece = getPieceAt(target);
            if (targetPiece == null) {
                return true; // Simple move
            }

            if (targetPiece.getColor() == playerColor) {
                return false; // Cannot push own
            }

            // Sumito Check
            // Count opponents
            int pushPower = move.getMarbles().size();
            int opponentCount = 0;
            HexCoordinate current = target;

            while (true) {
                Piece p = getPieceAt(current);
                if (p == null) {
                    // Empty spot found behind opponents
                    // Valid push!
                    break;
                }
                if (p.getColor() == playerColor) {
                    return false; // Blocked by own piece
                }
                opponentCount++;
                current = current.add(dir);

                // Check if valid coord
                if (!isValid(current)) {
                    // Pushing off board!
                    // Valid if we have advantage.
                    break;
                }
            }

            return (pushPower > opponentCount);
        }
    }

    private boolean isBroadside(Move move) {
        if (move.getMarbles().size() <= 1)
            return false; // Single marble is always inline-like
        HexCoordinate first = move.getMarbles().get(0);
        HexCoordinate second = move.getMarbles().get(1);

        // Line direction (axial difference)
        int dq = second.q() - first.q();
        int dr = second.r() - first.r();

        // Normalize line vector is hard with loose ints.
        // But if dir equals line vector, it's inline.
        // Line vector can be (1,0), (-1,0), (0,1), (0,-1), (1,-1), (-1,1)
        // Check if move direction matches line direction (or opposite)
        HexCoordinate dir = move.getDirection();

        // Simple check: if dir is parallel to line connecting 0 and 1.
        // Parallel means cross product is 0? Or just compare.
        // (dq, dr) vs (dir.q, dir.r)
        // parallel if dq*dir.r == dr*dir.q?
        return (dq * dir.r() != dr * dir.q());
    }

    // Sort marbles so the last one is the "Head" in the direction of movement
    private List<HexCoordinate> sortMarblesAlongDirection(List<HexCoordinate> marbles, HexCoordinate dir) {
        List<HexCoordinate> sorted = new ArrayList<>(marbles);
        // Project onto direction vector?
        // q*dir.q + r*dir.r ? Not quite for Hex.
        // For hex (q,r), projection logic:
        // A generic way: sort by (q*dir.q + r*dir.r + s*dir.s)?
        // Let's just try all permutations or simple dot product equivalent.
        // In cubic (x,y,z): dot product is reliable.
        // x=q, z=r, y=-q-r.
        // dot = c.x*d.x + c.y*d.y + c.z*d.z
        sorted.sort((c1, c2) -> {
            int dot1 = c1.q() * dir.q() + c1.r() * dir.r() + (-c1.q() - c1.r()) * (-dir.q() - dir.r());
            int dot2 = c2.q() * dir.q() + c2.r() * dir.r() + (-c2.q() - c2.r()) * (-dir.q() - dir.r());
            return Integer.compare(dot1, dot2);
        });
        return sorted;
    }
}
