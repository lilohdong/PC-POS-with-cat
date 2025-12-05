package client.order.ordermake;

import util.Sizes;

import javax.swing.*;
import java.awt.*;

public class OrderMakePanel extends JPanel{

    private OMLeft leftPanel;
    private OMCenter centerPanel;
    private OMRight rightPanel;

    public OrderMakePanel(){
        initUI();
    }

    private void initUI(){
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, Sizes.PANEL_HEIGHT));
        setLayout(new BorderLayout());

        int wLeft = (int)(Sizes.PANEL_WIDTH * 0.7);
        int wCenter = (int)(Sizes.PANEL_WIDTH * 0.2);
        int wRight = (int)(Sizes.PANEL_WIDTH * 0.1);

        centerPanel = new OMCenter();
        centerPanel.setPreferredSize(new Dimension(wCenter, Sizes.PANEL_HEIGHT));
        centerPanel.setBackground(Color.LIGHT_GRAY);

        leftPanel = new OMLeft(centerPanel);
        leftPanel.setPreferredSize(new Dimension(wLeft, Sizes.PANEL_HEIGHT));
        leftPanel.setBackground(Color.WHITE);

        rightPanel = new OMRight();
        rightPanel.setPreferredSize(new Dimension(wRight, Sizes.PANEL_HEIGHT)); 
        rightPanel.setBackground(Color.WHITE);

        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        centerPanel.setOMLeft(leftPanel);
    }

    public OMCenter getCenterPanel() {
        return centerPanel;
    }
}
