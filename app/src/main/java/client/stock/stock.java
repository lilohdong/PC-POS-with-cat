package client.stock;

import client.stock.view.StockBottom;
import client.stock.view.StockHeader;
import client.stock.view.StockList;
import client.stock.view.StockSearch;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/*
재고 관리 화면 전체를 구성하는 메인 패널

레이아웃: BorderLayout
상단: StockHeader (제목)
중앙: 검색 + 목록 + 하단 기록 영역
*/
public class stock extends JPanel {
    public stock() {
        this.initUI();
    }

    /*
    전체 UI 구성 메소드
    상단 헤더, 검색창, 재고 목록 테이블, 하단 입출고/알림 기록 영역을 조합
    */
    public void initUI() {
        this.setLayout(new BorderLayout());
        this.setSize(1016, 832);
        this.setBackground(Color.WHITE);

        //상단 제목 영역
        StockHeader header = new StockHeader();
        this.add(header, "North");

        //중앙 영역: 검색 + 테이블 + 하단 기록 패널
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //하단 기록 패널을 먼저 생성 (StockList가 기록을 추가하기 위해 참조 필요 -> Bottom 참조를 받음)
        StockBottom bottom = new StockBottom();

        // 재고 목록 테이블 (하단 패널 참조 전달)
        StockList s_list = new StockList(bottom);

        // 검색 패널
        StockSearch search = new StockSearch();

        /*
        검색창에서 검색 버튼 클릭 → StockList에 검색 조건 전달
        search.setSearchAction은 Consumer<String[]> 타입을 받음 -> String[]을 받아서 사용
        */
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
            System.out.println("performSearch called: " + Arrays.toString(arr));
        });

        //필터 버튼(정상/부족/품절) 클릭 -> 해당 상태의 재고만 표시
        search.setFilterAction(filterType -> {
            s_list.performFilter(filterType);
        });

        //중앙 영역 조립
        center.add(search, "North");
        center.add(s_list, "Center");
        center.add(bottom, "South");
        this.add(center, "Center");
    }
}