package client.game.example;
import client.game.*;
import javax.swing.*;


import util.*;

import java.awt.*;

public class GameExample extends JFrame {

    public GameExample() {
        GameMainPanel gp = new GameMainPanel();
        setTitle("Game Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(new Dimension(1280,832));

        SideBar sb = new SideBar();
        add(sb, BorderLayout.WEST);

        add(gp, BorderLayout.CENTER);
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameExample game = new GameExample();
            game.setVisible(true);

        });
    }
}
