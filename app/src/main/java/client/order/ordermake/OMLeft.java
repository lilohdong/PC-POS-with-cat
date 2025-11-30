package client.order.ordermake;

import javax.swing.*;
import java.awt.*;


/*
좌측 메뉴 선택 패널

-카테고리 버튼 10개
-메뉴 아이템 목록 (GridLayout로)
*/
public class OMLeft extends JPanel{

    public OMLeft() {
        initUI();
    }

    private void initUI(){
        setLayout(new BorderLayout());

        //상단 카테고리
        JPanel category = new JPanel();
        category.setLayout(new GridLayout(2, 5, 5, 5));

        String[] categoryList = {
            "전체", "인기메뉴", "라면", "볶음밥", "덮밥",
            "분식", "사이드", "음료", "과자", "기타/요청"
        };

        for (String c : categoryList){
            JButton btn = new JButton(c);
            category.add(btn);
        }

        //메뉴 리스트 패널-실제 메뉴들은 DB연동 후 추가 예정
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(2, 3, 10, 10));

        for(int i=0; i<6; i++){
            JPanel box = new JPanel();
            box.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            box.add(new JLabel("메뉴" + (i+1)));

            menuPanel.add(box);
        }

        
        add(category, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.CENTER);
    }
}
