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

    public MainUI() {
        initUI();
    }
    // 메인 UI 초기화, 카드 레이아웃
    private void initUI() {
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH,Sizes.PANEL_HEIGHT));
        cl = new CardLayout();
        setLayout(cl);

        GameMainPanel gameMainPanel = new GameMainPanel();
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
    // 카드 레이아웃 변경 메소드 생성, MainFrame에서 컨트롤
    public void showUI(String title) {
        cl.show(this,title);
    }
}
