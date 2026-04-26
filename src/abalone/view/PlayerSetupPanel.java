package abalone.view;

import abalone.ai.Difficulty;
import abalone.model.Color;
import javax.swing.*;
import java.awt.*;

public class PlayerSetupPanel extends JPanel {
  public interface Listener {
    void onStartGame(String p1Name, Color p1Color, String p2Name, Color p2Color,
        boolean p2IsAI, Difficulty difficulty);

    void onBackToTitle();
  }

  private static final String MODE_HVH = "Humain vs Humain";
  private static final String MODE_HVAI = "Humain vs IA";

  private final JTextField p1NameField;
  private final JTextField p2NameField;
  private final JComboBox<Color> p1ColorBox;
  private final JLabel p2ColorLabel;
  private final JComboBox<String> modeBox;
  private final JComboBox<Difficulty> difficultyBox;

  public PlayerSetupPanel(Listener listener) {
    setLayout(new GridBagLayout());
    setBackground(new java.awt.Color(34, 139, 34));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JLabel title = new JLabel("Configuration des Joueurs");
    title.setFont(new Font("Arial", Font.BOLD, 36));
    title.setForeground(java.awt.Color.WHITE);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    add(title, gbc);

    gbc.gridwidth = 1;
    gbc.gridy++;
    add(createLabel("Mode :"), gbc);
    modeBox = new JComboBox<>(new String[] { MODE_HVH, MODE_HVAI });
    gbc.gridx = 1;
    add(modeBox, gbc);

    gbc.gridx = 0;
    gbc.gridy++;
    add(createLabel("Difficulté IA :"), gbc);
    difficultyBox = new JComboBox<>(Difficulty.values());
    difficultyBox.setSelectedItem(Difficulty.MEDIUM);
    difficultyBox.setEnabled(false);
    gbc.gridx = 1;
    add(difficultyBox, gbc);

    gbc.gridx = 0;
    gbc.gridy++;
    add(createLabel("Joueur 1 :"), gbc);
    p1NameField = new JTextField("Joueur 1", 15);
    gbc.gridx = 1;
    add(p1NameField, gbc);

    gbc.gridx = 0;
    gbc.gridy++;
    add(createLabel("Couleur P1 :"), gbc);
    p1ColorBox = new JComboBox<>(new Color[] { Color.BLACK, Color.WHITE });
    gbc.gridx = 1;
    add(p1ColorBox, gbc);

    gbc.gridx = 0;
    gbc.gridy++;
    add(createLabel("Joueur 2 :"), gbc);
    p2NameField = new JTextField("Joueur 2", 15);
    gbc.gridx = 1;
    add(p2NameField, gbc);

    gbc.gridx = 0;
    gbc.gridy++;
    add(createLabel("Couleur P2 :"), gbc);
    p2ColorLabel = new JLabel(Color.WHITE.toString());
    p2ColorLabel.setForeground(java.awt.Color.WHITE);
    p2ColorLabel.setFont(new Font("Arial", Font.BOLD, 18));
    gbc.gridx = 1;
    add(p2ColorLabel, gbc);

    p1ColorBox.addActionListener(e -> {
      Color c = (Color) p1ColorBox.getSelectedItem();
      p2ColorLabel.setText(c == Color.BLACK ? Color.WHITE.toString() : Color.BLACK.toString());
    });

    modeBox.addActionListener(e -> applyModeState());

    gbc.gridx = 0;
    gbc.gridy++;
    gbc.gridwidth = 2;
    JButton startButton = new JButton("Commencer la partie");
    startButton.addActionListener(e -> {
      Color c1 = (Color) p1ColorBox.getSelectedItem();
      Color c2 = c1 == Color.BLACK ? Color.WHITE : Color.BLACK;
      boolean isAI = MODE_HVAI.equals(modeBox.getSelectedItem());
      Difficulty diff = isAI ? (Difficulty) difficultyBox.getSelectedItem() : null;
      String p2Name = isAI ? "IA (" + diff.getLabel() + ")" : p2NameField.getText();
      listener.onStartGame(p1NameField.getText(), c1, p2Name, c2, isAI, diff);
    });
    add(startButton, gbc);

    gbc.gridy++;
    JButton backButton = new JButton("Retour");
    backButton.addActionListener(e -> listener.onBackToTitle());
    add(backButton, gbc);
  }

  private void applyModeState() {
    boolean ai = MODE_HVAI.equals(modeBox.getSelectedItem());
    difficultyBox.setEnabled(ai);
    p2NameField.setEnabled(!ai);
    if (ai) {
      Difficulty d = (Difficulty) difficultyBox.getSelectedItem();
      p2NameField.setText("IA (" + d.getLabel() + ")");
    }
  }

  private JLabel createLabel(String text) {
    JLabel l = new JLabel(text);
    l.setForeground(java.awt.Color.WHITE);
    l.setFont(new Font("Arial", Font.BOLD, 18));
    return l;
  }
}
