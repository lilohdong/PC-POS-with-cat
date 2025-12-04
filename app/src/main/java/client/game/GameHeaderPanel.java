package client.game;

import font.ClearGodic;
import util.Sizes;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class GameHeaderPanel extends JPanel {

    public GameHeaderPanel() {
        initUI();
    }
    // 단순 헤더 패널 생성
    private void initUI() {
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, 74));
        setBorder(new MatteBorder(1, 0, 1, 0, Color.BLACK));
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel titleLabel = new JLabel("게임 통계", JLabel.CENTER);
        titleLabel.setFont(new ClearGodic(36));
        add(titleLabel);
    }
}
