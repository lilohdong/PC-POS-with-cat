package client.handover;

import dto.HandOverDTO;
import service.HandOverService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.text.DecimalFormat;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

public class HandOverMainPanel extends JPanel {

    private HandOverFrame parent;
    private String giverName;    // 인계자
    private String receiverName; // 인수자

    // 서비스 및 데이터
    private HandOverService service;
    private Timestamp startTime;
    private Timestamp endTime;

    // 금고 / 차액 관련
    private int prevCashReserve; // 이전까지 누적된 장부상 금고 금액 (금고 amount)
    private int prevDiffAcc;     // 이전까지 누적된 업무 차액 (diff_accumulate)

    private DecimalFormat df = new DecimalFormat("#,###"); // 금액 콤마 포맷

    // UI 필드
    private JTextField pcSalesField, productSalesField, cashDepositField, totalSalesField;
    private JTextField cashSafeField, realCashField, diffField;
    private JTextField withdrawalField, refundField;

    // 매출 값 (PC / 현금 / 카드)
    private int pcSalesValue;         // 시간충전 = PC 사용료
    private int cashProductSales;     // 상품 판매 (현금)
    private int cardProductSales;     // 상품 판매 (카드)

    public HandOverMainPanel(HandOverFrame parent, HandOverService service, String giverName, String receiverName) {
        this.parent = parent;
        this.service = service;
        this.giverName = giverName;
        this.receiverName = receiverName;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // 데이터 로드 및 계산 초기화
        initData();

        // 상단 타이틀
        add(createTopPanel(), BorderLayout.NORTH);
        // 중앙 내용
        add(createCenterPanel(), BorderLayout.CENTER);
        // 하단 버튼
        add(createBottomPanel(), BorderLayout.SOUTH);

        // UI 생성 후 초기 계산 실행
        updateCalculation();
    }

    // 초기 데이터 로드
    private void initData() {
        // 이전 인수인계 정보(시작시간, 이전 시재) 가져오기
        HandOverDTO lastData = service.getInitialData();
        this.startTime = lastData.getEndTime(); // 이전 끝난 시간이 나의 시작
        this.endTime = Timestamp.valueOf(LocalDateTime.now()); // 현재 시간

        // cash_safe에서 금고 금액 + 누적 차액 읽어오기
        try {
            int[] safeInfo = service.getCashSafeDetail();
            this.prevCashReserve = safeInfo[0]; // 장부상 금고 금액
            this.prevDiffAcc = safeInfo[1];     // 누적 차액
        } catch (Exception e) {
            e.printStackTrace();
            this.prevCashReserve = lastData.getCashReserve(); // 실패 시 대체값
            this.prevDiffAcc = 0;
        }
    }

    // 계산 업데이트 (리스너 등에서 호출)
    private void updateCalculation() {
        try {
            long cashSales = parseLong(cashDepositField.getText()); // 이번 근무 현금 매출
            long withdraw  = parseLong(withdrawalField.getText());  // 출금
            long refund    = parseLong(refundField.getText());      // 환불

            // 장부상 금고 금액 = 이전 금고 + 현금 매출 - 출금 - 환불
            long expected = prevCashReserve + cashSales - withdraw - refund;
            cashSafeField.setText(df.format(expected) + " 원");

            // 실제 금고 금액 (사용자 입력)
            long real = parseLong(realCashField.getText());

            // 누적 차액 = 실제 금고 - 장부 금고
            long accumulatedDiff = real - expected;


            diffField.setText(df.format(accumulatedDiff) + " 원");
            if (accumulatedDiff < 0) diffField.setForeground(Color.RED);
            else diffField.setForeground(Color.BLUE);

        } catch (Exception e) {

        }
    }

    // 문자열 금액 파싱
    private long parseLong(String text) {
        if (text == null) return 0;
        String trimmed = text.trim();
        if (trimmed.isEmpty()) return 0;

        // 숫자/마이너스만 남기기 ("원", 콤마 등 제거)
        String cleaned = trimmed.replaceAll("[^0-9\\-]", "");
        if (cleaned.equals("") || cleaned.equals("-")) return 0;

        return Long.parseLong(cleaned);
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

        JPanel content = new JPanel(new GridLayout(1, 2, 40, 0));
        content.setBorder(new EmptyBorder(30, 30, 30, 30));

        content.add(createLeftColumn());
        content.add(createRightColumn());

        centerPanel.add(content);
        return centerPanel;
    }

    private JPanel createLeftColumn() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 인계자/인수자
        leftPanel.add(createInputRow("인계자", giverName));
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(createInputRow("인수자", receiverName));
        leftPanel.add(Box.createVerticalStrut(30));

        // 매출 데이터 로드
        Map<String, Integer> sales = service.getSales(startTime, endTime);
        pcSalesValue     = sales.getOrDefault("pc", 0);
        cashProductSales = sales.getOrDefault("cash", 0);
        cardProductSales = sales.getOrDefault("card", 0);
        int totalSalesVal = sales.getOrDefault("total",
                pcSalesValue + cashProductSales + cardProductSales);

        leftPanel.add(createHeader("매출"));
        pcSalesField = new JTextField(df.format(pcSalesValue) + " 원");
        productSalesField = new JTextField(df.format(cashProductSales + cardProductSales) + " 원");
        cashDepositField = new JTextField(df.format(cashProductSales) + " 원"); // 현금 매출

        leftPanel.add(createFieldRow("PC 사용료", pcSalesField));
        leftPanel.add(createFieldRow("상품 판매액", productSalesField));
        leftPanel.add(createFieldRow("현금 입금액", cashDepositField));

        leftPanel.add(Box.createVerticalStrut(30));

