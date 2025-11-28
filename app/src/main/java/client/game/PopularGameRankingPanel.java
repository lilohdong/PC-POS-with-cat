package client.game;

import util.Sizes;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class PopularGameRankingPanel extends JPanel {

    private String[][] gameData = {
            {"1", "League of Legend", "35%"},
            {"2", "FC 온라인", "10.96%"},
            {"3", "Valolant", "9.67%"},
            {"4", "배틀그라운드", "9.21%"},
            {"5", "오버워치", "4.18%"},
            {"6", "서든어택", "4.11%"},
            {"7", "로블록스", "4.05%"},
            {"8", "던전앤파이터", "2.89%"},
            {"9", "메이플스토리", "2.29%"},
            {"10", "스타크래프트", "1.89%"}
    };

    public PopularGameRankingPanel() {
        initUI();
    }

    private void initUI() {
        setPreferredSize(new Dimension(Sizes.GAME_POPULAR_WIDTH, 758));
        setBackground(new Color(240, 240, 240));
        setBorder(new MatteBorder(0, 2, 2, 1, Color.BLACK));
        setLayout(new BorderLayout());

        // 헤더
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

        JLabel titleLabel = new JLabel("인기게임 순위");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel dateLabel = new JLabel("기준 : 2025.11.07");
        dateLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);
        headerPanel.add(dateLabel, BorderLayout.CENTER);

        JPanel columnHeader = new JPanel(new GridLayout(1, 2));
        columnHeader.setBackground(Color.WHITE);
        JLabel gameLabel = new JLabel("게임 이름");
        gameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        JLabel shareLabel = new JLabel("점유율", JLabel.RIGHT);
        shareLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        columnHeader.add(gameLabel);
        columnHeader.add(shareLabel);
        headerPanel.add(columnHeader, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // 게임 리스트
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        for (String[] game : gameData) {
            JPanel gamePanel = createGameItem(game[0], game[1], game[2]);
            listPanel.add(gamePanel);
            listPanel.add(Box.createVerticalStrut(20)); // 간격
        }
        add(listPanel, BorderLayout.CENTER);
    }

    // 게임 순위별로 한개씩
    private JPanel createGameItem(String rank, String name, String share) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(269, 40));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel nameLabel = new JLabel(rank + ". " + name);
        nameLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        JLabel shareLabel = new JLabel(share, JLabel.RIGHT);
        shareLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));

        panel.add(nameLabel, BorderLayout.WEST);
        panel.add(shareLabel, BorderLayout.EAST);

        return panel;
    }

    private void initData() {

    }
}
