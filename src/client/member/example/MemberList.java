package client.member.example;

import client.member.Member;

import javax.swing.*;


public class MemberList extends JFrame {
    private MemberList() {
        Member member = new Member();

        add(member);
        setTitle("회원 관리");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200,832);
        setVisible(true);
    }
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            new MemberList();
        });
    }

}