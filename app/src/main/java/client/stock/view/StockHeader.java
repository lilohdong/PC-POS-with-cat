package client.stock.view;

import javax.swing.*;
import java.awt.*;
import util.*;

public class StockHeader extends JPanel {
    public StockHeader(){
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, 80));

        JLabel title = new JLabel("재고 관리");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 40));

        add(title);
    }
}