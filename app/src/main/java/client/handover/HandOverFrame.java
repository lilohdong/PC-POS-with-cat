package client.handover;

import service.HandOverService;

import javax.swing.*;
import java.awt.*;

public class HandOverFrame extends JFrame {

    private final HandOverService service;
    String currentGiver;

    public HandOverFrame() {
        this.service = new HandOverService(); // 서비스 초기화

        setTitle("인수인계");
        setSize(900, 832);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 현재 근무자 정보 가져오기
        this.currentGiver = service.getInitialData().getReceiverId();

        // 로그인 패널에 현재 근무자 이름 전달 (한 번만 추가)
        add(new HandOverLoginPanel(this, currentGiver), BorderLayout.CENTER);

        setVisible(true);
    }

    public HandOverService getService() {
        return service;
    }

    // 화면 전환 메서드
    public void changeToMain(String giverName,String receiverName) {
        Component centerComp = ((BorderLayout)getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (centerComp != null) {
            remove(centerComp);
        }
        add(new HandOverMainPanel(this, service, giverName, receiverName), BorderLayout.CENTER);

        // 화면 갱신
        revalidate();
        repaint();
    }
}
