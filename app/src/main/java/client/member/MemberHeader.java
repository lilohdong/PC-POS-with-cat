package client.member;

import util.Sizes;

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
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, Sizes.MEMBER_HEADER_HEIGHT ));
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
        Object src = e.getSource();

        // 현재 패널을 포함한 상위 JFrame을 찾아 Dialog의 부모로 사용
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);

        if (src == btnJoin) {
            new JoinDialog(parent);
        }
//        else if (src == btnUpdate) {
//            new UpdateDialog(parent).setVisible(true);
//        }
//        else if (src == btnDelete) {
//            new DeleteConfirmDialog(parent).setVisible(true);
//        }
    }
}
