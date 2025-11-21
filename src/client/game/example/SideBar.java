package client.game.example;
import java.awt.*;
import javax.swing.*;
import util.*;
public class SideBar extends JPanel{
    public SideBar() {
        initUI();
    }
    private void initUI() {
        setPreferredSize(new Dimension(Sizes.SIDEBAR_WIDTH, Sizes.SIDEBAR_HEIGHT));

        setBackground(Color.white);

        setBorder(BorderFactory.createLineBorder(Color.black, 1));
    }
}
