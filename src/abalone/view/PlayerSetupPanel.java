package abalone.view;

import abalone.model.Color;
import javax.swing.*;
import java.awt.*;

public class PlayerSetupPanel extends JPanel {
  public interface Listener {
    void onStartGame(String p1Name, Color p1Color, String p2Name, Color p2Color);

    void onBackToTitle();
  }

  private final JTextField p1NameField;
  private final JTextField p2NameField;
  private final JComboBox<Color> p1ColorBox;
  private final JLabel p2ColorLabel;

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

    gbc.gridx = 0;
    gbc.gridy++;
    gbc.gridwidth = 2;
    JButton startButton = new JButton("Commencer la partie");
    startButton.addActionListener(e -> {
      Color c1 = (Color) p1ColorBox.getSelectedItem();
      Color c2 = c1 == Color.BLACK ? Color.WHITE : Color.BLACK;
      listener.onStartGame(p1NameField.getText(), c1, p2NameField.getText(), c2);
    });
    add(startButton, gbc);

    gbc.gridy++;
    JButton backButton = new JButton("Retour");
    backButton.addActionListener(e -> listener.onBackToTitle());
    add(backButton, gbc);
  }

  private JLabel createLabel(String text) {
    JLabel l = new JLabel(text);
    l.setForeground(java.awt.Color.WHITE);
    l.setFont(new Font("Arial", Font.BOLD, 18));
    return l;
  }
}
