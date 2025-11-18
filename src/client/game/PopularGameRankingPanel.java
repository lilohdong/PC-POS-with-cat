package client.game;

import javax.swing.*;
import java.awt.*;

public class PopularGameRankingPanel extends JPanel {
    private static final Object[][] data = {
            {"1. League of Legend", "35%"},
            {"2. FC 온라인", "10.96%"},
            {"3. Valorant", "9.67%"},
            {"4. 배틀그라운드", "9.21%"},
            {"5. 오버워치", "4.15%"},
            {"6. 서든어택", "4.11%"},
            {"7. 로블록스", "4.05%"},
            {"8. 던전앤파이터", "2.59%"},
            {"9. 메이플스토리", "2.29%"},
            {"10. 스타크래프트", "1.59%"}
    };
    public PopularGameRankingPanel() {
        setSize(270, 758);

        setLayout(new BorderLayout());

        JLabel top = new JLabel("게임 순위");
        top.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        add(top, BorderLayout.NORTH);
        JLabel dateLabel = new JLabel("기준 : 2025.11.07");
        dateLabel.setFont(new Font("맑은 고딕",Font.PLAIN,12));
        dateLabel.setForeground(Color.GRAY);
        dateLabel.setBounds(10, 10, 200, 30);
        add(dateLabel);
    }
}
