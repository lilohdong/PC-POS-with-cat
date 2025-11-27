package client.member.example;

import client.component.SideBar;
import client.member.Member;

import javax.swing.*;
import java.awt.*;


public class MemberList extends JFrame {
    private MemberList() {
        Member member = new Member();
        SideBar sb = new SideBar();

        add(sb,BorderLayout.WEST);
        add(member, BorderLayout.CENTER);

        setTitle("회원 관리");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 832);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MemberList();
        });
    }

}