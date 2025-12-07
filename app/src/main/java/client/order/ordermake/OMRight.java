package client.order.ordermake;

import javax.swing.*;
import java.awt.*;

/*
주문하기 창의 우측 영역
창 닫기 버튼(X)만 제공
*/
public class OMRight extends JPanel{

    public OMRight() {
        initUI();
    }

    /*
    우측 상단에 큰 "X" 버튼 배치
    클릭 시 현재 주문 창을 완전히 닫음
    창이 아닌 전체 화면을 이용하는 PC들의 경우 창에 있는 "X"버튼을 사용 못 할 경우를 생각해 배치
    */
    private void initUI(){
        setLayout(new BorderLayout());

        //닫기버튼 추가
        JButton close = new JButton("X");
        close.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        close.setPreferredSize(new Dimension(60, 60));

        close.addActionListener(c->{
            Window w = SwingUtilities.getWindowAncestor(close);
            if(w!=null){
                w.dispose();
            }
        });

        add(close, BorderLayout.NORTH);
    }
}