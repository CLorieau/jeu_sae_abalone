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
                showPlayerSetup();
            }

            @Override
            public void onQuitClicked() {
                System.exit(0);
            }
        });

        PlayerSetupPanel setupPanel = new PlayerSetupPanel(new PlayerSetupPanel.Listener() {
            @Override
            public void onStartGame(String p1Name, abalone.model.Color p1Color, String p2Name,
                    abalone.model.Color p2Color) {
                startGame(p1Name, p1Color, p2Name, p2Color);
            }

            @Override
            public void onBackToTitle() {
                showTitleScreen();
            }
        });

        mainPanel.add(titlePanel, "TITLE");
        mainPanel.add(setupPanel, "SETUP");

        add(mainPanel);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        showTitleScreen();
    }

    public void showTitleScreen() {
        cardLayout.show(mainPanel, "TITLE");
    }

    public void showPlayerSetup() {
        cardLayout.show(mainPanel, "SETUP");
    }

    private void startGame(String p1Name, abalone.model.Color p1Color, String p2Name, abalone.model.Color p2Color) {
        Board board = new Board();
        GuiController controller = new GuiController(board);

        // Configure players based on color selection
        if (p1Color == abalone.model.Color.BLACK) {
            controller.setPlayerNames(p1Name, p2Name);
        } else {
            controller.setPlayerNames(p2Name, p1Name);
        }

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
