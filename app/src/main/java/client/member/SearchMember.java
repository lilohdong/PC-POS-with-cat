package client.member;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class SearchMember extends JPanel implements ActionListener {

    private JComboBox<String> combo;
    private String[] searchMethod = {"전체검색", "이름", "아이디"};

    JTextField searchField;
    JButton searchBtn;

    public SearchMember() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        combo = new JComboBox<>(searchMethod);
        searchField = new JTextField(30);
        searchBtn = new JButton();


        add(combo);
        add(searchField);
        add(searchBtn);

        searchBtn.addActionListener(this);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public static class MTable extends JPanel implements ActionListener {

        Vector<String> columnNames;
        Vector<Vector<String>> rows;
        DefaultTableModel model;
        JTable table;
        JScrollPane scroll;

        public MTable() {

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

            setLayout(new BorderLayout());
            model = new DefaultTableModel(rows, columnNames);
            table = new JTable(model);
            scroll = new JScrollPane(table);


            add(scroll);
            add(table);


        }

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }
}
