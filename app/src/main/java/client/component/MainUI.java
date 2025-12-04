package client.component;

import client.game.GameMainPanel;
import client.member.Member;
import client.order.Order;
import client.sales.SalesMainPanel;
import client.staff.view.StaffPanel;
import client.stock.stock;
import client.store.view.StorePanel;
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
        StaffPanel staffPanel = new StaffPanel();
        add(storeMainPanel, "STORE");
        add(gameMainPanel, "GAME");
        add(orderMainPanel, "ORDER");
        add(salesMainPanel, "SALES");
        add(memberMainPanel, "MEMBER");
        add(stockMainPanel, "STOCK");
        add(staffPanel, "STAFF");
    }
    public void showUI(String title) {
        cl.show(this,title);
    }
}
