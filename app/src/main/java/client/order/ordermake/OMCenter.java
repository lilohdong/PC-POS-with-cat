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

        //검색창
        JTextField search = new JTextField("메뉴 검색");
        container.add(search);

        //주문내역 영역
        JLabel title = new JLabel("주문내역");
        container.add(title);

        JList<String> orderList = new JList<>(new DefaultListModel<>());
        JScrollPane scrollBar = new JScrollPane(orderList);
        container.add(scrollBar);

        //가격(합산)
        JLabel totalPrice = new JLabel("가격: 0 원");
        container.add(totalPrice);

        // 결제수단
        JPanel payPanel = new JPanel();
        payPanel.add(new JButton("현금"));
        payPanel.add(new JButton("신용카드"));
        container.add(payPanel);

        // 좌석번호
        container.add(new JLabel("고객 좌석:"));
        JTextField seatField = new JTextField();
        container.add(seatField);

        // 요청사항
        container.add(new JLabel("고객 요청:"));
        JTextField requestField = new JTextField();
        container.add(requestField);

        // 접수 버튼
        JButton submit = new JButton("접수");
        container.add(submit);


        add(container, BorderLayout.CENTER);
    }
}
