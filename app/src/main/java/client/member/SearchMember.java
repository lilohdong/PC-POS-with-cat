package client.member;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

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
        columnNames.add("번호");
        columnNames.add("연령대");
        columnNames.add("이름");
        columnNames.add("아이디");
        columnNames.add("생년월일");
        columnNames.add("성별");
        columnNames.add("나이");
        columnNames.add("잔여시간");
        columnNames.add("휴대폰");

        rows = new Vector<>();

        tablePanel.setLayout(new BorderLayout());
        model = new DefaultTableModel(rows, columnNames);
        table = new JTable(model);
        scroll = new JScrollPane(table);

        tablePanel.add(scroll);
        tablePanel.add(table);



    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
