package abalone.view;

import abalone.model.HexCoordinate;
import java.awt.Point;

public class HexLayout {
    private final int size; // Size of hexagon (center to corner)
    private final int originX;
    private final int originY;

    // Orientation: Pointy Top
    // width = sqrt(3) * size
    // height = 2 * size
    // Horizontal distance between centers = width
    // Vertical distance = 3/2 * size

    // Actually, Abalone board usually visualized as Pointy Top?
    // ConsoleView assumed rows.
    // In Pointy Top:
    // x = size * sqrt(3) * (q + r/2)
    // y = size * 3/2 * r

    public HexLayout(int size, int originX, int originY) {
        this.size = size;
        this.originX = originX;
        this.originY = originY;
    }

    public Point hexToPixel(HexCoordinate h) {
        double x = size * Math.sqrt(3) * (h.q() + h.r() / 2.0);
        double y = size * 3.0 / 2.0 * h.r();
        return new Point((int) (x + originX), (int) (y + originY));
    }

    public HexCoordinate pixelToHex(int x, int y) {
        x -= originX;
        y -= originY;

        double q = (Math.sqrt(3) / 3 * x - 1.0 / 3 * y) / size;
        double r = (2.0 / 3 * y) / size;

        return axialRound(q, r);
    }

    private HexCoordinate axialRound(double q, double r) {
        double x = q;
        double z = r;
        double y = -x - z;

        int rx = (int) Math.round(x);
        int ry = (int) Math.round(y);
        int rz = (int) Math.round(z);

        double x_diff = Math.abs(rx - x);
        double y_diff = Math.abs(ry - y);
        double z_diff = Math.abs(rz - z);

        if (x_diff > y_diff && x_diff > z_diff) {
            rx = -ry - rz;
        } else if (y_diff > z_diff) {
            ry = -rx - rz;
        } else {
            rz = -rx - ry;
        }

        return new HexCoordinate(rx, rz);
    }

    public int getSize() {
        return size;
    }
}
