package client.member;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Member extends JFrame implements ActionListener{

    public Member(Memberbtns mbtns){
        add(mbtns,BorderLayout.NORTH);
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
            searchField = new JTextField();
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



    @Override
    public void actionPerformed(ActionEvent e){}
}


public class MemberList {
    public static void main(String[] args){
        Member member = new Member(mbtns);
        Member.Memberbtns mbtns = member.new Memberbtns();
        Member.SearchMember smem = member.new SearchMember();

        member.setTitle("회원 관리");
        member.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        member.setSize(1200,832);
        member.setVisible(true);
    }
}
