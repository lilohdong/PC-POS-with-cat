package client.order.ordermake;

import util.Sizes;

import javax.swing.*;
import java.awt.*;

public class OrderMakePanel extends JPanel{
    
    public OrderMakePanel(){
        initUI();
    }

    private void initUI(){
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, Sizes.PANEL_HEIGHT));
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(1, 3));
        add(mainPanel, BorderLayout.CENTER);


        int wLeft = (int)(Sizes.PANEL_WIDTH * 0.7);
        int wCenter = (int)(Sizes.PANEL_WIDTH * 0.2);
        int wRight = (int)(Sizes.PANEL_WIDTH * 0.1);

        OMLeft leftPanel = new OMLeft();
        leftPanel.setPreferredSize(new Dimension(wLeft, Sizes.PANEL_HEIGHT));
        leftPanel.setBackground(Color.WHITE);

        OMCenter centerPanel = new OMCenter();
        centerPanel.setPreferredSize(new Dimension(wCenter, Sizes.PANEL_HEIGHT));
        centerPanel.setBackground(Color.LIGHT_GRAY);

        OMRight rightPanel = new OMRight();
        rightPanel.setPreferredSize(new Dimension(wRight, Sizes.PANEL_HEIGHT)); 
        rightPanel.setBackground(Color.WHITE);
    }
}
