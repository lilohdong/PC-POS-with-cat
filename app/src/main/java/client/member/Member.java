package client.member;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Member extends JPanel {

    public Member() {

        initUI();
    }

    private void initUI() {
        SearchMember sm = new SearchMember();
        MemberHeader mb = new MemberHeader(sm);

        setLayout(new BorderLayout());

        add(mb, BorderLayout.NORTH);
        add(sm, BorderLayout.CENTER);
    }
}
