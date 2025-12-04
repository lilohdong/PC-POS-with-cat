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
    private String giverName; // 받아온 인계자 이름
    private String receiverName;

    //  서비스 객체 및 데이터 처리 변수
    private HandOverService service;
    private Timestamp startTime;
    private Timestamp endTime;
    private int prevCashReserve; // 이전 타임에서 넘겨받은 시재 (인수 금액)
    private DecimalFormat df = new DecimalFormat("#,###"); // 금액 콤마 포맷

    private JTextField pcSalesField, productSalesField, cashDepositField, totalSalesField;
    private JTextField cashSafeField, realCashField, diffField;
    // 필드명을 UI 용도에 맞게 매칭 (출금, 환불, 인계금액)
    private JTextField withdrawalField, refundField, handoverField;

    // 생성자 파라미터에 Service와 인수자 이름 추가
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

    // 초기 데이터 로드 메서드
    private void initData() {
        // 이전 인수인계 정보(시작시간, 이전 시재) 가져오기
        HandOverDTO lastData = service.getInitialData();
        this.startTime = lastData.getEndTime(); // 이전 끝난 시간이 나의 시작
        this.prevCashReserve = lastData.getCashReserve(); // 이전 시재가 나의 인수 금액
        this.endTime = Timestamp.valueOf(LocalDateTime.now()); // 현재 시간

        // 영속화된 '금고 금액'을 DB로부터 읽어와서 prevCashReserve로 덮어쓸지 결정
        // 사용자가 원하면 prevCashReserve를 handover 테이블의 값(마지막 인계금액)으로 유지할 수도 있음.
        try {
            int persistedSafe = service.getCashSafe(); // [추가] Service에서 DAO 호출
            if (persistedSafe > 0) {
                // 실제 운영 환경에서는 정책에 따라 병합/대체 결정 필요
                this.prevCashReserve = persistedSafe; // [추가] 우선 DB에 저장된 금고값 사용
            }
        } catch (Exception e) {
            // 실패 시 기존값 유지
            e.printStackTrace();
        }
    }

    // 계산 업데이트 메서드 (리스너 등에서 호출)
    private void updateCalculation() {
        try {
            long cashSales = parseLong(cashDepositField.getText());
            long withdraw = parseLong(withdrawalField.getText());
            long refund = parseLong(refundField.getText());

            // 장부상 금고 금액 = 이전시재(인수금액) + 현금매출 - 출금 - 환불
            long expected = prevCashReserve + cashSales - withdraw - refund;
            cashSafeField.setText(df.format(expected) + " 원");

            // 실제 금고 금액 (사용자 입력)
            long real = parseLong(realCashField.getText());

            // 차액
            long diff = real - expected;
            diffField.setText(df.format(diff) + " 원");
            if(diff < 0) diffField.setForeground(Color.RED);
            else diffField.setForeground(Color.BLUE);

            // 인계 금액 = 실제 금고 금액 (다음 사람에게 넘겨줄 돈)
            handoverField.setText(df.format(real) + " 원");

        } catch (Exception e) {}
    }

    // 문자열 금액 파싱 헬퍼
    private long parseLong(String text) {
        if(text == null || text.trim().isEmpty()) return 0;
        return Long.parseLong(text.replaceAll("[^0-9]", ""));
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
        leftPanel.add(createInputRow("인수자", receiverName )); // 받아온 인수자 이름 사용
        leftPanel.add(Box.createVerticalStrut(30));

        // DB에서 매출 데이터 로드
        Map<String, Integer> sales = service.getSales(startTime, endTime);
        int cashS = sales.get("cash");
        int cardS = sales.get("card");
        int pcS = 0; // PC 사용료 로직 (임시 0)

        leftPanel.add(createHeader("매출"));
        pcSalesField = new JTextField(df.format(pcS) + " 원");
        productSalesField = new JTextField(df.format(cashS + cardS) + " 원"); // 상품 총액
        cashDepositField = new JTextField(df.format(cashS) + " 원"); // 현금 매출

        leftPanel.add(createFieldRow("PC 사용료", pcSalesField));
        leftPanel.add(createFieldRow("상품 판매액", productSalesField));
        leftPanel.add(createFieldRow("현금 입금액", cashDepositField));

        leftPanel.add(Box.createVerticalStrut(30));

        // 출금
        leftPanel.add(createHeader("출금"));

        // 입력 감지를 위한 리스너 생성
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateCalculation(); }
            public void removeUpdate(DocumentEvent e) { updateCalculation(); }
            public void changedUpdate(DocumentEvent e) { updateCalculation(); }
        };

        withdrawalField = new JTextField("0"); // 현금 출금
        withdrawalField.getDocument().addDocumentListener(dl);

        refundField = new JTextField("0");     // 환불 금액
        refundField.getDocument().addDocumentListener(dl);

        leftPanel.add(createFieldRow("현금 출금", withdrawalField, true)); // 입력 가능하도록 true
        leftPanel.add(createFieldRow("환불 금액", refundField, true));     // 입력 가능하도록 true

        leftPanel.add(Box.createVerticalStrut(30));

        // 합계
        totalSalesField = new JTextField(df.format(pcS + cashS + cardS) + " 원");
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
        cashSafeField = new JTextField(); // 전산상 금액 (계산됨)
        realCashField = new JTextField("0"); // 실제 금액 (입력)
        diffField = new JTextField();
        diffField.setForeground(Color.RED); // 차액 빨간색

        // 실제 금고 금액 입력 시 리스너 연결
        realCashField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateCalculation(); }
            public void removeUpdate(DocumentEvent e) { updateCalculation(); }
            public void changedUpdate(DocumentEvent e) { updateCalculation(); }
        });

        rightPanel.add(createFieldRow("금고 금액", cashSafeField));
        rightPanel.add(createFieldRow("실제 금고 금액", realCashField, true)); // [수정] 입력 가능
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createFieldRow("업무 차액", diffField));

        rightPanel.add(Box.createVerticalStrut(30));


        rightPanel.add(createHeader("인수 / 인계 금액"));

        // 인수 금액 = 이전 타임에서 받은 돈
        JTextField insuField = new JTextField(df.format(prevCashReserve) + " 원");

        // 인계 금액 = 다음 사람에게 줄 돈
        handoverField = new JTextField("0 원");

        rightPanel.add(createFieldRow("인수 금액", insuField));
        rightPanel.add(createFieldRow("인계 금액", handoverField));

        rightPanel.add(Box.createVerticalStrut(30));

        // 시간 확인
        rightPanel.add(createHeader("근무 시간 확인"));
        // 현재 시간 포맷팅
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String startStr = startTime.toLocalDateTime().format(dtf);
        String endStr = endTime.toLocalDateTime().format(dtf);

        // 실제 DB 시간 데이터 반영
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

        // 완료 버튼 클릭 시 DB 저장 로직
        done.addActionListener(e -> {
            HandOverDTO dto = new HandOverDTO();
            dto.setGiverId(giverName);
            dto.setReceiverId(receiverName);
            dto.setStartTime(startTime);
            dto.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
            dto.setTotalSales((int)parseLong(totalSalesField.getText()));
            dto.setCashSales((int)parseLong(cashDepositField.getText()));
            dto.setCardSales(dto.getTotalSales() - dto.getCashSales());
            dto.setCashReserve((int)parseLong(handoverField.getText())); // 인계 금액 저장
            dto.setMemo("업무차액: " + diffField.getText());

            // 저장 성공 시 금고 금액을 영속화(cash_safe 테이블에 저장)
            if(service.save(dto)) {
                // DB에 인수인계 기록이 저장되었으므로 현재 실제 금고(인계 금액)를 영속화
                try {
                    int newSafe = dto.getCashReserve();
                    service.updateCashSafe(newSafe);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                JOptionPane.showMessageDialog(this, "인수인계가 완료되었습니다.");
                parent.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "저장 실패");
            }
        });

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

    // 라벨 + 텍스트필드 (기존 메서드 오버로딩하여 수정 가능 여부 처리)
    private JPanel createFieldRow(String title, JTextField field) {
        return createFieldRow(title, field, false);
    }

    // 수정 가능 여부를 파라미터로 받는 메서드 오버로딩
    private JPanel createFieldRow(String title, JTextField field, boolean isEditable) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35)); // 높이 제한
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // 박스 전체 왼쪽 정렬

        JLabel rowLable= new JLabel(title);
        rowLable.setPreferredSize(new Dimension(100, 30));
        rowLable.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        // 파라미터에 따라 수정 가능 여부 설정
        field.setEditable(isEditable);
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
        //  인계자/인수자 이름은 자동 입력되므로 수정 불가
        return createFieldRow(title, tf, false);
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
