package client.member;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import util.Sizes;

public class SearchMember extends JPanel implements ActionListener {

    private final JComboBox<String> combo;
    private final String[] searchMethod = {"전체검색", "이름", "아이디"};

    JTextField searchField;
    JButton searchBtn;
    JLabel total;

    Vector<String> columnNames;
    Vector<Vector<String>> rows;
    DefaultTableModel model;
    JTable table;
    JScrollPane scroll;

    public SearchMember() {

        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, Sizes.PANEL_HEIGHT));

        setLayout(new BorderLayout());
        JPanel searchPanel = new JPanel();
        JPanel tablePanel = new JPanel();

        //총 회원 수
        total = new JLabel("총 회원 수 : ");

        add(searchPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(total, BorderLayout.SOUTH);


        //회원 검색
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        combo = new JComboBox<>(searchMethod);
        searchField = new JTextField(30);
        searchBtn = new JButton();

        searchPanel.add(combo);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        searchBtn.addActionListener(this);

        //회원 테이블
        columnNames = new Vector<>();
        columnNames.add("연령대");
        columnNames.add("이름");
        columnNames.add("아이디");
        columnNames.add("생년월일");
        columnNames.add("성별");
        columnNames.add("나이");
        columnNames.add("잔여시간");
        columnNames.add("휴대폰");

        // 수정 불가 모델
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;   // 모든 셀 수정 불가
            }
        };

        rows = new Vector<>();

        tablePanel.setLayout(new BorderLayout());
        table = new JTable(model);
        scroll = new JScrollPane(table);


        // 마지막 컬럼 가변길이 설정
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        //헤더 이동 및 크기 조절 불가
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        //컬럼 별 크기 고정(마지막 크기는 남는 공간 가변 길이로 공백)
        int[] preferredWidths = {100, 100, 130, 130, 60, 60, 170 };

        // 배열의 길이와 컬럼 수 중 작은 값 만큼 반복, i번째 컬럼을 가져와 크기 적용 조건
        for (int i = 0; i < preferredWidths.length && i < table.getColumnModel().getColumnCount(); i++) {
            TableColumn col = table.getColumnModel().getColumn(i); // 컬럼 객체 생성
            col.setPreferredWidth(preferredWidths[i]); // 컬럼 객체 기본 크기
            col.setMinWidth(preferredWidths[i]);       // 컬럼 최소 크기
            col.setMaxWidth(preferredWidths[i]);       // 컬럼 최대 크기
            col.setResizable(false);                   // 사용자가 컬럼 크기 조절 불가
        }

        // 가운데 정렬
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRender);
        }

        tablePanel.add(scroll);
        addDummyData();

        updateTotal();

    }
    //더미 데이터
    private void addDummyData() {
        Vector<String> dummydata = new Vector<>();
        dummydata.add("초등학생");
        dummydata.add("오동준");
        dummydata.add("dhehdwns");
        dummydata.add("2003-09-02");
        dummydata.add("남");
        dummydata.add("23");
        dummydata.add("15:07");
        dummydata.add("010-1234-124");
        model.addRow(dummydata);
    }

    // 총 회원수 갱신
    private void updateTotal() {
        total.setText("총 회원 수 : " + model.getRowCount());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchBtn) {
            // 더미 데이터 ( 나중에 DB 연결 할 자리)
            addDummyData();

            updateTotal();

        }

    }
}
