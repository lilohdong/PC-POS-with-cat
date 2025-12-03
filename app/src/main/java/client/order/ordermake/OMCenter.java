package client.order.ordermake;

import javax.swing.*;
import java.awt.*;


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

    public OMCenter() {
        initUI();
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
        container.add(search);
        container.add(Box.createVerticalStrut(15));  // 여백


        JLabel title = new JLabel("주문내역");
        title.setAlignmentX(LEFT_ALIGNMENT);
        container.add(title);
        container.add(Box.createVerticalStrut(5));

        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> orderList = new JList<>(model);

        JScrollPane scrollBar = new JScrollPane(orderList);
        scrollBar.setAlignmentX(LEFT_ALIGNMENT);
        scrollBar.setPreferredSize(new Dimension(0, 300)); // 최소 높이
        scrollBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        container.add(scrollBar);
        container.add(Box.createVerticalStrut(15));


        JLabel totalPrice = new JLabel("가격: 0 원");
        totalPrice.setAlignmentX(LEFT_ALIGNMENT);
        container.add(totalPrice);
        container.add(Box.createVerticalStrut(15));


        JPanel payPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        payPanel.setAlignmentX(LEFT_ALIGNMENT);
        payPanel.add(new JButton("현금"));
        payPanel.add(new JButton("신용카드"));
        container.add(payPanel);
        container.add(Box.createVerticalStrut(15));


        JPanel seatPanel = new JPanel();
        seatPanel.setLayout(new BoxLayout(seatPanel, BoxLayout.Y_AXIS));
        seatPanel.setAlignmentX(LEFT_ALIGNMENT);
        seatPanel.setBorder(BorderFactory.createTitledBorder("고객 좌석"));

        JTextField seatField = new JTextField();
        seatField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        seatPanel.add(seatField);
        container.add(seatPanel);
        container.add(Box.createVerticalStrut(15));


        JPanel reqPanel = new JPanel();
        reqPanel.setLayout(new BoxLayout(reqPanel, BoxLayout.Y_AXIS));
        reqPanel.setAlignmentX(LEFT_ALIGNMENT);
        reqPanel.setBorder(BorderFactory.createTitledBorder("고객 요청"));

        JTextArea requestField = new JTextArea(4, 1);
        requestField.setLineWrap(true);
        requestField.setWrapStyleWord(true);

        JScrollPane reqScroll = new JScrollPane(requestField);
        reqScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        reqPanel.add(reqScroll);
        container.add(reqPanel);
        container.add(Box.createVerticalStrut(15));


        JButton submit = new JButton("접수");
        submit.setAlignmentX(LEFT_ALIGNMENT);
        submit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        container.add(submit);


        add(container, BorderLayout.CENTER);
    }
}
