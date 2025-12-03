package client.stock;

import client.stock.view.StockBottom;
import client.stock.view.StockHeader;
import client.stock.view.StockList;
import client.stock.view.StockSearch;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class stock extends JPanel {
    public stock() {
        this.initUI();
    }

    public void initUI() {
        this.setLayout(new BorderLayout());
        this.setSize(1016, 832);
        this.setBackground(Color.WHITE);
        StockHeader header = new StockHeader();
        this.add(header, "North");
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Bottom 먼저 생성하고, List는 Bottom 참조를 받음
        StockBottom bottom = new StockBottom();
        StockList s_list = new StockList(bottom);      // List가 Bottom에 기록을 전달하도록 변경
        StockSearch search = new StockSearch();

        // search.setSearchAction은 Consumer<String[]> 타입을 받음 -> String[]을 받아서 사용
        search.setSearchAction(arr -> {
            String name = "";
            String code = "";
            String category = "";

            if (arr != null) {
                if (arr.length > 0 && arr[0] != null) name = arr[0];
                if (arr.length > 1 && arr[1] != null) code = arr[1];
                if (arr.length > 2 && arr[2] != null) category = arr[2];
            }

            s_list.performSearch(name, code, category);
        });

        // 필터 액션은 Consumer<String> 이므로 기존 방식 유지
        search.setFilterAction(filterType -> {
            s_list.performFilter(filterType);
        });

        center.add(search, "North");
        center.add(s_list, "Center");
        center.add(bottom, "South");
        this.add(center, "Center");
    }
}