package client.order.ordermake;

import client.order.controller.OrderController;
import dto.MenuDTO;
import dto.OrderDataDTO;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/*
중앙 패널

- 검색창
- 주문내역 리스트
- 총 금액
- 결제 수단
- 고객 좌석번호
- 고객 요청사항
- 접수 버튼
*/
public class OMCenter extends JPanel{

    private DefaultListModel<String> orderListModel;
    private JLabel totalPriceLabel;
    private JRadioButton cardRadio;
    private JRadioButton cashRadio;
    private JTextField seatField;
    private JTextArea requestField;
    private JButton submitButton;

    // 주문에 담긴 메뉴 및 수량 정보
    private List<MenuDTO> selectedMenus = new ArrayList<>();
    private List<Integer> quantities = new ArrayList<>();

    private OMLeft omLeft;
    public OMCenter() {
        initUI();
    }
    public void setOMLeft(OMLeft omLeft) {
        this.omLeft = omLeft;
    }

    private void initUI(){
        setLayout(new BorderLayout());

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JTextField search = new JTextField();
        search.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        search.setAlignmentX(LEFT_ALIGNMENT);
        search.setBorder(BorderFactory.createTitledBorder("메뉴 검색"));

        search.addActionListener(e -> {
            if (omLeft != null) {
                omLeft.searchMenus(search.getText());
            }
        });

        container.add(search);
        container.add(Box.createVerticalStrut(15));  // 여백


        JLabel title = new JLabel("주문내역");
        title.setAlignmentX(LEFT_ALIGNMENT);
        container.add(title);
        container.add(Box.createVerticalStrut(5));

        orderListModel = new DefaultListModel<>();
        JList<String> orderList = new JList<>(orderListModel);

        // 주문내역 취소 기능 추가
        orderList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && orderList.getSelectedIndex() != -1) {
                int index = orderList.getSelectedIndex();
                int confirm = JOptionPane.showConfirmDialog(this,
                        orderListModel.getElementAt(index) + "\n정말 취소하시겠습니까?", "주문 취소 확인",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    removeMenuItem(index); // 선택된 메뉴 제거
                }
                orderList.clearSelection();
            }
        });

        JScrollPane scrollBar = new JScrollPane(orderList);
        scrollBar.setAlignmentX(LEFT_ALIGNMENT);
        scrollBar.setPreferredSize(new Dimension(0, 300));
        scrollBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        container.add(scrollBar);
        container.add(Box.createVerticalStrut(15));


        totalPriceLabel = new JLabel("가격: 0 원");
        totalPriceLabel.setAlignmentX(LEFT_ALIGNMENT);
        container.add(totalPriceLabel);
        container.add(Box.createVerticalStrut(15));


        // 결제 수단 (CARD/CASH)
        JPanel payPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        payPanel.setAlignmentX(LEFT_ALIGNMENT);
        cardRadio = new JRadioButton("신용카드", true);
        cashRadio = new JRadioButton("현금");
        ButtonGroup payGroup = new ButtonGroup();
        payGroup.add(cardRadio);
        payGroup.add(cashRadio);
        payPanel.add(cardRadio);
        payPanel.add(cashRadio);
        container.add(payPanel);
        container.add(Box.createVerticalStrut(15));


        JPanel seatPanel = new JPanel();
        seatPanel.setLayout(new BoxLayout(seatPanel, BoxLayout.Y_AXIS));
        seatPanel.setAlignmentX(LEFT_ALIGNMENT);
        seatPanel.setBorder(BorderFactory.createTitledBorder("고객 좌석"));

        seatField = new JTextField();
        seatField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        container.add(seatPanel);
        container.add(Box.createVerticalStrut(15));
        seatPanel.add(seatField);


        JPanel reqPanel = new JPanel();
        reqPanel.setLayout(new BoxLayout(reqPanel, BoxLayout.Y_AXIS));
        reqPanel.setAlignmentX(LEFT_ALIGNMENT);
        reqPanel.setBorder(BorderFactory.createTitledBorder("고객 요청"));

        requestField = new JTextArea(4, 1);
        requestField.setLineWrap(true);
        requestField.setWrapStyleWord(true);

        JScrollPane reqScroll = new JScrollPane(requestField);
        reqScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        reqPanel.add(reqScroll);
        container.add(reqPanel);
        container.add(Box.createVerticalStrut(15));


        submitButton = new JButton("접수");
        submitButton.setAlignmentX(LEFT_ALIGNMENT);
        submitButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        submitButton.addActionListener(e -> processOrder());
        container.add(submitButton);


        add(container, BorderLayout.CENTER);
    }

    // 메뉴 추가 (요청 3번)
    public void addMenuItem(MenuDTO menu) {
        int index = -1;

        // 이미 주문 내역에 있는 메뉴인지 확인
        for (int i = 0; i < selectedMenus.size(); i++) {
            if (selectedMenus.get(i).getMenuId().equals(menu.getMenuId())) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            // 수량 증가
            int newQuantity = quantities.get(index) + 1;
            quantities.set(index, newQuantity);
            orderListModel.set(index, String.format("%s (%d개) - %d원", menu.getMName(), newQuantity, menu.getMPrice() * newQuantity));
        } else {
            // 신규 메뉴 추가
            selectedMenus.add(menu);
            quantities.add(1);
            orderListModel.addElement(String.format("%s (1개) - %d원", menu.getMName(), menu.getMPrice()));
        }

        updateTotalPrice();
    }

    // 메뉴 취소 (요청 3번)
    private void removeMenuItem(int index) {
        if (index >= 0 && index < selectedMenus.size()) {
            MenuDTO menu = selectedMenus.get(index);
            int quantity = quantities.get(index);

            if (quantity > 1) {
                // 수량 감소
                int newQuantity = quantity - 1;
                quantities.set(index, newQuantity);
                orderListModel.set(index, String.format("%s (%d개) - %d원", menu.getMName(), newQuantity, menu.getMPrice() * newQuantity));
            } else {
                // 완전히 제거
                selectedMenus.remove(index);
                quantities.remove(index);
                orderListModel.remove(index);
            }
            updateTotalPrice();
        }
    }

    // 총 가격 업데이트 (요청 3번)
    private void updateTotalPrice() {
        int total = 0;
        for (int i = 0; i < selectedMenus.size(); i++) {
            total += selectedMenus.get(i).getMPrice() * quantities.get(i);
        }
        totalPriceLabel.setText("가격: " + total + " 원");
    }

    // 주문 접수 처리 (요청 4번)
    private void processOrder() {
        if (selectedMenus.isEmpty()) {
            JOptionPane.showMessageDialog(this, "주문 내역이 없습니다.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int seatNum = Integer.parseInt(seatField.getText());
            if (seatNum <= 0 || seatNum > 100) {
                JOptionPane.showMessageDialog(this, "좌석 번호는 1부터 100 사이여야 합니다.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String payMethod = cardRadio.isSelected() ? "CARD" : "CASH";
            String requestment = requestField.getText();
            String mId = "NO_MEMBER"; // 임시 주문자 ID

            OrderDataDTO orderDTO = new OrderDataDTO(mId, seatNum, requestment, payMethod);

            // 1. 주문을 DB에 저장하고 OrderList에 추가
            String newOId = OrderController.addNewOrder(orderDTO, selectedMenus, quantities);

            if (newOId != null) {
                JOptionPane.showMessageDialog(this, "주문이 성공적으로 접수되었습니다. (주문번호: " + newOId + ")", "접수 완료", JOptionPane.INFORMATION_MESSAGE);
                // 2. 주문 프레임 닫기 (OrderMakeFrame의 dispose 호출 필요)
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window instanceof JFrame) {
                    window.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(this, "주문 접수 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "유효한 좌석 번호를 입력하세요.", "경고", JOptionPane.WARNING_MESSAGE);
        }
    }
}
