package client.order.ordermake;

import util.Sizes;

import javax.swing.*;
import java.awt.*;

/*
"주문하기" 창의 메인 패널

좌/중/우 3분할 레이아웃으로 구성
좌측 (70%): 메뉴 목록 (OMLeft)
중앙 (20%): 주문 내역, 결제 정보 입력 (OMCenter)
우측 (10%): 닫기 버튼 (OMRight)
*/
public class OrderMakePanel extends JPanel{

    private OMLeft leftPanel;
    private OMCenter centerPanel;
    private OMRight rightPanel;

    public OrderMakePanel(){
        initUI();
    }

    //전체 레이아웃 구성 및 각 패널 초기화
    private void initUI(){
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, Sizes.PANEL_HEIGHT));
        setLayout(new BorderLayout());

        //화면 비율에 따라 너비 계산
        int wLeft = (int)(Sizes.PANEL_WIDTH * 0.7);
        int wCenter = (int)(Sizes.PANEL_WIDTH * 0.2);
        int wRight = (int)(Sizes.PANEL_WIDTH * 0.1);

        //중앙 패널: 주문 내역 및 결제 정보
        centerPanel = new OMCenter();
        centerPanel.setPreferredSize(new Dimension(wCenter, Sizes.PANEL_HEIGHT));
        centerPanel.setBackground(Color.LIGHT_GRAY);

        //좌측 패널: 메뉴 카테고리 + 메뉴 리스트
        leftPanel = new OMLeft(centerPanel);
        leftPanel.setPreferredSize(new Dimension(wLeft, Sizes.PANEL_HEIGHT));
        leftPanel.setBackground(Color.WHITE);

        //우측 패널: 창 닫기 버튼
        rightPanel = new OMRight();
        rightPanel.setPreferredSize(new Dimension(wRight, Sizes.PANEL_HEIGHT)); 
        rightPanel.setBackground(Color.WHITE);

        //BorderLayout으로 배치
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        //검색 기능 연동을 위해 좌측 패널 전달
        centerPanel.setOMLeft(leftPanel);
    }

    //외부에서 중앙 패널에 접근할 수 있도록 getter 제공
    public OMCenter getCenterPanel() {
        return centerPanel;
    }
}
