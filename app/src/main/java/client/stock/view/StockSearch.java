package client.stock.view;

import java.awt.FlowLayout;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class StockSearch extends JPanel {
    private JTextField m_name;
    private JTextField m_code;
    private JComboBox<String> category;
    private JButton btnSearch;
    private JButton btnNormal;
    private JButton btnLack;
    private JButton btnSoldout;
    private Consumer<String[]> searchCallback; // accepts [name, code, category]
    private java.util.function.Consumer<String> filterCallback;

    public StockSearch() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
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

        // UI 버튼에 내부 리스너 등록 -> 밖에 등록된 콜백 호출
        btnSearch.addActionListener(e -> {
            if (searchCallback != null) {
                System.out.println("SEARCH CLICK: " + m_name.getText() + "," + category.getSelectedItem());
                searchCallback.accept(new String[]{m_name.getText(), m_code.getText(), (String) category.getSelectedItem()});
            }
        });

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

    // 외부에서 콜백 등록
    public void setSearchAction(java.util.function.Consumer<String[]> cb) {
        this.searchCallback = cb;
    }

    public void setFilterAction(java.util.function.Consumer<String> cb) {
        this.filterCallback = cb;
    }

    public void setSearchAction(Object cb) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setSearchAction'");
    }
}