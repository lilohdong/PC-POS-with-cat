package client.game;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class GameHeaderPanel extends JPanel {

    public GameHeaderPanel() {
        initUI();
    }
    // 단순 헤더 패널 생성
    private void initUI() {
        setPreferredSize(new Dimension(1016, 74));
        setBorder(new MatteBorder(1, 0, 1, 0, Color.BLACK));
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel titleLabel = new JLabel("게임 통계", JLabel.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 36));
        add(titleLabel);
    }
}
