package client.store.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StoreControlPanel extends JPanel {
    private JLabel statusLabel;

    // 버튼 필드 (Controller에서 접근 가능하도록 getter 제공)
    private JButton startBtn;
    private JButton endBtn;
    private JButton infoBtn;
    private JButton chargeBtn;
    private JButton availBtn;
    private JButton refreshBtn;

    public StoreControlPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initStatusPanel();
        initButtonPanel();
    }

    private void initStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("좌석을 선택하세요");
        statusLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.WEST);
    }

    private void initButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        startBtn = createButton("사용 시작", new Color(146, 160, 250));
        endBtn = createButton("사용 종료", new Color(255, 150, 150));
        infoBtn = createButton("좌석 정보", new Color(200, 230, 255));
        chargeBtn = createButton("시간 충전", new Color(255, 200, 100));
        availBtn = createButton("이용 불가", new Color(200, 200, 200));
        refreshBtn = createButton("새로고침", new Color(180, 180, 180));

        buttonPanel.add(startBtn);
        buttonPanel.add(endBtn);
        buttonPanel.add(infoBtn);
        buttonPanel.add(chargeBtn);
        buttonPanel.add(availBtn);
        buttonPanel.add(refreshBtn);

        add(buttonPanel, BorderLayout.EAST);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 35));
        button.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 호버 효과
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { button.setBackground(bgColor.brighter()); }
            @Override
            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
        });

        return button;
    }

    // 상태 메시지 변경 메서드
    public void setStatusText(String text) {
        statusLabel.setText(text);
    }

    public JButton getStartBtn() { return startBtn; }
    public JButton getEndBtn() { return endBtn; }
    public JButton getInfoBtn() { return infoBtn; }
    public JButton getChargeBtn() { return chargeBtn; }
    public JButton getAvailBtn() { return availBtn; }
    public JButton getRefreshBtn() { return refreshBtn; }
}
