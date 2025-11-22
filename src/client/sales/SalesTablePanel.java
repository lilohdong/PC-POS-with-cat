package client.sales;

import util.Sizes;

import javax.swing.*;
import java.awt.*;

public class SalesTablePanel extends JPanel {
    public SalesTablePanel() {
        initUI();
    }
    private void initUI() {
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, Sizes.SALES_TABLE_HEIGHT));
    }
}
