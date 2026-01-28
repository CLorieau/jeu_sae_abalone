package abalone.view;

import abalone.controller.GuiController;
import abalone.model.Board;
import abalone.model.Color;
import abalone.model.HexCoordinate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class GamePanel extends JPanel {
    private final Board board;
    private final GuiController controller;
    private final HexLayout layout;

    public GamePanel(Board board, GuiController controller) {
        this.board = board;
        this.controller = controller;
        this.layout = new HexLayout(30, 400, 300); // Size 30, Centered roughly 800x600?

        setBackground(new java.awt.Color(34, 139, 34)); // Forest Green

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                HexCoordinate h = layout.pixelToHex(e.getX(), e.getY());
                controller.handleHashClick(h);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw Board Grid (All valid spots)
        // Range q: -4 to 4, r: -4 to 4
        g2.setColor(new java.awt.Color(0, 100, 0)); // Darker green holes
        for (int q = -4; q <= 4; q++) {
            for (int r = -4; r <= 4; r++) {
                HexCoordinate h = new HexCoordinate(q, r);
                if (board.isValid(h)) {
                    Point p = layout.hexToPixel(h);
                    drawHex(g2, p.x, p.y, layout.getSize(), false);
                }
            }
        }

        // Draw Pieces
        board.getPieces().forEach((coord, piece) -> {
            Point p = layout.hexToPixel(coord);
            drawMarble(g2, p.x, p.y, layout.getSize() - 5, piece.getColor());
        });

        // Draw Selection
        List<HexCoordinate> selected = controller.getSelectedMarbles();
        g2.setColor(java.awt.Color.YELLOW);
        g2.setStroke(new BasicStroke(3));
        for (HexCoordinate h : selected) {
            Point p = layout.hexToPixel(h);
            g2.drawOval(p.x - (layout.getSize() - 5), p.y - (layout.getSize() - 5), (layout.getSize() - 5) * 2,
                    (layout.getSize() - 5) * 2);
        }

        // Draw Message
        g2.setColor(java.awt.Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString(controller.getMessage(), 20, 30);
    }

    private void drawHex(Graphics2D g2, int x, int y, int size, boolean fill) {
        // Just draw a circle hole for simplicity? Or actual hex?
        // Hexagon polygon
        // But Abalone board has circular sockets.
        // Let's draw holes.
        int r = size - 2;
        g2.fillOval(x - r, y - r, r * 2, r * 2);
    }

    private void drawMarble(Graphics2D g2, int x, int y, int radius, Color color) {
        if (color == Color.BLACK) {
            g2.setColor(java.awt.Color.BLACK);
        } else {
            g2.setColor(java.awt.Color.WHITE);
        }
        g2.fillOval(x - radius, y - radius, radius * 2, radius * 2);

        // Gloss/Shine for 3D effect
        g2.setColor(new java.awt.Color(255, 255, 255, 100));
        g2.fillOval(x - radius / 2, y - radius / 2, radius / 2, radius / 2);
    }
}
