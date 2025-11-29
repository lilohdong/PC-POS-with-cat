package client.game;

import util.Sizes;

import javax.swing.*;
import java.awt.*;

public class GameMainPanel extends JPanel {
    public GameMainPanel() {
        initUI();
    }
    private GameHeaderPanel headerPanel;
    private PopularGameRankingPanel rankingPanel;
    private MainGameStatistics mainStats;
    private void initUI() {
        setSize(Sizes.PANEL_WIDTH, Sizes.PANEL_HEIGHT);
        setLayout(new BorderLayout());
        // 헤더 패널
        headerPanel = new GameHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // 인기게임 순위 패널
        rankingPanel = new PopularGameRankingPanel();
        add(rankingPanel, BorderLayout.WEST);

        // 메인 게임 통계 패널
        mainStats = new MainGameStatistics();
        add(mainStats, BorderLayout.CENTER);
    }

}
