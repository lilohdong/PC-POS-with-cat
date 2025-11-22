package client.sales;

import javax.swing.*;
import java.awt.*;
import util.Sizes;
public class SalesMainPanel extends JPanel {
    public SalesMainPanel() {
        initUI();
    }
    private void initUI() {
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, Sizes.PANEL_HEIGHT));
        setLayout(new BorderLayout());

        SalesHeaderPanel shp = new SalesHeaderPanel();
        add(shp, BorderLayout.NORTH);

        SalesTablePanel stp = new SalesTablePanel();
        add(stp, BorderLayout.CENTER);
    }
}
