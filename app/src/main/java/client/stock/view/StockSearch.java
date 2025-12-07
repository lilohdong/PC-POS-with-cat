package client.stock.view;

import java.awt.FlowLayout;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/*
재고 관리 화면 상단 검색 및 필터 영역

기능:
상품명, 상품코드, 카테고리로 검색
상태별 필터링 (정상 / 재고부족 / 품절)
*/
public class StockSearch extends JPanel {

    private JTextField m_name;                    // 상품명 입력 필드
    private JTextField m_code;                    // 상품코드 입력 필드
    private JComboBox<String> category;           // 카테고리 선택 콤보박스
    private JButton btnSearch;                    // 검색 실행 버튼
    private JButton btnNormal;                    // 정상 재고만 보기
    private JButton btnLack;                      // 재고 부족만 보기
    private JButton btnSoldout;                   // 품절만 보기

    // 외부에서 등록할 콜백
    private Consumer<String[]> searchCallback;                  // 검색 조건 전달 [name, code, category]
    private java.util.function.Consumer<String> filterCallback; // 필터 타입 전달 (NORMAL/LACK/SOLDOUT)

    public StockSearch() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        //입력 컴포넌트 생성
        m_name = new JTextField(10);
        m_code = new JTextField(10);
        String[] categories = new String[]{
                "전체",
                "면류",
                "밥/곡물",
                "육류/해산물",
                "채소/과일",
                "유제품/소스",
                "음료/액상",
                "스낵/과자",
                "조미료/양념",
                "기타 잡화"
        };
        category = new JComboBox<>(categories);

        btnSearch = new JButton("검색");
        btnNormal = new JButton("정상");
        btnLack = new JButton("재고부족");
        btnSoldout = new JButton("품절");

        //UI 배치
        this.add(new JLabel("상품명:"));
        this.add(m_name);
        this.add(new JLabel("상품코드:"));
        this.add(m_code);
        this.add(new JLabel("카테고리:"));
        this.add(category);
        this.add(btnSearch);
        this.add(btnNormal);
        this.add(btnLack);
        this.add(btnSoldout);

        //검색 버튼 클릭 -> 입력값을 배열로 만들어 콜백 호출
        btnSearch.addActionListener(e -> {
            if (searchCallback != null) {
                System.out.println("SEARCH CLICK: " + m_name.getText() + "," + category.getSelectedItem());
                searchCallback.accept(new String[]{m_name.getText(), m_code.getText(), (String) category.getSelectedItem()});
            }
        });

        // 필터 버튼들 -> 각 상태 타입 문자열 전달
        btnNormal.addActionListener(e -> {
            if (filterCallback != null) filterCallback.accept("NORMAL");
        });

        btnLack.addActionListener(e -> {
            if (filterCallback != null) filterCallback.accept("LACK");
        });

        btnSoldout.addActionListener(e -> {
            if (filterCallback != null) filterCallback.accept("SOLDOUT");
        });
    }

    //외부에서 검색 결과를 처리할 콜백 등록
    public void setSearchAction(java.util.function.Consumer<String[]> cb) {
        this.searchCallback = cb;
    }

    //외부에서 필터 결과를 처리할 콜백 등록
    public void setFilterAction(java.util.function.Consumer<String> cb) {
        this.filterCallback = cb;
    }

    /*  사용하지 X
    public void setSearchAction(Object cb) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setSearchAction'");
    }
    */
}