package client.member;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

class Member extends JFrame implements ActionListener{

    public Member(Memberbtns mbtns,SearchMember smem, MTable mtable ){

        setLayout( new BorderLayout());

        add(mbtns,BorderLayout.NORTH);
        add(smem,BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e){

    }
}

class Memberbtns extends JPanel implements ActionListener{

    JButton btnJoin, btnUpdate, btnDelete;

    Memberbtns(){
        setLayout(new FlowLayout(FlowLayout.LEFT));

        btnJoin = new JButton("가입");
        btnUpdate = new JButton("수정");
        btnDelete = new JButton("삭제");

        add(btnJoin);
        add(btnUpdate);
        add(btnDelete);

        btnJoin.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnDelete.addActionListener(this);

    }

    @Override
    public void  actionPerformed(ActionEvent e){

    }
}

class SearchMember extends JPanel implements ActionListener{

    JComboBox<String> combo;
    private String[] searchMethod = {"전체검색", "이름" , "아이디"};

    JTextField searchField;
    JButton searchBtn;

    SearchMember(){
        setLayout( new FlowLayout(FlowLayout.LEFT));

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
}

class MTable extends JPanel implements ActionListener{

    Vector<String> columnNames;
    Vector<Vector<String>> rows;
    DefaultTableModel model;
    JTable table;
    JScrollPane scroll;

    MTable(){

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

        setLayout( new BorderLayout());
        model = new DefaultTableModel(rows,columnNames);
        table = new JTable(model);
        scroll = new JScrollPane(table);


        add(scroll);
        add(table);


    }

    @Override
    public void  actionPerformed(ActionEvent e){

    }
}


public class MemberList {
    public static void main(String[] args){
        Memberbtns mbtns = new Memberbtns();
        SearchMember smem = new SearchMember();
        MTable mtable = new MTable();
        Member member = new Member(mbtns,smem, mtable);


        member.setTitle("회원 관리");
        member.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        member.setSize(1200,832);
        member.setVisible(true);
    }

}