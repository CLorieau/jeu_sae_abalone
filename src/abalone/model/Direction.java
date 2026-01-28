package abalone.model;

/**
 * Common logic for Abalone rules.
 */
public enum Direction {
    TOP_LEFT(0, -1),
    TOP_RIGHT(1, -1),
    RIGHT(1, 0),
    BOTTOM_RIGHT(0, 1),
    BOTTOM_LEFT(-1, 1),
    LEFT(-1, 0);

    public final int dq;
    public final int dr;

    Direction(int dq, int dr) {
        this.dq = dq;
        this.dr = dr;
    }

    public HexCoordinate toHex() {
        return new HexCoordinate(dq, dr);
    }

    public static Direction fromHex(HexCoordinate h) {
        for (Direction d : values()) {
            if (d.dq == h.q() && d.dr == h.r())
                return d;
        }
        return null;
    }
}
