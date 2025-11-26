package client.component;

import util.Sizes;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        initUI();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("POS PLACE");
        setVisible(true);
    }

    private void initUI() {
        setSize(new Dimension(Sizes.FRAME_WIDTH,Sizes.FRAME_HEIGHT));
        setResizable(false);
        setLocationRelativeTo(null);

        SideBar sb = new SideBar();
        MainUI mui = new MainUI();

        sb.setNavListener(e-> {
            String clickedBtn =e.getActionCommand();
            switch (clickedBtn) {
                //case "매장관리" -> mui.showUI("");
                case "매출관리" -> mui.showUI("SALES");
                case "게임통계" -> mui.showUI("GAME");
                case "상품판매" -> mui.showUI("ORDER");
                case "회원관리" -> mui.showUI("MEMBER");
            }
        });

        add(sb, BorderLayout.WEST);
        add(mui, BorderLayout.CENTER);
    }
}