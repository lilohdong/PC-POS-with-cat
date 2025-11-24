package client.game;

import javax.swing.*;
import java.awt.*;

public class GameMainPanel extends JPanel {
    public GameMainPanel() {
        initUI();
    }

    private void initUI() {
        setSize(1016, 832);
        setLayout(new BorderLayout());
        // 헤더 패널
        GameHeaderPanel headerPanel = new GameHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // 인기게임 순위 패널
        PopularGameRankingPanel rankingPanel = new PopularGameRankingPanel();
        add(rankingPanel, BorderLayout.WEST);

        // 메인 게임 통계 패널
        MainGameStatistics mainStats = new MainGameStatistics();
        add(mainStats, BorderLayout.CENTER);
    }
}
