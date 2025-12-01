package client.component;

import client.game.GameMainPanel;
import client.member.Member;
import client.order.Order;
import client.sales.SalesMainPanel;
import client.stock.stock;
import client.store.StorePanel;
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

        gameMainPanel = new GameMainPanel();
        Member memberMainPanel = new Member();
        Order orderMainPanel = new Order();
        SalesMainPanel  salesMainPanel = new SalesMainPanel();
        StorePanel storeMainPanel = new StorePanel();
        stock stockMainPanel = new stock();
        add(storeMainPanel, "STORE");
        add(gameMainPanel, "GAME");
        add(orderMainPanel, "ORDER");
        add(salesMainPanel, "SALES");
        add(memberMainPanel, "MEMBER");
        add(stockMainPanel, "STOCK");

    }
    public void showUI(String title) {
        cl.show(this,title);
    }
}
