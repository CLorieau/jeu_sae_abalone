package abalone.model;

import java.util.Objects;

public class HexCoordinate {
    private final int q; // column
    private final int r; // row

    public HexCoordinate(int q, int r) {
        this.q = q;
        this.r = r; // axial coordinates
    }

    public int q() {
        return q;
    }

    public int r() {
        return r;
    }
    
    // Convert to Cubic coordinates for some calculations
    public int x() { return q; }
    public int z() { return r; }
    public int y() { return -x() - z(); }

    public HexCoordinate add(HexCoordinate other) {
        return new HexCoordinate(this.q + other.q, this.r + other.r);
    }
    
    // 6 directions in axial coordinates
    // "Top Left", "Top Right", "Right", "Bottom Right", "Bottom Left", "Left"
    // (0, -1), (1, -1), (1, 0), (0, 1), (-1, 1), (-1, 0)
    public static final HexCoordinate[] DIRECTIONS = {
        new HexCoordinate(0, -1), new HexCoordinate(1, -1), new HexCoordinate(1, 0),
        new HexCoordinate(0, 1),  new HexCoordinate(-1, 1), new HexCoordinate(-1, 0)
    };

    public HexCoordinate neighbor(int directionIndex) {
        return this.add(DIRECTIONS[directionIndex % 6]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HexCoordinate that = (HexCoordinate) o;
        return q == that.q && r == that.r;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r);
    }

    @Override
    public String toString() {
        // Convert to something like A1? Or just keep raw initially.
        // Let's keep raw for debug, formatted will be in View
        return String.format("(%d, %d)", q, r);
    }
}
