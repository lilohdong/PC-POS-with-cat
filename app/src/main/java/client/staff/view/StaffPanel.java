package client.staff.view;

import dao.MemberDAO;
import dto.MemberDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class StaffPanel extends JPanel {
    private MemberDAO memberDAO;

    // 탭 버튼
    private JButton memberInfoBtn;
    private JButton routeManageBtn;

    // 검색 패널
    private JTextField searchField;
    private JButton searchBtn;

    // 테이블
    private DefaultTableModel tableModel;
    private JTable memberTable;

    // 하단 버튼
    private JButton addBtn;
    private JButton editBtn;
    private JButton deleteBtn;

    public StaffPanel() {
        memberDAO = MemberDAO.getInstance();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        initComponents();
        loadMemberData();
    }

    private void initComponents() {
        // 상단 탭 패널
        JPanel tabPanel = createTabPanel();
        add(tabPanel, BorderLayout.NORTH);

        // 중앙 컨텐츠 패널
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createTabPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        memberInfoBtn = createTabButton("직원 정보", true);
        routeManageBtn = createTabButton("근태 관리", false);

        memberInfoBtn.addActionListener(e -> selectTab(true));
        routeManageBtn.addActionListener(e -> selectTab(false));

        panel.add(memberInfoBtn);
        panel.add(routeManageBtn);

        return panel;
    }

    private JButton createTabButton(String text, boolean selected) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 40));
        button.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (selected) {
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            button.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(70, 130, 255)));
        } else {
            button.setBackground(new Color(245, 245, 245));
            button.setForeground(Color.GRAY);
            button.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        }

        return button;
    }

    private void selectTab(boolean isMemberInfo) {
        if (isMemberInfo) {
            memberInfoBtn.setBackground(Color.WHITE);
            memberInfoBtn.setForeground(Color.BLACK);
            memberInfoBtn.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(70, 130, 255)));

            routeManageBtn.setBackground(new Color(245, 245, 245));
            routeManageBtn.setForeground(Color.GRAY);
            routeManageBtn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        } else {
            routeManageBtn.setBackground(Color.WHITE);
            routeManageBtn.setForeground(Color.BLACK);
            routeManageBtn.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(70, 130, 255)));

            memberInfoBtn.setBackground(new Color(245, 245, 245));
            memberInfoBtn.setForeground(Color.GRAY);
            memberInfoBtn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

            JOptionPane.showMessageDialog(this, "근태 관리 기능은 준비 중입니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 검색 패널
        JPanel searchPanel = createSearchPanel();
        panel.add(searchPanel, BorderLayout.NORTH);

        // 테이블 패널
        JPanel tablePanel = createTablePanel();
        panel.add(tablePanel, BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = createButtonPanel();
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(Color.WHITE);

        searchField = new JTextField(30);
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // 플레이스홀더 효과
        searchField.setForeground(Color.GRAY);
        searchField.setText("이름으로 검색하기...");
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("이름으로 검색하기...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("이름으로 검색하기...");
                }
            }
        });

        searchBtn = new JButton("🔍");
        searchBtn.setPreferredSize(new Dimension(35, 35));
        searchBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setBackground(Color.WHITE);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.addActionListener(e -> searchMember());

        // Enter 키로도 검색 가능
        searchField.addActionListener(e -> searchMember());

        panel.add(searchField);
        panel.add(searchBtn);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // 테이블 모델 생성
        String[] columnNames = {"이름", "생년월일", "시급", "고용일"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 셀 편집 불가
            }
        };

        memberTable = new JTable(tableModel);
        memberTable.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        memberTable.setRowHeight(45);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.setShowVerticalLines(false);
        memberTable.setGridColor(new Color(240, 240, 240));
        memberTable.setSelectionBackground(new Color(230, 240, 255));
        memberTable.setSelectionForeground(Color.BLACK);

        // 헤더 스타일
        JTableHeader header = memberTable.getTableHeader();
        header.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        header.setBackground(new Color(250, 250, 250));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 220, 220)));

        // 셀 정렬
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < memberTable.getColumnCount(); i++) {
            memberTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // 더블클릭 이벤트
        memberTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = memberTable.getSelectedRow();
                    if (row >= 0) {
                        showMemberDetail(row);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(Color.WHITE);

        addBtn = createActionButton("추가", new Color(70, 130, 255));
        editBtn = createActionButton("수정", new Color(100, 180, 100));
        deleteBtn = createActionButton("삭제", new Color(255, 100, 100));

        addBtn.addActionListener(e -> addMember());
        editBtn.addActionListener(e -> editMember());
        deleteBtn.addActionListener(e -> deleteMember());

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);

        return panel;
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(80, 35));
        button.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 호버 효과
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    // 데이터 로드
    private void loadMemberData() {
        tableModel.setRowCount(0);
        List<MemberDTO> members = memberDAO.getAllMembers();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        for (MemberDTO member : members) {
            Object[] row = {
                    member.getName(),
                    member.getBirth() != null ? dateFormat.format(member.getBirth()) : "",
                    "10,030원", // 고정 시급 (실제로는 별도 테이블에서 관리 필요)
                    member.getJoinDate() != null ? dateFormat.format(member.getJoinDate()) : ""
            };
            tableModel.addRow(row);
        }
    }

    // 검색 기능
    private void searchMember() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty() || searchText.equals("이름으로 검색하기...")) {
            loadMemberData();
            return;
        }

        tableModel.setRowCount(0);
        List<MemberDTO> members = memberDAO.getMembersByName(searchText);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        if (members.isEmpty()) {
            JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            loadMemberData();
            return;
        }

        for (MemberDTO member : members) {
            Object[] row = {
                    member.getName(),
                    member.getBirth() != null ? dateFormat.format(member.getBirth()) : "",
                    "10,030원",
                    member.getJoinDate() != null ? dateFormat.format(member.getJoinDate()) : ""
            };
            tableModel.addRow(row);
        }
    }

    // 직원 상세 정보 보기
    private void showMemberDetail(int row) {
        List<MemberDTO> members = memberDAO.getAllMembers();
        if (row < 0 || row >= members.size()) return;

        MemberDTO member = members.get(row);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        StringBuilder info = new StringBuilder();
        info.append("━━━━━━━━━━ 직원 정보 ━━━━━━━━━━\n\n");
        info.append("회원 ID: ").append(member.getmId()).append("\n");
        info.append("이름: ").append(member.getName()).append("\n");
        info.append("생년월일: ").append(member.getBirth() != null ? dateFormat.format(member.getBirth()) : "").append("\n");
        info.append("성별: ").append(member.getSex()).append("\n");
        info.append("연락처: ").append(member.getPhone()).append("\n");
        info.append("잔여 시간: ").append(member.getRemainTime()).append("분\n");
        info.append("가입일: ").append(member.getJoinDate() != null ? dateFormat.format(member.getJoinDate()) : "").append("\n");

        JOptionPane.showMessageDialog(this, info.toString(), "직원 상세 정보", JOptionPane.INFORMATION_MESSAGE);
    }

    // 직원 추가
    private void addMember() {
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 10, 10));

        JTextField idField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JTextField nameField = new JTextField();
        JTextField birthField = new JTextField();
        JTextField sexField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField timeField = new JTextField("0");

        inputPanel.add(new JLabel("회원 ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("비밀번호:"));
        inputPanel.add(passField);
        inputPanel.add(new JLabel("이름:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("생년월일 (yyyy-MM-dd):"));
        inputPanel.add(birthField);
        inputPanel.add(new JLabel("성별 (M/F):"));
        inputPanel.add(sexField);
        inputPanel.add(new JLabel("연락처:"));
        inputPanel.add(phoneField);
        inputPanel.add(new JLabel("잔여 시간(분):"));
        inputPanel.add(timeField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "직원 추가", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                MemberDTO newMember = new MemberDTO();
                newMember.setmId(idField.getText().trim());
                newMember.setPasswd(new String(passField.getPassword()));
                newMember.setName(nameField.getText().trim());
                newMember.setBirth(java.sql.Date.valueOf(birthField.getText().trim()));
                newMember.setSex(sexField.getText().trim());
                newMember.setPhone(phoneField.getText().trim());
                newMember.setRemainTime(Integer.parseInt(timeField.getText().trim()));

                if (memberDAO.insertMember(newMember)) {
                    JOptionPane.showMessageDialog(this, "직원이 추가되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                    loadMemberData();
                } else {
                    JOptionPane.showMessageDialog(this, "직원 추가에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "입력 형식이 올바르지 않습니다.\n" + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 직원 수정
    private void editMember() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "수정할 직원을 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<MemberDTO> members = memberDAO.getAllMembers();
        MemberDTO member = members.get(selectedRow);

        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 10, 10));

        JTextField idField = new JTextField(member.getmId());
        idField.setEditable(false);
        JPasswordField passField = new JPasswordField(member.getPasswd());
        JTextField nameField = new JTextField(member.getName());
        JTextField birthField = new JTextField(member.getBirth().toString());
        JTextField sexField = new JTextField(member.getSex());
        JTextField phoneField = new JTextField(member.getPhone());
        JTextField timeField = new JTextField(String.valueOf(member.getRemainTime()));

        inputPanel.add(new JLabel("회원 ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("비밀번호:"));
        inputPanel.add(passField);
        inputPanel.add(new JLabel("이름:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("생년월일 (yyyy-MM-dd):"));
        inputPanel.add(birthField);
        inputPanel.add(new JLabel("성별 (M/F):"));
        inputPanel.add(sexField);
        inputPanel.add(new JLabel("연락처:"));
        inputPanel.add(phoneField);
        inputPanel.add(new JLabel("잔여 시간(분):"));
        inputPanel.add(timeField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "직원 정보 수정", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                member.setPasswd(new String(passField.getPassword()));
                member.setName(nameField.getText().trim());
                member.setBirth(java.sql.Date.valueOf(birthField.getText().trim()));
                member.setSex(sexField.getText().trim());
                member.setPhone(phoneField.getText().trim());
                member.setRemainTime(Integer.parseInt(timeField.getText().trim()));

                if (memberDAO.updateMember(member)) {
                    JOptionPane.showMessageDialog(this, "직원 정보가 수정되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                    loadMemberData();
                } else {
                    JOptionPane.showMessageDialog(this, "정보 수정에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "입력 형식이 올바르지 않습니다.\n" + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 직원 삭제
    private void deleteMember() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "삭제할 직원을 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "선택한 직원을 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        List<MemberDTO> members = memberDAO.getAllMembers();
        MemberDTO member = members.get(selectedRow);

        if (memberDAO.deleteMember(member.getmId())) {
            JOptionPane.showMessageDialog(this, "직원이 삭제되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            loadMemberData();
        } else {
            JOptionPane.showMessageDialog(this, "삭제에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}