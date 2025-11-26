package client.component;

import util.Sizes;

import javax.swing.*;
import java.awt.*;

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

        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(Sizes.SIDEBAR_WIDTH,64));
        topPanel.setBackground(Color.white);
        chmodBtn = new JButton("관리자 모드 변경");
        chmodBtn.setSize(new Dimension(Sizes.SIDEBAR_WIDTH,64));
        topPanel.add(chmodBtn);
        add(topPanel);

        add(Box.createVerticalStrut(17));

        initReal();
    }

    private JButton initBtn(String name) {
        JButton jBtn = new JButton(name);
        jBtn.setBackground(Color.white);
        jBtn.setBorder(BorderFactory.createLineBorder(Color.black, 1));

        setPreferredSize(new Dimension(200,63));

        return jBtn;
    }
    private void initReal() {
        manageBtn = initBtn("매장관리");
        add(manageBtn);
        add(Box.createVerticalStrut(Sizes.BOX_STRUT));
        orderBtn = initBtn("상품판매");
        add(orderBtn);
        add(Box.createVerticalStrut(Sizes.BOX_STRUT));
        stockBtn = initBtn("재고관리");
        add(stockBtn);
        add(Box.createVerticalStrut(Sizes.BOX_STRUT));
        memberBtn = initBtn("회원관리");
        add(memberBtn);
        add(Box.createVerticalStrut(Sizes.BOX_STRUT));
        handOverBtn = initBtn("인수인계");
        add(handOverBtn);
        add(Box.createVerticalStrut(Sizes.BOX_STRUT));
        salesBtn = initBtn("매출관리");
        add(salesBtn);
        add(Box.createVerticalStrut(Sizes.BOX_STRUT));
        staffBtn = initBtn("직원관리");
        add(staffBtn);
        add(Box.createVerticalStrut(Sizes.BOX_STRUT));
        gameBtn = initBtn("게임통계");
        add(gameBtn);
        add(Box.createVerticalStrut(Sizes.BOX_STRUT));
    }
}
