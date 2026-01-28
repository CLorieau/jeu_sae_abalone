package abalone.view;

import abalone.controller.GuiController;
import abalone.model.Board;
import javax.swing.*;

public class GameWindow extends JFrame {
    public GameWindow() {
        super("Abalone");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Board board = new Board();
        GuiController controller = new GuiController(board);
        GamePanel panel = new GamePanel(board, controller);

        add(panel);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
