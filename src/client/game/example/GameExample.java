package client.game.example;

import javax.swing.*;
import client.game.*;

import java.awt.*;

class GameWindow extends JFrame {

    public GameWindow() {
        setSize(1016, 832);
        setLayout(new BorderLayout(0, 7));

        GameHeaderPanel topPanel = new GameHeaderPanel();
        add(topPanel, BorderLayout.NORTH);
        PopularGameRankingPanel leftPanel = new PopularGameRankingPanel();
        add(leftPanel, BorderLayout.WEST);
        MainGameStatistics rightPanel = new MainGameStatistics();
        add(rightPanel, BorderLayout.EAST);
    }
}

public class GameExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setVisible(true);
        });
    }
}
