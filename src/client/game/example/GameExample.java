package client.game.example;
import client.game.*;
import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import util.*;

public class GameExample extends JFrame {

    public GameExample() {
        initUI();
    }

    private void initUI() {
        setTitle("게임 통계");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1016, 832);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);

        // 헤더 패널
        GameHeaderPanel headerPanel = new GameHeaderPanel();
        headerPanel.setBounds(0, 0, 1016, 74);
        add(headerPanel);

        // 인기게임 순위 패널
        PopularGameRankingPanel rankingPanel = new PopularGameRankingPanel();
        rankingPanel.setBounds(0, 74, 269, 758);
        add(rankingPanel);

        // 메인 게임 통계 패널
        MainGameStatistics mainStats = new MainGameStatistics();
        mainStats.setBounds(269, 74, 747, 758);
        add(mainStats);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameExample game = new GameExample();
            game.setVisible(true);

        });
    }
}
