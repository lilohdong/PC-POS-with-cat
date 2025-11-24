package client.member;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Member extends JPanel implements ActionListener {

    public Member() {
        initUI();
    }
    private void initUI() {
        MemberHeader mb = new MemberHeader();
        SearchMember sm = new SearchMember();
        setLayout(new BorderLayout());

        add(mb, BorderLayout.NORTH);
        add(sm, BorderLayout.CENTER);
    }
    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
