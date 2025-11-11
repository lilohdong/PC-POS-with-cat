package client.game;

import javax.swing.*;

public class PopularGameRankingPanel extends JPanel {
    private static Object[][] data = {
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
    }
}
