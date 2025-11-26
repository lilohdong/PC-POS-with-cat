package client.order;

import javax.swing.*;
import java.awt.*;
import util.*;

//상품판매 페이지 - 조리 상태 구분 패널
public class OrderState extends JPanel {
    private JButton cookingBtn;
    private JButton doneBtn;
    private JButton addMenu;

    public OrderState() {
        setLayout(getLayout());
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, 60));

        //조리중 메뉴 목록 버튼
        cookingBtn = new JButton("조리중");
        //조리 완료 메뉴 목록 버튼
        doneBtn = new JButton("조리 완료");
        //메뉴 추가 버튼
        addMenu = new JButton("+");

        add(cookingBtn, BorderLayout.WEST);
        add(doneBtn, BorderLayout.CENTER);
        add(addMenu, BorderLayout.EAST);
    }
}