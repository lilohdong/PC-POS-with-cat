package client.store.view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StoreHeaderPanel extends JPanel {
    private JLabel timeLabel;
    private JLabel totalLabel;
    private JLabel availLabel;
    private JLabel occupiedLabel;

    // 범례 색상 상수 (SeatButton과 일치)
    private static final Color COL_AVAIL = new Color(200, 200, 200);
    private static final Color COL_CHILD = new Color(255, 180, 180);
    private static final Color COL_ADULT = new Color(180, 180, 255);
    private static final Color COL_UNAVAIL = new Color(150, 150, 150);
    private static final Color COL_SELECT = new Color(255, 255, 150);

    public StoreHeaderPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initTitleAndTime();
        initStatsPanel();
        initLegendPanel();
    }

    private void initTitleAndTime() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("매장 관리", JLabel.LEFT);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));

        timeLabel = new JLabel();
        timeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        updateCurrentTime(); // 초기 시간 설정

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(timeLabel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);
    }

    private void initStatsPanel() {
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        totalLabel = createStatLabel("전체: 0석", Color.BLACK);
        availLabel = createStatLabel("이용가능: 0석", new Color(0, 150, 0));
        occupiedLabel = createStatLabel("사용중: 0석", new Color(200, 0, 0));

        statsPanel.add(totalLabel);
        statsPanel.add(new JSeparator(JSeparator.VERTICAL));
        statsPanel.add(availLabel);
        statsPanel.add(new JSeparator(JSeparator.VERTICAL));
        statsPanel.add(occupiedLabel);

        add(statsPanel, BorderLayout.CENTER);
    }

    private JLabel createStatLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        label.setForeground(color);
        return label;
    }

    private void initLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        legendPanel.add(createLegendItem("이용가능", COL_AVAIL));
        legendPanel.add(createLegendItem("미성년자", COL_CHILD));
        legendPanel.add(createLegendItem("성인", COL_ADULT));
        legendPanel.add(createLegendItem("이용불가", COL_UNAVAIL));
        legendPanel.add(createLegendItem("선택됨", COL_SELECT));
        add(legendPanel, BorderLayout.SOUTH);
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel label = new JLabel(text);
        label.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        panel.add(colorBox);
        panel.add(label);
        return panel;
    }

    // 시간 업데이트 메서드 (Controller/Timer가 호출)
    public void updateCurrentTime() {
        timeLabel.setText(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    // 통계 업데이트 메서드 (Controller가 호출)
    public void updateStats(int total, int available, int occupied) {
        totalLabel.setText("전체: " + total + "석");
        availLabel.setText("이용가능: " + available + "석");
        occupiedLabel.setText("사용중: " + occupied + "석");
    }
}
