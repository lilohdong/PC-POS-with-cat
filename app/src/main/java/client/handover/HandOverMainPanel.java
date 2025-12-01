package client.handover;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HandOverMainPanel extends JPanel {

    private HandOverFrame parent;
    private String giverName; // 받아온 인계자 이름
    private String receiverName;

    private JTextField pcSalesField, productSalesField, cashDepositField, totalSalesField;
    private JTextField cashSafeField, realCashField, diffField;

    public HandOverMainPanel(HandOverFrame parent, String giverName) {
        this.parent = parent;
        this.giverName = giverName;
        this.receiverName = receiverName;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // 상단 타이틀
        add(createTopPanel(), BorderLayout.NORTH);
        // 중앙 내용
        add(createCenterPanel(), BorderLayout.CENTER);
        // 하단 버튼
        add(createBottomPanel(), BorderLayout.SOUTH);

    }


    // UI 구성
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel topLabel = new JLabel("  인수 / 인계  ");
        topLabel.setOpaque(true);
        topLabel.setBackground(new Color(50, 100, 255));
        topLabel.setForeground(Color.WHITE);
        topLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        topLabel.setPreferredSize(new Dimension(100, 30));
        topPanel.add(topLabel);
        return topPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        centerPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));

        // 내부 패딩
        JPanel content = new JPanel(new GridLayout(1, 2, 40, 0));
        content.setBorder(new EmptyBorder(30, 30, 30, 30));

        content.add(createLeftColumn());
        content.add(createRightColumn());

        centerPanel.add(content);
        return centerPanel;
    }

    private JPanel createLeftColumn() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS)); // 세로 정렬
        leftPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // 왼쪽 정렬

        // 인계자/인수자
        leftPanel.add(createInputRow("인계자", giverName)); // 로그인에서 받아온 이름 사용
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(createInputRow("인수자", receiverName )); //

        leftPanel.add(Box.createVerticalStrut(30));

        // 매출
        leftPanel.add(createHeader("매출"));
        pcSalesField = new JTextField();
        productSalesField = new JTextField();
        cashDepositField = new JTextField();

        leftPanel.add(createFieldRow("PC 사용료", pcSalesField));
        leftPanel.add(createFieldRow("상품 판매액", productSalesField));
        leftPanel.add(createFieldRow("현금 입금액", cashDepositField));

        leftPanel.add(Box.createVerticalStrut(30));

        // 출금
        leftPanel.add(createHeader("출금"));
        leftPanel.add(createFieldRow("현금 출금", new JTextField("0원")));
        leftPanel.add(createFieldRow("환불 금액", new JTextField("0원")));

        leftPanel.add(Box.createVerticalStrut(30));

        // 합계
        totalSalesField = new JTextField();
        totalSalesField.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        totalSalesField.setForeground(new Color(50, 100, 255)); // 파란색 강조
        leftPanel.add(createFieldRow("매출 합계", totalSalesField));

        leftPanel.add(Box.createVerticalGlue()); // 남는 공간 채움
        return leftPanel;
    }

    private JPanel createRightColumn() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 금고
        rightPanel.add(createHeader("금고 금액 현황"));
        cashSafeField = new JTextField();
        realCashField = new JTextField();
        diffField = new JTextField();
        diffField.setForeground(Color.RED); // 차액 빨간색

        rightPanel.add(createFieldRow("금고 금액", cashSafeField));
        rightPanel.add(createFieldRow("실제 금고 금액", realCashField));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createFieldRow("업무 차액", diffField));

        rightPanel.add(Box.createVerticalStrut(30));

        // 인출/인계
        rightPanel.add(createHeader("인출 / 인계 금액"));
        rightPanel.add(createFieldRow("인출 금액", new JTextField("0 원")));
        rightPanel.add(createFieldRow("인계 금액", new JTextField("0 원")));

        rightPanel.add(Box.createVerticalStrut(30));

        // 시간 확인
        rightPanel.add(createHeader("근무 시간 확인"));
        // 현재 시간 포맷팅
        String nowTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        rightPanel.add(createLabelRow("이전 인수 시간", "2025-12-01 09:00")); // DB에서 가져와야 함
        rightPanel.add(createLabelRow("관리자 근무 시간", "09:00 ~ " + nowTime));

        rightPanel.add(Box.createVerticalGlue());
        return rightPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton close = new JButton("닫기 (C)");
        close.setPreferredSize(new Dimension(100, 40));
        close.addActionListener(e -> System.exit(0));

        JButton done = new JButton("완료");
        done.setPreferredSize(new Dimension(100, 40));
        done.setBackground(new Color(50, 100, 255));

        bottomPanel.add(close, BorderLayout.WEST);
        bottomPanel.add(done, BorderLayout.EAST);
        return bottomPanel;
    }

    // 섹션 제목 (굵은 글씨, 왼쪽 정렬)
    private JLabel createHeader(String text) {

        JLabel headerPanel = new JLabel(text);
        headerPanel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // 왼쪽 정렬

        return headerPanel;
    }

    // 라벨 + 텍스트필드
    private JPanel createFieldRow(String title, JTextField field) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35)); // 높이 제한
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // 박스 전체 왼쪽 정렬

        JLabel rowLable= new JLabel(title);
        rowLable.setPreferredSize(new Dimension(100, 30));
        rowLable.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        field.setEditable(false); // 수정 불가
        field.setHorizontalAlignment(JTextField.RIGHT); // 숫자는 우측 정렬
        field.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.LIGHT_GRAY)); // 밑줄만

        rowPanel.add(rowLable, BorderLayout.WEST);
        rowPanel.add(field, BorderLayout.CENTER);

        // 간격 띄우기용 패널
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(rowPanel, BorderLayout.CENTER);
        wrapper.setBorder(new EmptyBorder(0,0,5,0)); // 아래로 5px 간격
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        return wrapper;
    }

    // 단순 텍스트 표시용
    private JPanel createInputRow(String title, String value) {
        JTextField tf = new JTextField(value);
        tf.setBorder(new LineBorder(Color.LIGHT_GRAY)); // 박스 테두리
        tf.setHorizontalAlignment(JTextField.CENTER);
        return createFieldRow(title, tf);
    }

    // 시간 표시용
    private JPanel createLabelRow(String title, String value) {
        JPanel p = new JPanel(new BorderLayout());

        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel t = new JLabel(title);
        t.setPreferredSize(new Dimension(120, 20));
        t.setForeground(Color.GRAY);

        JLabel v = new JLabel(value);
        v.setHorizontalAlignment(JLabel.RIGHT);

        p.add(t, BorderLayout.WEST);
        p.add(v, BorderLayout.CENTER);
        return p;
    }
}
