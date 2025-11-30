package client.stock.view;

import javax.swing.*;
import java.awt.*;

public class StockSearch extends JPanel{
    public StockSearch(){
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JTextField m_name = new JTextField(10);
        JTextField m_code = new JTextField(10);
        String[] categories = {"전체", "라면", "음료", "사이드", "토핑"};
        JComboBox<String> category = new JComboBox<>(categories);

        JButton btnSearch = new JButton("검색");
        JButton btnNormal = new JButton("정상");
        JButton btnLack = new JButton("재고부족");
        JButton btnSoldout = new JButton("품절");

        
        //라인1: 검색창 + 검색버튼
        add(new JLabel("상품명:"));
        add(m_name);

        add(new JLabel("상품코드:"));
        add(m_code);

        add(new JLabel("카테고리:"));
        add(category);

        add(btnSearch);


        //라인2: 재고 상태필터
        add(btnNormal);
        add(btnLack);
        add(btnSoldout);
    }
}
