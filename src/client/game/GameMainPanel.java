package client.game;

import javax.swing.*;
import java.awt.*;

public class GameMainPanel extends JPanel {
    public GameMainPanel() {
        setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("게임 통계");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 32));
        add(titleLabel, BorderLayout.WEST);
    }
}
