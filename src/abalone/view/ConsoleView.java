package abalone.view;

import abalone.model.Board;
import abalone.model.Color;
import abalone.model.HexCoordinate;
import abalone.model.Move;
import abalone.model.Piece;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleView {

    public void printBoard(Board board) {
        System.out.println("  -------------------------");
        // Print row by row. r goes from -4 to 4.
        for (int r = -4; r <= 4; r++) {
            // Indentation
            // As r increases, row shifts?
            // Simulating hex grid:
            // Row -4 (top) is short (5).
            // Row 0 (center) is long (9).
            // Row 4 (bottom) is short (5).
            // We need to shift based on... something.
            // Usually, staggered.
            // Let's print spaces = abs(r). (Wait, Center row is widest, has 9. Top row has
            // 5. Difference 4.)
            // So indent = abs(r) (half-spaces?).
            StringBuilder sb = new StringBuilder();
            int indent = Math.abs(r);
            for (int i = 0; i < indent; i++)
                sb.append(" ");

            // Loop q.
            // Valid q depends on r.
            // We can iterate all q and check isValid.
            // Or use known bounds.
            int minQ = Math.max(-4, -4 - r);
            int maxQ = Math.min(4, 4 - r);

            for (int q = minQ; q <= maxQ; q++) {
                HexCoordinate h = new HexCoordinate(q, r);
                Piece p = board.getPieceAt(h);
                if (p == null) {
                    sb.append(" . ");
                } else {
                    sb.append(p.getColor() == Color.WHITE ? " W " : " B ");
                }
            }
            System.out.println(sb.toString());
        }
        System.out.println("  -------------------------");
    }

    public Move getUserMove(Scanner scanner, Color turn) {
        System.out.println("Player " + turn + ", enter move: (count q1 r1 ... qN rN directionIndex 0-5)");
        System.out.println("Example: 1 0 -4 0 (Select 1 marble at 0,-4 and move dir 0)");

        // Very simple parser for now.
        // N q1 r1 q2 r2 ... dir
        // Or just prompt for count.
        try {
            System.out.print("Number of marbles (1-3): ");
            int count = scanner.nextInt();
            List<HexCoordinate> marbles = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                System.out.print("Marble " + (i + 1) + " q: ");
                int q = scanner.nextInt();
                System.out.print("Marble " + (i + 1) + " r: ");
                int r = scanner.nextInt();
                marbles.add(new HexCoordinate(q, r));
            }
            System.out.print("Direction (0-5, 0=TL, 1=TR, 2=R, 3=BR, 4=BL, 5=L): ");
            int dirIdx = scanner.nextInt();

            // Validate dirIdx
            if (dirIdx < 0 || dirIdx > 5)
                return null;

            return new Move(marbles, HexCoordinate.DIRECTIONS[dirIdx]);
        } catch (Exception e) {
            scanner.nextLine(); // Clear buffer
            System.out.println("Invalid input format.");
            return null;
        }
    }

    public void displayMessage(String msg) {
        System.out.println(msg);
    }
}
