package client.staff.view;

import dao.StaffDAO;
import dto.StaffDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class StaffPanel extends JPanel {
    private StaffDAO staffDAO;

    // 탭 버튼
    private JButton staffInfoBtn;
    private JButton attendanceBtn;

    // 검색 패널
    private JTextField searchField;
    private JButton searchBtn;

    // 테이블
    private DefaultTableModel tableModel;
    private JTable staffTable;

    // 하단 버튼
    private JButton addBtn;
    private JButton editBtn;
    private JButton deleteBtn;

    // 현재 로드된 직원 목록 (테이블 행과 매핑용)
    private List<StaffDTO> currentStaffList;

    public StaffPanel() {
        staffDAO = StaffDAO.getInstance();
        setLayout(new BorderLayout());

        initComponents();
        loadStaffData();
    }

    private void initComponents() {
        // 중앙 컨텐츠 패널
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
    }

    private void selectTab(boolean isStaffInfo) {
        if (isStaffInfo) {
            staffInfoBtn.setBackground(Color.WHITE);
            staffInfoBtn.setForeground(Color.BLACK);
            staffInfoBtn.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(70, 130, 255)));

            attendanceBtn.setBackground(new Color(245, 245, 245));
            attendanceBtn.setForeground(Color.GRAY);
            attendanceBtn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        } else {
            attendanceBtn.setBackground(Color.WHITE);
            attendanceBtn.setForeground(Color.BLACK);
            attendanceBtn.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(70, 130, 255)));

            staffInfoBtn.setBackground(new Color(245, 245, 245));
            staffInfoBtn.setForeground(Color.GRAY);
            staffInfoBtn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

            JOptionPane.showMessageDialog(this, "근태 관리 기능은 준비 중입니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
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
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.addActionListener(e -> searchStaff());

        // Enter 키로도 검색 가능
        searchField.addActionListener(e -> searchStaff());

        panel.add(searchField);
        panel.add(searchBtn);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 테이블 모델 생성 - Staff 테이블 구조에 맞춤
        String[] columnNames = {"이름", "생년월일", "월급", "고용일"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 셀 편집 불가
            }
        };

        staffTable = new JTable(tableModel);
        staffTable.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        staffTable.setRowHeight(45);
        staffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        staffTable.setShowVerticalLines(false);
        staffTable.setGridColor(new Color(240, 240, 240));
        staffTable.setSelectionBackground(new Color(230, 240, 255));
        staffTable.setSelectionForeground(Color.BLACK);

        // 헤더 스타일
        JTableHeader header = staffTable.getTableHeader();
        header.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 220, 220)));

        // 셀 정렬
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < staffTable.getColumnCount(); i++) {
            staffTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // 더블클릭 이벤트
        staffTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = staffTable.getSelectedRow();
                    if (row >= 0) {
                        showStaffDetail(row);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(staffTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        addBtn = createActionButton("추가", new Color(70, 130, 255));
        editBtn = createActionButton("수정", new Color(100, 180, 100));
        deleteBtn = createActionButton("삭제", new Color(255, 100, 100));

        addBtn.addActionListener(e -> addStaff());
        editBtn.addActionListener(e -> editStaff());
        deleteBtn.addActionListener(e -> deleteStaff());

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
        button.setForeground(Color.GRAY);
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

    // Staff 테이블 데이터 로드
    private void loadStaffData() {
        tableModel.setRowCount(0);
        currentStaffList = staffDAO.getAllStaff();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
        NumberFormat currencyFormat = NumberFormat.getInstance(Locale.KOREA);

        for (StaffDTO staff : currentStaffList) {
            Object[] row = {
                    staff.getStaffName(),
                    staff.getBirth() != null ? dateFormat.format(staff.getBirth()) : "",
                    currencyFormat.format(staff.getSalary()) + "원",
                    staff.getHireDate() != null ? dateFormat.format(staff.getHireDate()) : ""
            };
            tableModel.addRow(row);
        }
    }

    // 검색 기능
    private void searchStaff() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty() || searchText.equals("이름으로 검색하기...")) {
            loadStaffData();
            return;
        }

        tableModel.setRowCount(0);
        currentStaffList = staffDAO.getStaffByName(searchText);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
        NumberFormat currencyFormat = NumberFormat.getInstance(Locale.KOREA);

        if (currentStaffList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            loadStaffData();
            return;
        }

        for (StaffDTO staff : currentStaffList) {
            Object[] row = {
                    staff.getStaffName(),
                    staff.getBirth() != null ? dateFormat.format(staff.getBirth()) : "",
                    currencyFormat.format(staff.getSalary()) + "원",
                    staff.getHireDate() != null ? dateFormat.format(staff.getHireDate()) : ""
            };
            tableModel.addRow(row);
        }
    }

    // 직원 상세 정보 보기
    private void showStaffDetail(int row) {
        if (currentStaffList == null || row < 0 || row >= currentStaffList.size()) {
            JOptionPane.showMessageDialog(this, "직원 정보를 불러올 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StaffDTO staff = currentStaffList.get(row);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        NumberFormat currencyFormat = NumberFormat.getInstance(Locale.KOREA);

        StringBuilder info = new StringBuilder();
        info.append("━━━━━━━━━━ 직원 상세 정보 ━━━━━━━━━━\n\n");
        info.append("직원 ID: ").append(staff.getStaffId()).append("\n");
        info.append("이름: ").append(staff.getStaffName()).append("\n");
        info.append("생년월일: ").append(staff.getBirth() != null ? dateFormat.format(staff.getBirth()) : "").append("\n");
        info.append("성별: ").append(staff.getGender()).append("\n");
        info.append("연락처: ").append(staff.getPhone() != null ? staff.getPhone() : "미등록").append("\n");
        info.append("\n─────────────────────────\n\n");
        info.append("월급: ").append(currencyFormat.format(staff.getSalary())).append("원\n");
        info.append("입사일: ").append(staff.getHireDate() != null ? dateFormat.format(staff.getHireDate()) : "").append("\n");
        info.append("재직 상태: ").append(staff.isActive() ? "재직중" : "퇴사").append("\n");

        JOptionPane.showMessageDialog(this, info.toString(), "직원 상세 정보", JOptionPane.INFORMATION_MESSAGE);
    }

    // 직원 추가
    private void addStaff() {
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField();
        JTextField birthField = new JTextField();
        JTextField genderField = new JTextField();
        JTextField salaryField = new JTextField();
        JTextField phoneField = new JTextField();

        inputPanel.add(new JLabel("이름:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("생년월일 (yyyy-MM-dd):"));
        inputPanel.add(birthField);
        inputPanel.add(new JLabel("성별 (남/여):"));
        inputPanel.add(genderField);
        inputPanel.add(new JLabel("월급(원):"));
        inputPanel.add(salaryField);
        inputPanel.add(new JLabel("연락처:"));
        inputPanel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "직원 추가",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // 입력값 검증
                if (nameField.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("이름을 입력해주세요.");
                }
                if (!genderField.getText().trim().equals("남") && !genderField.getText().trim().equals("여")) {
                    throw new IllegalArgumentException("성별은 '남' 또는 '여'로 입력해주세요.");
                }

                StaffDTO newStaff = new StaffDTO();
                newStaff.setStaffName(nameField.getText().trim());
                newStaff.setBirth(java.sql.Date.valueOf(birthField.getText().trim()));
                newStaff.setGender(genderField.getText().trim());
                newStaff.setSalary(Integer.parseInt(salaryField.getText().trim()));
                newStaff.setPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());

                if (staffDAO.insertStaff(newStaff)) {
                    JOptionPane.showMessageDialog(this, "직원이 추가되었습니다.", "성공",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadStaffData();
                } else {
                    JOptionPane.showMessageDialog(this, "직원 추가에 실패했습니다.", "오류",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "입력 오류", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "입력 형식이 올바르지 않습니다.\n" + e.getMessage(),
                        "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 직원 수정
    private void editStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "수정할 직원을 선택해주세요.", "알림",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentStaffList == null || selectedRow >= currentStaffList.size()) {
            JOptionPane.showMessageDialog(this, "직원 정보를 불러올 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StaffDTO staff = currentStaffList.get(selectedRow);

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField idField = new JTextField(String.valueOf(staff.getStaffId()));
        idField.setEditable(false);
        idField.setBackground(new Color(240, 240, 240));
        JTextField nameField = new JTextField(staff.getStaffName());
        JTextField birthField = new JTextField(staff.getBirth().toString());
        JTextField genderField = new JTextField(staff.getGender());
        JTextField salaryField = new JTextField(String.valueOf(staff.getSalary()));
        JTextField phoneField = new JTextField(staff.getPhone() != null ? staff.getPhone() : "");

        inputPanel.add(new JLabel("직원 ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("이름:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("생년월일 (yyyy-MM-dd):"));
        inputPanel.add(birthField);
        inputPanel.add(new JLabel("성별 (남/여):"));
        inputPanel.add(genderField);
        inputPanel.add(new JLabel("월급(원):"));
        inputPanel.add(salaryField);
        inputPanel.add(new JLabel("연락처:"));
        inputPanel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "직원 정보 수정",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // 입력값 검증
                if (nameField.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("이름을 입력해주세요.");
                }
                if (!genderField.getText().trim().equals("남") && !genderField.getText().trim().equals("여")) {
                    throw new IllegalArgumentException("성별은 '남' 또는 '여'로 입력해주세요.");
                }

                staff.setStaffName(nameField.getText().trim());
                staff.setBirth(java.sql.Date.valueOf(birthField.getText().trim()));
                staff.setGender(genderField.getText().trim());
                staff.setSalary(Integer.parseInt(salaryField.getText().trim()));
                staff.setPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());

                if (staffDAO.updateStaff(staff)) {
                    JOptionPane.showMessageDialog(this, "직원 정보가 수정되었습니다.", "성공",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadStaffData();
                } else {
                    JOptionPane.showMessageDialog(this, "정보 수정에 실패했습니다.", "오류",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "입력 오류", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "입력 형식이 올바르지 않습니다.\n" + e.getMessage(),
                        "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 직원 삭제 (퇴사 처리)
    private void deleteStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "삭제할 직원을 선택해주세요.", "알림",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentStaffList == null || selectedRow >= currentStaffList.size()) {
            JOptionPane.showMessageDialog(this, "직원 정보를 불러올 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StaffDTO staff = currentStaffList.get(selectedRow);

        int confirm = JOptionPane.showConfirmDialog(this,
                "선택한 직원 '" + staff.getStaffName() + "'을(를) 퇴사 처리하시겠습니까?",
                "퇴사 확인",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        if (staffDAO.deleteStaff(staff.getStaffId())) {
            JOptionPane.showMessageDialog(this, "직원이 퇴사 처리되었습니다.", "성공",
                    JOptionPane.INFORMATION_MESSAGE);
            loadStaffData();
        } else {
            JOptionPane.showMessageDialog(this, "퇴사 처리에 실패했습니다.", "오류",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}