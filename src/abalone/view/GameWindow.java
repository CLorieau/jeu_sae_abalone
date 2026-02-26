package abalone.view;

import abalone.controller.GuiController;
import abalone.model.Board;
import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    public GameWindow() {
        super("Abalone");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        TitlePanel titlePanel = new TitlePanel(new TitlePanel.Listener() {
            @Override
            public void onStartClicked() {
                startGame();
            }

            @Override
            public void onQuitClicked() {
                System.exit(0);
            }
        });

        mainPanel.add(titlePanel, "TITLE");

        add(mainPanel);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        showTitleScreen();
    }

    public void showTitleScreen() {
        cardLayout.show(mainPanel, "TITLE");
    }

    private void startGame() {
        Board board = new Board();
        GuiController controller = new GuiController(board);
        GamePanel gamePanel = new GamePanel(board, controller);

        controller.setOnGameEnd(() -> {
            Timer timer = new Timer(3000, e -> showTitleScreen());
            timer.setRepeats(false);
            timer.start();
        });

        mainPanel.add(gamePanel, "GAME");
        cardLayout.show(mainPanel, "GAME");
    }
}
