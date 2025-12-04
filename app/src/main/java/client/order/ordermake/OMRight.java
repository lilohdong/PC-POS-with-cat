package client.order.ordermake;

import javax.swing.*;
import java.awt.*;

public class OMRight extends JPanel{

    public OMRight() {
        initUI();
    }

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