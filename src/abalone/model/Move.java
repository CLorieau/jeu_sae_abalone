package abalone.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Move {
    private final List<HexCoordinate> marbles;
    private final HexCoordinate direction;

    public Move(List<HexCoordinate> marbles, HexCoordinate direction) {
        if (marbles == null || marbles.isEmpty() || marbles.size() > 3) {
            throw new IllegalArgumentException("Move must involve 1 to 3 marbles.");
        }
        this.marbles = new ArrayList<>(marbles);
        // Sort to ensure consistency (e.g. for finding head/tail)
        // Sort by q then r
        this.marbles.sort(Comparator.comparingInt(HexCoordinate::q).thenComparingInt(HexCoordinate::r));
        this.direction = direction;
    }

    public List<HexCoordinate> getMarbles() {
        return Collections.unmodifiableList(marbles);
    }

    public HexCoordinate getDirection() {
        return direction;
    }

    // Helper to check if marbles are linear and connected
    public boolean isLinear() {
        if (marbles.size() <= 1)
            return true;

        // Check if all align on q, r, or s
        boolean sameQ = marbles.stream().allMatch(m -> m.q() == marbles.get(0).q());
        boolean sameR = marbles.stream().allMatch(m -> m.r() == marbles.get(0).r());
        boolean sameS = marbles.stream().allMatch(m -> (m.q() + m.r()) == (marbles.get(0).q() + marbles.get(0).r())); // -s
                                                                                                                      // constant
                                                                                                                      // implies
                                                                                                                      // x+y
                                                                                                                      // constant

        if (!sameQ && !sameR && !sameS)
            return false;

        // Check adjacency (already sorted)
        // If sorted, distance between adjacent elements in list must be 1.
        for (int i = 0; i < marbles.size() - 1; i++) {
            HexCoordinate c1 = marbles.get(i);
            HexCoordinate c2 = marbles.get(i + 1);
            if (c1.add(c2.add(new HexCoordinate(-c1.q(), -c1.r()))).q() != 0) {
                // Simple distance check might be better
                int dist = (Math.abs(c1.q() - c2.q()) + Math.abs(c1.r() - c2.r())
                        + Math.abs(c1.q() + c1.r() - (c2.q() + c2.r()))) / 2;
                if (dist != 1)
                    return false;
            }
        }
        return true;
    }
}
