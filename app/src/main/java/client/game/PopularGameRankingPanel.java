package client.game;

import dao.GameDAO;
import dto.PopularGameDTO;
import util.Sizes;
import java.util.List;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PopularGameRankingPanel extends JPanel {

    private JPanel listPanel;

    public PopularGameRankingPanel() {
        initUI();
        initData(); // UI 생성 후 데이터 로드
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
        // 현재 시간 기준으로 날짜 잡음 (formatter)
        JLabel dateLabel = new JLabel("기준 : "+ LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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
        // 리스트 영역
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    // 게임 순위별로 한개씩
    private JPanel createGameItem(String rank, String name, String share) {
        JPanel panel = new JPanel(new BorderLayout());
        // 길이를 MAX_VALUE로 잡아서 꽉차게
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.setPreferredSize(new Dimension(0, 40));

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
        // 기존 내용 초기화 (새로고침 시 유용)
        listPanel.removeAll();
        List<PopularGameDTO> games = GameDAO.getInstance().getAllPopularGames();

        if (games.isEmpty()) {
            JLabel emptyLabel = new JLabel("데이터 집계 중...");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(Box.createVerticalStrut(20));
            listPanel.add(emptyLabel);
        } else {
            for (PopularGameDTO game : games) {
                // % 단위로 포멧
                String rankStr = String.valueOf(game.getRank());
                String shareStr = String.format("%.2f%%", game.getShare());

                JPanel gamePanel = createGameItem(rankStr, game.getGameName(), shareStr);
                listPanel.add(gamePanel);
                // 간격 추가
                listPanel.add(Box.createVerticalStrut(10));
            }
        }
        // UI 갱신
        listPanel.revalidate();
        listPanel.repaint();
    }
}
