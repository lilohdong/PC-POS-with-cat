package client.stock;

import javax.swing.*;
import java.awt.*;
import client.stock.view.*;
import util.*;

public class stock extends JPanel{

    public stock(){
        initUI();
    }

    public void initUI(){
        setLayout(new BorderLayout());
        setSize(Sizes.PANEL_WIDTH, Sizes.PANEL_HEIGHT);
        setBackground(Color.WHITE);
        
        //최상단 헤더 패널
        StockHeader header = new StockHeader();
        add(header, BorderLayout.NORTH);



        //패널 -> 새로운 중앙 패널: 검색, 재고, 알림 패널을 insert
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //center.검색 패널
        StockSearch search = new StockSearch();
        center.add(search, BorderLayout.NORTH);

        //재고 테이블
        StockList s_list = new StockList();
        center.add(s_list, BorderLayout.CENTER);

        //알람 패널: 부족 등의 알림
        StockBottom bottom = new StockBottom();
        center.add(bottom, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);
    }
}
