package client.component;

import client.game.GameMainPanel;
import client.member.Member;
import client.order.Order;
import client.sales.SalesMainPanel;
import util.Sizes;

import javax.swing.*;
import java.awt.*;

public class MainUI extends JPanel {
    private CardLayout cl;

    public MainUI() {
        initUI();
    }
    private void initUI() {
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH,Sizes.PANEL_HEIGHT));
        cl = new CardLayout();
        setLayout(cl);

        GameMainPanel gameMainPanel = new GameMainPanel();
        Member memberMainPanel = new Member();
        Order orderMainPanel = new Order();
        SalesMainPanel  salesMainPanel = new SalesMainPanel();

    }
}
