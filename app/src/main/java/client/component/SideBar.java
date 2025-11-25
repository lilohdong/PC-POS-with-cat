package client.component;

import util.Sizes;

import javax.swing.*;
import java.awt.*;

public class SideBar extends JPanel {
    public SideBar() {
        initUI();
    }

    private void initUI() {
        setPreferredSize(new Dimension(Sizes.SIDEBAR_WIDTH, Sizes.SIDEBAR_HEIGHT));

        setBackground(Color.white);

        setBorder(BorderFactory.createLineBorder(Color.black, 1));
    }
}
