package client.member;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MemberHeader extends JPanel implements ActionListener {

    private JButton btnJoin, btnUpdate, btnDelete;

    public MemberHeader() {
        initUI();
    }
    private void initUI() {

        setLayout(new FlowLayout(FlowLayout.LEFT));

        btnJoin = new JButton("가입");
        btnUpdate = new JButton("수정");
        btnDelete = new JButton("삭제");

        add(btnJoin);
        add(btnUpdate);
        add(btnDelete);

        btnJoin.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnDelete.addActionListener(this);// 나이ㅡ
    }
    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
