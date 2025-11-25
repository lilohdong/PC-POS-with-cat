package client.order;

import javax.swing.*;
import java.awt.*;

public class OrderState extends JPanel {
    public JButton cookingBtn;
    public JButton doneBtn;

    public OrderState() {
        setLayout(getLayout());
        setPreferredSize(new Dimension(1016, 60));
    }
}