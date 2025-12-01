package client.handover;

import client.component.SideBar;
import javax.swing.*;
import java.awt.*;

public class HandOverFrame extends JFrame {

    public HandOverFrame() {

        setTitle("인수인계");
        setSize(1200, 832);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        SideBar sb = new SideBar();
        add(sb, BorderLayout.WEST);

        add(new HandOverLoginPanel(this), BorderLayout.CENTER);

        setVisible(true);
    }

    // 화면 전환 메서드
    public void changeToMain(String giverName) {
        // 기존 센터 패널(로그인) 제거
        Component centerComp = ((BorderLayout)getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (centerComp != null) {
            remove(centerComp);
        }

        add(new HandOverMainPanel(this, giverName), BorderLayout.CENTER);

        // 화면 갱신
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        new HandOverFrame();
    }
}
