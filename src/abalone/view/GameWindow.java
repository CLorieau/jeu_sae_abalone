package abalone.view;

import abalone.ai.AIPlayer;
import abalone.ai.Difficulty;
import abalone.controller.GuiController;
import abalone.model.Board;
import abalone.model.Player;
import abalone.model.SaveService;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

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
            public void onLoadClicked() {
                loadGame();
            }

            @Override
            public void onQuitClicked() {
                System.exit(0);
            }
        });

        PlayerSetupPanel setupPanel = new PlayerSetupPanel(new PlayerSetupPanel.Listener() {
            @Override
            public void onStartGame(String p1Name, abalone.model.Color p1Color, String p2Name,
                    abalone.model.Color p2Color, boolean p2IsAI, Difficulty difficulty) {
                startGame(p1Name, p1Color, p2Name, p2Color, p2IsAI, difficulty);
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

    private void startGame(String p1Name, abalone.model.Color p1Color, String p2Name, abalone.model.Color p2Color,
            boolean p2IsAI, Difficulty difficulty) {
        Board board = new Board();
        GuiController controller = new GuiController(board);

        Player p1 = new Player(p1Name, p1Color);
        Player p2 = p2IsAI
                ? new AIPlayer(p2Name, p2Color, difficulty)
                : new Player(p2Name, p2Color);

        Player black = (p1Color == abalone.model.Color.BLACK) ? p1 : p2;
        Player white = (p1Color == abalone.model.Color.BLACK) ? p2 : p1;

        launchGame(board, controller, black, white);
    }

    private void launchGame(Board board, GuiController controller, Player black, Player white) {
        GamePanel gamePanel = new GamePanel(board, controller);
        // Set players AFTER GamePanel construction so the onUpdate callback is wired
        // before setPlayers may trigger an opening AI move.
        controller.setPlayers(black, white);
        finishLaunch(board, controller, gamePanel);
    }

    private void launchGame(Board board, GuiController controller) {
        GamePanel gamePanel = new GamePanel(board, controller);
        finishLaunch(board, controller, gamePanel);
    }

    private void finishLaunch(Board board, GuiController controller, GamePanel gamePanel) {
        controller.setOnGameEnd(() -> {
            Timer timer = new Timer(3000, e -> showTitleScreen());
            timer.setRepeats(false);
            timer.start();
        });

        JPanel gameWrapper = new JPanel(new BorderLayout());
        gameWrapper.add(gamePanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new java.awt.Color(34, 139, 34));
        JButton saveButton = new JButton("Sauvegarder");
        saveButton.setFont(new Font("Arial", Font.PLAIN, 18));
        saveButton.addActionListener(e -> saveGame(board));
        bottomPanel.add(saveButton);

        gameWrapper.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(gameWrapper, "GAME");
        cardLayout.show(mainPanel, "GAME");
    }

    private void loadGame() {
        File savesDir = new File("saves");
        if (!savesDir.exists()) {
            savesDir.mkdirs();
        }
        JFileChooser fileChooser = new JFileChooser(savesDir);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            SaveService saveService = new SaveService();
            Board board = new Board();
            try {
                saveService.loadFromFile(selectedFile.getAbsolutePath(), board);
                GuiController controller = new GuiController(board);
                launchGame(board, controller);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors du chargement : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveGame(Board board) {
        String fileName = JOptionPane.showInputDialog(this, "Nom de la sauvegarde :", "Sauvegarder", JOptionPane.PLAIN_MESSAGE);
        if (fileName != null && !fileName.trim().isEmpty()) {
            File savesDir = new File("saves");
            if (!savesDir.exists()) {
                savesDir.mkdirs();
            }
            File saveFile = new File(savesDir, fileName + ".json");
            SaveService saveService = new SaveService();
            try {
                saveService.saveToFile(saveFile.getAbsolutePath(), board);
                JOptionPane.showMessageDialog(this, "Partie sauvegardée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
