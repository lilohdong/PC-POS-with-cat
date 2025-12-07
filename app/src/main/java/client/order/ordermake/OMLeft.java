package client.order.ordermake;

import java.util.List;

import javax.swing.*;
import java.awt.*;

import dao.MenuDAO;
import dto.MenuDTO;

/*
주문하기 창의 좌측 영역: 메뉴 선택 패널

상단: 카테고리 버튼 10개 (2행 5열)
하단: 선택된 카테고리의 메뉴들을 3열 그리드로 표시
메뉴 클릭 -> OMCenter의 장바구니에 추가
*/
public class OMLeft extends JPanel{

    //메뉴 아이템들이 들어갈 동적 패널
    private JPanel menuPanel;
    private MenuDAO menuDAO = new MenuDAO();
    private final OMCenter omCenter;
    // 메뉴 추가를 위해 참조 보관
    private String[] categoryList = {
            "전체", "인기메뉴", "라면", "볶음밥", "덮밥",
            "분식", "사이드", "음료", "과자", "기타/요청"
    };

    public OMLeft(OMCenter omCenter) {
        this.omCenter = omCenter;
        initUI();
        loadMenusByCategory("전체"); // 초기 메뉴 목록 로드
    }

    //좌측 패널 UI 구성: 카테고리 영역 + 스크롤 가능한 메뉴 영역
    private void initUI(){
        setLayout(new BorderLayout());

        //상단: 카테고리 버튼들
        JPanel category = new JPanel();
        category.setLayout(new GridLayout(2, 5, 5, 5));

        for (String c : categoryList){
            JButton btn = new JButton(c);
            btn.addActionListener(e -> loadMenusByCategory(c));
            category.add(btn);
        }

        //중앙: 메뉴 아이템들이 들어갈 패널 (FlowLayout으로 동적 배치)
        menuPanel = new JPanel();
        menuPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(category, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    //카테고리 클릭 또는 검색 시 호출 -> 해당 메뉴들만 표시
    public void loadMenusByCategory(String categoryName) {
        List<MenuDTO> menus = menuDAO.getMenusByCategory(categoryName);
        updateMenuPanel(menus);
    }

    //중앙 패널의 검색창에서 Enter 입력 시 호출
    public void searchMenus(String keyword) {
        List<MenuDTO> menus = menuDAO.searchMenus(keyword);
        updateMenuPanel(menus);
    }

    /*
    메뉴 목록을 새로 그리기 위한 핵심 메서드
    3열 고정, 세로 스크롤 유도, 빈 화면 방지 처리 포함
    */
    private void updateMenuPanel(List<MenuDTO> menus) {
        menuPanel.removeAll();

        if (menus.isEmpty()) {
            menuPanel.add(new JLabel("해당 카테고리 메뉴가 없습니다."));
        } else {
            for(MenuDTO menu : menus){
                menuPanel.add(createMenuBox(menu));
            }
        }

        //고정 너비 계산: (메뉴 박스 150px * 3열) + (간격 약 10px * 4개) = 490px
        int columnCount = 3;
        int menuWidth = 150;
        int gap = 10;
        int preferredWidth = (menuWidth * columnCount) + (gap * (columnCount + 1)) + 5; // 약 500px

        //동적 높이 계산 (행 수에 따라 자동 조정 - ex: 3열 당 180px)
        int menuCount = menus.size();
        int rowCount = (int) Math.ceil((double) menuCount / columnCount);
        int menuHeight = 180;
        int preferredHeight = (menuHeight * rowCount) + (gap * rowCount);

        // 데이터가 없을 때 빈 화면 방지
        if (menuCount == 0) {
            preferredHeight = 400;
        }

        menuPanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        menuPanel.revalidate();
        menuPanel.repaint();
    }

    /*
    단일 메뉴를 시각적으로 표현하는 박스 생성
    클릭 시 OMCenter.addMenuItem() 호출
    */
    private JPanel createMenuBox(MenuDTO menu) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        box.setPreferredSize(new Dimension(150, 150));
        box.setMinimumSize(new Dimension(150, 150));

        JLabel nameLabel = new JLabel(menu.getMName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));

        JLabel priceLabel = new JLabel(menu.getMPrice() + "원", SwingConstants.CENTER);
        priceLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));

        // 이미지 영역 (임시)
        JPanel imagePlaceholder = new JPanel();
        imagePlaceholder.setPreferredSize(new Dimension(0, 100));
        imagePlaceholder.setBackground(Color.LIGHT_GRAY);
        JLabel imageLabel = new JLabel("이미지 없음", SwingConstants.CENTER);
        imagePlaceholder.add(imageLabel);

        //모든 영역에 동일한 클릭 리스너 부착
        java.awt.event.MouseAdapter clickHandler = new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (omCenter != null) {
                    omCenter.addMenuItem(menu);
                    evt.consume();
                }
            }
        };

        box.addMouseListener(clickHandler);
        imagePlaceholder.addMouseListener(clickHandler);
        imageLabel.addMouseListener(clickHandler);
        nameLabel.addMouseListener(clickHandler);
        priceLabel.addMouseListener(clickHandler);

        box.add(imagePlaceholder, BorderLayout.NORTH);
        box.add(nameLabel, BorderLayout.CENTER);
        box.add(priceLabel, BorderLayout.SOUTH);

        return box;
    }
}
