package client.component;

import util.Sizes;

import javax.swing.*;
import java.awt.*;
import java.sql.Array;
import java.util.ArrayList;

public class SideBar extends JPanel {
    public SideBar() {
        initUI();
    }

    private JButton manageBtn;
    private JButton orderBtn;
    private JButton stockBtn;
    private JButton memberBtn;
    private JButton handOverBtn;
    private JButton salesBtn;
    private JButton staffBtn;
    private JButton gameBtn;

    private JButton chmodBtn;
    private BoxLayout box;
    private void initUI() {
        setPreferredSize(new Dimension(Sizes.SIDEBAR_WIDTH, Sizes.SIDEBAR_HEIGHT));
        setBackground(Color.white);
        setBorder(BorderFactory.createLineBorder(Color.black, 1));

        box = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(box);

        chmodBtn = new JButton("관리자 모드 변경");
        chmodBtn.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        chmodBtn.setPreferredSize(new Dimension(Sizes.SIDEBAR_WIDTH,64));
        chmodBtn.setMaximumSize(new Dimension(Sizes.SIDEBAR_WIDTH,64));
        add(chmodBtn);

        add(Box.createVerticalStrut(17));

        initReal();
    }

    private JButton initBtn(String name) {
        JButton jBtn = new JButton(name);
        jBtn.setBackground(Color.white);
        jBtn.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        jBtn.setPreferredSize(new Dimension(Sizes.SIDEBAR_WIDTH,63));
        jBtn.setMaximumSize(new Dimension(200,63));
        return jBtn;
    }
    private void initReal() {
        JPanel[] p = new JPanel[8];
        for (int i = 0; i < p.length; i++) {
            p[i] = new JPanel(new FlowLayout(FlowLayout.CENTER));
        }

        manageBtn = initBtn("매장관리");
        orderBtn = initBtn("상품판매");
        stockBtn = initBtn("재고관리");
        memberBtn = initBtn("회원관리");
        handOverBtn = initBtn("인수인계");
        salesBtn = initBtn("매출관리");
        staffBtn = initBtn("직원관리");
        gameBtn = initBtn("게임통계");

        p[0].add(manageBtn);
        p[1].add(orderBtn);
        p[2].add(stockBtn);
        p[3].add(memberBtn);
        p[4].add(handOverBtn);
        p[5].add(salesBtn);
        p[6].add(staffBtn);
        p[7].add(gameBtn);

        for(int i = 0; i < p.length; i++){
            add(p[i]);
            add(Box.createVerticalStrut(Sizes.BOX_STRUT));
        }
    }
}
