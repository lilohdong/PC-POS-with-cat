package client.sales;

import client.sales.menusales.SalesHeaderPanel;
import client.sales.menusales.SalesTablePanel;
import client.sales.timesales.TimeSalesHeaderPanel;
import client.sales.timesales.TimeSalesTablePanel;
import util.Sizes;

import javax.swing.*;
import java.awt.*;

public class SalesMainPanel extends JPanel {
    public SalesMainPanel() {
        initUI();
    }

    private void initUI() {
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, Sizes.PANEL_HEIGHT));
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, Sizes.PANEL_HEIGHT));
        // 메뉴 탭
        JPanel menuSalesPanel = new JPanel(new BorderLayout());
        SalesHeaderPanel shp = new SalesHeaderPanel();
        menuSalesPanel.add(shp, BorderLayout.NORTH);
        SalesTablePanel stp = new SalesTablePanel();
        menuSalesPanel.add(stp, BorderLayout.CENTER);

        // 시간 탭
        JPanel timeSalesPanel = new JPanel(new BorderLayout());
        TimeSalesHeaderPanel  tshp = new TimeSalesHeaderPanel();
        TimeSalesTablePanel tstp = new TimeSalesTablePanel();
        timeSalesPanel.add(tshp, BorderLayout.NORTH);
        timeSalesPanel.add(tstp, BorderLayout.CENTER);

        tabbedPane.addTab("상품매출",menuSalesPanel);
        tabbedPane.addTab("시간충전내역",timeSalesPanel);
        add(tabbedPane, BorderLayout.CENTER);
    }
}
