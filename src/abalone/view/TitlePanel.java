package abalone.view;

import javax.swing.*;
import java.awt.*;

public class TitlePanel extends JPanel {
  public interface Listener {
    void onStartClicked();

    void onQuitClicked();
  }

  public TitlePanel(Listener listener) {
    setLayout(new GridBagLayout());
    setBackground(new Color(34, 139, 34)); // Match GamePanel background

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.insets = new Insets(20, 20, 20, 20);

    JLabel titleLabel = new JLabel("ABALONE");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 72));
    titleLabel.setForeground(Color.WHITE);
    gbc.gridy = 0;
    add(titleLabel, gbc);

    JButton playButton = new JButton("Jouer");
    playButton.setFont(new Font("Arial", Font.PLAIN, 24));
    playButton.setPreferredSize(new Dimension(200, 50));
    playButton.addActionListener(e -> listener.onStartClicked());
    gbc.gridy = 1;
    add(playButton, gbc);

    JButton quitButton = new JButton("Quitter le jeu");
    quitButton.setFont(new Font("Arial", Font.PLAIN, 24));
    quitButton.setPreferredSize(new Dimension(200, 50));
    quitButton.addActionListener(e -> listener.onQuitClicked());
    gbc.gridy = 2;
    add(quitButton, gbc);
  }
}
