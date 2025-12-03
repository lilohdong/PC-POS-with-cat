package client.handover;

import dto.HandOverDTO;
import service.HandOverService;

import javax.swing.*;
import java.awt.*;

public class HandOverFrame extends JFrame {

    private final HandOverService service;
    String currentGiver;

    public HandOverFrame() {
        this.service = new HandOverService(); // 서비스 초기화

        setTitle("인수인계");
        setSize(800, 832);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 현재 근무자 정보 가져오기
        // getInitialGiver()는 DAO에서 마지막 receiver_id를 가져오거나 "사장님" 반환
        this.currentGiver = service.getInitialGiver();

        // 로그인 패널에 현재 근무자 이름 전달 (한 번만 추가)
        add(new HandOverLoginPanel(this, currentGiver), BorderLayout.CENTER);

        setVisible(true);
    }

    // 화면 전환 메서드
    public void changeToMain(String giverName,String receiverName) {
        // 기존 센터 패널(로그인) 제거
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
