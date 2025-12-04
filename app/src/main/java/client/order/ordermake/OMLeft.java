package client.order.ordermake;

import java.util.List;

import javax.swing.*;
import java.awt.*;

import dao.MenuDAO;
import dto.MenuDTO;
/*
좌측 메뉴 선택 패널

-카테고리 버튼 10개
-메뉴 아이템 목록 (GridLayout로)
*/
public class OMLeft extends JPanel{


    private OMLeft self = this;
    private JPanel menuPanel;
    private MenuDAO menuDAO = new MenuDAO();
    private final OMCenter omCenter;
    private String[] categoryList = {
            "전체", "인기메뉴", "라면", "볶음밥", "덮밥",
            "분식", "사이드", "음료", "과자", "기타/요청"
    };

    public OMLeft(OMCenter omCenter) {
        this.omCenter = omCenter;
        initUI();
        loadMenusByCategory("전체"); // 초기 메뉴 목록 로드
    }

    private void initUI(){
        setLayout(new BorderLayout());

        // 상단 카테고리
        JPanel category = new JPanel();
        category.setLayout(new GridLayout(2, 5, 5, 5));

        for (String c : categoryList){
            JButton btn = new JButton(c);
            btn.addActionListener(e -> loadMenusByCategory(c));
            category.add(btn);
        }

        // 메뉴 리스트 패널
        menuPanel = new JPanel();
        menuPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); // 0행 3열 (동적으로 추가)
        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(category, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // 카테고리별 메뉴 로드 (요청 2번)
    public void loadMenusByCategory(String categoryName) {
        List<MenuDTO> menus = menuDAO.getMenusByCategory(categoryName);
        updateMenuPanel(menus);
    }


    // 메뉴 이름 검색 (요청 2번) - OMCenter의 검색창과 연결 필요 (Search 버튼 없으므로 Enter 키 또는 별도 버튼 필요)
    // OMCenter에서 검색 이벤트가 발생하면 이 메서드를 호출하도록 구현되어야 함.
    public void searchMenus(String keyword) {
        List<MenuDTO> menus = menuDAO.searchMenus(keyword);
        updateMenuPanel(menus);
    }

    // 메뉴 패널 UI 업데이트
    private void updateMenuPanel(List<MenuDTO> menus) {
        menuPanel.removeAll();

        if (menus.isEmpty()) {
            menuPanel.add(new JLabel("해당 카테고리 메뉴가 없습니다."));
        } else {
            for(MenuDTO menu : menus){
                menuPanel.add(createMenuBox(menu));
            }
        }
        // [핵심 수정] 3열 레이아웃을 강제하기 위해 PreferredSize 설정
        // (메뉴 박스 150px * 3열) + (간격 약 10px * 4개) = 490px
        int columnCount = 3;
        int menuWidth = 150;
        int gap = 10;
        int preferredWidth = (menuWidth * columnCount) + (gap * (columnCount + 1)) + 5; // 약 500px

        // 메뉴 개수에 따라 세로 높이 계산 (예시: 3열 당 180px)
        int menuCount = menus.size();
        int rowCount = (int) Math.ceil((double) menuCount / columnCount);
        int menuHeight = 180;
        int preferredHeight = (menuHeight * rowCount) + (gap * rowCount);

        // 데이터가 없을 때 JScrollPane이 찌그러지는 것을 방지
        if (menuCount == 0) {
            preferredHeight = 400;
        }

        // [적용] 메뉴 패널의 크기를 강제하여 가로 스크롤을 방지하고 세로 스크롤을 유도
        menuPanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));

        // 레이아웃 재계산 및 갱신
        menuPanel.revalidate();
        menuPanel.repaint();
    }

    // 단일 메뉴 아이템 패널 생성
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

        // 클릭 이벤트 로직 정의 (재사용을 위해 별도 객체로 생성)
        java.awt.event.MouseAdapter clickHandler = new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // 부모 프레임에서 OMCenter 인스턴스 찾기
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
