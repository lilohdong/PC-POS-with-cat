package client.component;

import client.store.StorePanel;
import client.game.GameMainPanel;
import client.member.Member;
import client.order.Order;
import client.sales.SalesMainPanel;
import util.Sizes;

import javax.swing.*;
import java.awt.*;

public class MainUI extends JPanel {
    private CardLayout cl;
    private GameMainPanel gameMainPanel;
    public MainUI() {
        initUI();
    }
    private void initUI() {
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH,Sizes.PANEL_HEIGHT));
        cl = new CardLayout();
        setLayout(cl);

        StorePanel storePanel = new StorePanel();
        gameMainPanel = new GameMainPanel();
        Member memberMainPanel = new Member();
        Order orderMainPanel = new Order();
        SalesMainPanel  salesMainPanel = new SalesMainPanel();

        add(storePanel, "STORE");
        add(gameMainPanel, "GAME");
        add(orderMainPanel, "ORDER");
        add(salesMainPanel, "SALES");
        add(memberMainPanel, "MEMBER");
    }
    public void showUI(String title) {
        cl.show(this,title);
    }
}
