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
public class OMRight extends JPanel{

    public OMRight() {
        initUI();
    }

    private void initUI(){
        setLayout(new FlowLayout(FlowLayout.CENTER));

        //닫기버튼 추가
        JButton close = new JButton("X");
        close.setPreferredSize(new Dimension(60, 60));
        add(close);
    }
}
