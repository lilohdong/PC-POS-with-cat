package client.sales;

import util.Sizes;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class SalesHeaderPanel extends JPanel {
    public SalesHeaderPanel() {
        initUI();
    }

    private void initUI() {
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, Sizes.SALES_HEADER_HEIGHT));
        setBackground(Color.WHITE);
        setBorder(new MatteBorder(0, 0, 2, 0, Color.BLACK));
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel titleLabel = new JLabel("매출 현황", JLabel.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 36));
        add(titleLabel);
    }
}
