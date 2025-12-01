package client.handover;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HandOverMainPanel extends JPanel {

    private HandOverFrame parent;

    public HandOverMainPanel(HandOverFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245)); // 전체 배경 연한 회색
        setBorder(new EmptyBorder(20, 30, 20, 30)); // 전체 여백

        // 1. 상단 타이틀 ("인수 / 인계")
        add(createTopPanel(), BorderLayout.NORTH);

        // 2. 중앙 내용 (좌측 폼, 우측 폼)
        add(createCenterPanel(), BorderLayout.CENTER);

        // 3. 하단 버튼 (닫기, 완료)
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    // --- 1. 상단 타이틀 영역 ---
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(new EmptyBorder(0, 0, 20, 0)); // 아래 내용과 간격

        JLabel titleLabel = new JLabel("  인수 / 인계  ");
        titleLabel.setOpaque(true); // 배경색 적용을 위해 필요
        titleLabel.setBackground(new Color(50, 100, 255)); // 파란색
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        titleLabel.setPreferredSize(new Dimension(100, 30));

        // 둥근 느낌은 단순 라벨로는 어려워서 일단 네모난 형태로 깔끔하게 처리
        panel.add(titleLabel);
        return panel;
    }

    // --- 2. 중앙 메인 폼 영역 ---
    private JPanel createCenterPanel() {
        // 좌우 2칸으로 나눔 (간격 40px)
        JPanel panel = new JPanel(new GridLayout(1, 2, 40, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1)); // 전체 테두리

        // 내부 여백
        JPanel paddingPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        paddingPanel.setBackground(Color.WHITE);
        paddingPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // 왼쪽 컬럼, 오른쪽 컬럼 생성
        paddingPanel.add(createLeftColumn());
        paddingPanel.add(createRightColumn());

        // 테두리 패널 안에 내용 패널 넣기 (구조상 깔끔하게 하기 위함)
        panel.add(paddingPanel);

        // GridLayout 특성상 꽉 차게 늘어나므로, BorderLayout 안에 넣어서 위쪽 정렬처럼 보이게 함
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.CENTER);

        return wrapper;
    }

    // [왼쪽 컬럼] 인계자/인수자, 매출, 출금, 합계
    private JPanel createLeftColumn() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // 위에서 아래로 쌓기
        panel.setBackground(Color.WHITE);

        // 1. 인계자/인수자 정보
        panel.add(createRow("인계자", "오동준", false));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createRow("인수자", "조춘규", false));

        panel.add(Box.createVerticalStrut(30)); // 섹션 간격

        // 2. 매출 정보
        panel.add(createSectionTitle("매출"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createRow("PC 사용료", "100,000원", true));
        panel.add(Box.createVerticalStrut(5));
        panel.add(createRow("상품 판매액", "58,000원", true));
        panel.add(Box.createVerticalStrut(5));
        panel.add(createRow("현금 입금액", "20,000원", true));

        panel.add(Box.createVerticalStrut(30));

        // 3. 출금 정보
        panel.add(createSectionTitle("출금"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createRow("현금 출금", "0원", true));
        panel.add(Box.createVerticalStrut(5));
        panel.add(createRow("환불 금액", "0원", true));

        panel.add(Box.createVerticalStrut(30));

        // 4. 매출 합계 (강조)
        JPanel totalPanel = createRow("매출 합계", "178,000원", false);
        // 폰트 강조를 위해 컴포넌트 찾아서 스타일 변경
        Component[] comps = totalPanel.getComponents();
        for(Component c : comps) {
            c.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        }
        panel.add(totalPanel);

        // 남는 공간 밀어내기
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // [오른쪽 컬럼] 금고 현황, 인출/인계, 근무 시간
    private JPanel createRightColumn() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // 1. 금고 금액 현황
        panel.add(createSectionTitle("금고 금액 현황"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createRow("금고 금액", "1,000,000 원", true));
        panel.add(Box.createVerticalStrut(5));
        panel.add(createRow("실제 금고 금액", "1,000,000 원", true));
        panel.add(Box.createVerticalStrut(10));

        // 업무 차액 (빨간 글씨)
        JPanel diffPanel = createRow("업무 차액", "+ 0 원", false);
        Component[] diffComps = diffPanel.getComponents();
        // 텍스트 필드(또는 라벨)를 찾아 빨간색으로 변경
        if(diffComps.length > 0 && diffComps[0] instanceof JPanel) { // 구조상 JPanel 안에 있음
            // 단순화를 위해 createRow 구조를 아래에서 자세히 봐야함.
            // 여기서는 간단히 구현했으므로 넘어가거나 별도 처리.
            // *아래 createRow 메서드에서 빨간색 처리 기능을 추가하는게 깔끔함*
        }
        // 임시로 빨간 텍스트 라벨 하나 추가하는 식으로 표현
        JLabel diffLabel = new JLabel("+ 0 원");
        diffLabel.setForeground(Color.RED);
        diffLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        JPanel diffRow = new JPanel(new BorderLayout());
        diffRow.setBackground(Color.WHITE);
        diffRow.add(new JLabel("업무 차액"), BorderLayout.WEST);
        diffRow.add(diffLabel, BorderLayout.EAST);
        panel.add(diffRow);


        panel.add(Box.createVerticalStrut(30));

        // 2. 인출 / 인계 금액
        panel.add(createSectionTitle("인출 / 인계 금액"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createRow("인출 금액", "0 원", true));
        panel.add(Box.createVerticalStrut(5));
        panel.add(createRow("인계 금액", "0 원", true));

        panel.add(Box.createVerticalStrut(30));

        // 3. 근무 시간 확인
        panel.add(createSectionTitle("근무 시간 확인"));
        panel.add(Box.createVerticalStrut(10));

        // 시간은 텍스트 필드보다 라벨이 어울림
        panel.add(createTextRow("이전 인수 시간", "2025-11-08 오전 08:54"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(createTextRow("관리자 근무 시간", "2025-11-08 08:54 ~ 12:46"));

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // --- 3. 하단 버튼 영역 ---
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton btnClose = new JButton("닫기 (C)");
        btnClose.setPreferredSize(new Dimension(100, 40));
        btnClose.setBackground(Color.WHITE);

        JButton btnDone = new JButton("완료");
        btnDone.setPreferredSize(new Dimension(100, 40));
        btnDone.setBackground(new Color(50, 100, 255));
        btnDone.setForeground(Color.WHITE);

        // 닫기 버튼 이벤트
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // 프로그램 종료 (임시)
            }
        });

        panel.add(btnClose, BorderLayout.WEST);
        panel.add(btnDone, BorderLayout.EAST);

        return panel;
    }

    // --- 유틸리티 메서드 (중복 코드 제거용) ---

    // 섹션 제목 (굵은 글씨)
    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        return label;
    }

    // 라벨 + 입력창 한 줄 만들기 (isBox: 입력상자 테두리 여부)
    private JPanel createRow(String labelText, String valueText, boolean isBox) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); // 높이 고정

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        label.setPreferredSize(new Dimension(100, 30)); // 라벨 너비 고정

        JTextField field = new JTextField(valueText);
        field.setHorizontalAlignment(JTextField.RIGHT); // 우측 정렬
        field.setEditable(false); // 일단 보기 전용
        field.setBackground(Color.WHITE);

        if (!isBox) {
            field.setBorder(null); // 테두리 없음
        }

        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    // 라벨 + 텍스트 한 줄 (입력창 아님, 근무시간용)
    private JPanel createTextRow(String labelText, String valueText) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20)); // 높이 얇게

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
        label.setForeground(Color.GRAY);

        JLabel value = new JLabel(valueText);
        value.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
        value.setHorizontalAlignment(SwingConstants.RIGHT); // 우측 정렬

        panel.add(label, BorderLayout.WEST);
        panel.add(value, BorderLayout.CENTER);

        return panel;
    }
}
