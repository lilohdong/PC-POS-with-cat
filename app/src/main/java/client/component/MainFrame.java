package client.component;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatGradiantoDarkFuchsiaIJTheme;
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
                case "매장관리" -> mui.showUI("STORE");
                case "★ 매출관리" -> mui.showUI("SALES");
                case "ⓖ 게임통계" -> mui.showUI("GAME");
                case "♣ 상품판매" -> mui.showUI("ORDER");
                case "▣ 회원관리" -> mui.showUI("MEMBER");
                case "▒ 재고관리" -> mui.showUI("STOCK");
            }
        });
        // 다크모드 변경 버튼 람다 리스너
        sb.darkModeBtn.addActionListener(e-> {
            if(e.getActionCommand().equals("DarkMode")) {
                try {
                    // 다크모드로 전환
                    UIManager.setLookAndFeel(new FlatDarkFlatIJTheme());
                    sb.darkModeBtn.setText("LightMode");
                } catch (Exception ex) {
                    System.out.println("Dark Mode 변환 실패");
                }
            } else {
                try {
                    //라이트모드 다시 전환
                    UIManager.setLookAndFeel(new FlatIntelliJLaf());
                    sb.darkModeBtn.setText("DarkMode");
                } catch (Exception ex) {
                    System.out.println("Light Mode 변환 실패");
                }
            }
            SwingUtilities.updateComponentTreeUI(this);
        });
        add(sb, BorderLayout.WEST);
        add(mui, BorderLayout.CENTER);
    }
}