        // 출금
        leftPanel.add(createHeader("출금"));

        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateCalculation(); }
            public void removeUpdate(DocumentEvent e) { updateCalculation(); }
            public void changedUpdate(DocumentEvent e) { updateCalculation(); }
        };

        withdrawalField = new JTextField("0");
        withdrawalField.getDocument().addDocumentListener(dl);

        refundField = new JTextField("0");
        refundField.getDocument().addDocumentListener(dl);

        leftPanel.add(createFieldRow("현금 출금", withdrawalField, true));
        leftPanel.add(createFieldRow("환불 금액", refundField, true));

        leftPanel.add(Box.createVerticalStrut(30));

        // 합계 (PC + 상품 전체)
        totalSalesField = new JTextField(df.format(totalSalesVal) + " 원");
        totalSalesField.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        totalSalesField.setForeground(new Color(50, 100, 255));
        leftPanel.add(createFieldRow("매출 합계", totalSalesField));

        leftPanel.add(Box.createVerticalGlue());
        return leftPanel;
    }

    private JPanel createRightColumn() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 금고
        rightPanel.add(createHeader("금고 금액 현황"));
        cashSafeField = new JTextField();      // 장부상 금고 금액(계산)
        realCashField = new JTextField("0");   // 실제 금고 금액(입력)
        diffField = new JTextField();          // 누적 업무 차액 표시
        diffField.setForeground(Color.RED);

        // 실제 금고 금액 입력 리스너
        realCashField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateCalculation(); }
            public void removeUpdate(DocumentEvent e) { updateCalculation(); }
            public void changedUpdate(DocumentEvent e) { updateCalculation(); }
        });

        rightPanel.add(createFieldRow("금고 금액", cashSafeField));
        rightPanel.add(createFieldRow("실제 금고 금액", realCashField, true));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createFieldRow("업무 차액", diffField));

        rightPanel.add(Box.createVerticalStrut(30));

        // 시간 확인
        rightPanel.add(createHeader("근무 시간 확인"));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String startStr = startTime.toLocalDateTime().format(dtf);
        String endStr = endTime.toLocalDateTime().format(dtf);

        rightPanel.add(createLabelRow("이전 인수 시간", startStr));
        rightPanel.add(createLabelRow("관리자 근무 시간", startStr + " ~ " + endStr));

        rightPanel.add(Box.createVerticalGlue());
        return rightPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton close = new JButton("닫기 (C)");
        close.setPreferredSize(new Dimension(100, 40));
        close.addActionListener(e -> parent.dispose());

        JButton done = new JButton("완료");
        done.setForeground(Color.WHITE);
        done.setPreferredSize(new Dimension(100, 40));
        done.setBackground(new Color(50, 100, 255));

        // 완료 버튼 클릭 시 DB 저장
        done.addActionListener(e -> {
            HandOverDTO dto = new HandOverDTO();
            dto.setGiverId(giverName);
            dto.setReceiverId(receiverName);
            dto.setStartTime(startTime);
            dto.setEndTime(Timestamp.valueOf(LocalDateTime.now()));

            int pc   = pcSalesValue;
            int cash = cashProductSales;
            int card = cardProductSales;

            dto.setTotalSales(pc + cash + card); // 전체 매출
            dto.setCashSales(cash);              // 현금 매출
            dto.setCardSales(card);              // 카드 매출

            // 장부상 금고 금액/차액 계산
            int previousSafe  = prevCashReserve;
            int withdraw      = (int)parseLong(withdrawalField.getText());
            int refund        = (int)parseLong(refundField.getText());
            int cashSalesNow  = cashProductSales;

            int expectedSafe  = previousSafe + cashSalesNow - withdraw - refund; // 장부 금고
            int realSafeInput = (int)parseLong(realCashField.getText());         // 실제 금고

            // 이번 누적 차액 = 실제 금고 - 장부 금고
            int accumulatedDiff = realSafeInput - expectedSafe;

            dto.setMemo("누적 차액: " + df.format(accumulatedDiff) + " 원");

            // 저장 성공 시 금고 금액 + 업무 차액 누적 업데이트
            if (service.save(dto)) {
                try {
                    service.updateCashSafe(expectedSafe, accumulatedDiff);

                    JOptionPane.showMessageDialog(this, "인수인계가 완료되었습니다.");
                    parent.dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "저장 실패");
            }
        });

        bottomPanel.add(close, BorderLayout.WEST);
        bottomPanel.add(done, BorderLayout.EAST);
        return bottomPanel;
    }

    // 공통 UI 헬퍼

    private JLabel createHeader(String text) {
        JLabel headerPanel = new JLabel(text);
        headerPanel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return headerPanel;
    }

    private JPanel createFieldRow(String title, JTextField field) {
        return createFieldRow(title, field, false);
    }

    private JPanel createFieldRow(String title, JTextField field, boolean isEditable) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rowLabel = new JLabel(title);
        rowLabel.setPreferredSize(new Dimension(100, 30));
        rowLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        field.setEditable(isEditable);
        field.setHorizontalAlignment(JTextField.RIGHT);
        field.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        rowPanel.add(rowLabel, BorderLayout.WEST);
        rowPanel.add(field, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(rowPanel, BorderLayout.CENTER);
        wrapper.setBorder(new EmptyBorder(0, 0, 5, 0));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        return wrapper;
    }

    private JPanel createInputRow(String title, String value) {
        JTextField tf = new JTextField(value);
        tf.setBorder(new LineBorder(Color.LIGHT_GRAY));
        tf.setHorizontalAlignment(JTextField.CENTER);
        return createFieldRow(title, tf, false);
    }

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
