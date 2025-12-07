package client.order.view;

import client.order.controller.OrderController;
//import client.order.model.OrderData; OrderData 삭제됨
import dto.OrderDataDTO;
import util.Sizes;

import java.time.format.DateTimeFormatter;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/*
주문 목록을 실시간으로 표시하는 핵심 패널

조리중 / 조리완료 모드에 따라 다른 주문 리스트 표시
각 주문은 수평 패널 형태로 표시 (좌석, 시간, 메뉴, 버튼 등)
조리중: "준비완료" + "취소" 버튼
조리완료: "취소(환불)" 버튼만 표시
*/
public class OrderList extends JPanel {
    private JPanel listAreaPanel;

    public static final int COOKING_MODE = 0;   // 조리중 주문 표시 모드
    public static final int DONE_MODE = 1;      // 조리완료 주문 표시 모드
    private int currentMode = COOKING_MODE;

    public OrderList() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, 675));

        // 주문 항목들이 세로로 쌓이는 영역
        listAreaPanel = new JPanel();
        listAreaPanel.setLayout(new BoxLayout(listAreaPanel, BoxLayout.Y_AXIS));
        listAreaPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(listAreaPanel);
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setUnitIncrement(50);  // 스크롤 감도 증가
        // 스크롤 가능하도록 JScrollPane으로 감쌈
        add(scrollPane, BorderLayout.CENTER);
    }

    //OrderController에서 호출: DB에서 가져온 주문 리스트를 화면에 표시
    public void displayOrdersDTO(List<OrderDataDTO> targetList) {
        listAreaPanel.removeAll();

        for (OrderDataDTO order : targetList) {
            listAreaPanel.add(createOrderPanel(order));
        }

        revalidate();
        repaint();
    }

    //하나의 주문을 시각적으로 표현하는 패널 생성
    private JPanel createOrderPanel(OrderDataDTO order) {
        JPanel menu = new JPanel();
        menu.setLayout(new BorderLayout());
        menu.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        menu.setMaximumSize(new Dimension(Sizes.PANEL_WIDTH, 150)); // 패널 높이 고정

        //좌측 정보 패널
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(0, 1));
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        //기본 정보 표시
        infoPanel.add(new JLabel("좌석: " + order.getSeatNum() + " (" + order.getOId() + ")"));
        infoPanel.add(new JLabel("주문시간: " + order.getOTime().format(timeFormatter)));

        if (currentMode == COOKING_MODE) {
            infoPanel.add(new JLabel("경과시간: " + order.getCookingTime() + "분"));
        }

        //주문 상세 내역 (DAO에서 조회)
        String details = OrderController.getOrderDetails(order.getOId());
        JLabel detailLabel = new JLabel("주문내역: " + details);
        infoPanel.add(detailLabel);

        //우측 액션 버튼 영역
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setPreferredSize(new Dimension(150, 150));

        // 상세정보 보기 버튼
        JButton detailBtn = new JButton("+");
        detailBtn.addActionListener(e -> showOrderDetailFrame(order));
        actionPanel.add(detailBtn);

        JButton cancelBtn = new JButton("취소");

        if (currentMode == COOKING_MODE) {
            //조리중일 때만 "준비완료" 버튼 표시
            JButton finishBtn = new JButton("준비완료");
            finishBtn.addActionListener(e -> {
                OrderController.markAsDone(order.getOId());
            });
            actionPanel.add(finishBtn);

            //조리중 목록 취소 버튼
            cancelBtn.addActionListener(e -> {

                //환불/취소 처리 (Sales 차감)
                OrderController.processCancel(order);
            });
            actionPanel.add(cancelBtn);

        } else {
            //조리완료 상태일 때 추가 정보 표시
            infoPanel.add(new JLabel("완료시간: " + order.getCompleteTime().format(timeFormatter)));
            infoPanel.add(new JLabel("걸린시간: " + order.getFinishCookingTime() + "분"));

            // 완료 목록 취소 버튼 (환불 처리)
            cancelBtn.addActionListener(e -> {
                // 환불 처리 (Sales 차감)
                OrderController.processCancel(order);
            });
            actionPanel.add(cancelBtn);
        }

        menu.add(infoPanel, BorderLayout.CENTER);
        menu.add(actionPanel, BorderLayout.EAST);
        return menu;
    }

    // 주문 상세 정보 프레임 표시하는 메서드
    private void showOrderDetailFrame(OrderDataDTO order) {
        JFrame detailFrame = new JFrame("주문 상세 정보: " + order.getOId());
        detailFrame.setSize(400, 300);
        detailFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // OrderController에서 상세 정보 (요청 사항, 결제 수단 등) 추가 조회 필요
        panel.add(new JLabel("주문번호: " + order.getOId()));
        panel.add(new JLabel("좌석번호: " + order.getSeatNum()));
        panel.add(new JLabel("총 결제액: " + order.getTotalAmount() + "원"));
        panel.add(new JLabel("주문 상태: " + order.getOStatus()));
        panel.add(new JLabel("주문 내역: " + OrderController.getOrderDetails(order.getOId())));
        panel.add(new JLabel("요청 사항: " + (order.getRequestment() != null ? order.getRequestment() : "없음")));
        panel.add(new JLabel("결제 수단: " + (order.getPayMethod() != null ? order.getPayMethod() : "미지정")));

        detailFrame.add(panel);
        detailFrame.setVisible(true);
    }

    public void setMode(int mode) {
        currentMode = mode;
    }

    public int getCurrentMode() {
        return currentMode;
    }
}