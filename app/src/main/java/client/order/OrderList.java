package client.order;

import javax.swing.*;
import java.awt.*;
import util.*;

import com.privatejgoodies.forms.layout.Sizes;

//상품판매 페이지 - 주문 목록 패널
public class OrderList extends JPanel {
    private JList<String> orderList;
    
    public OrderList() {
        setLayout(getLayout());
        setPreferredSize(new Dimension(1016, 675));
    }
}
