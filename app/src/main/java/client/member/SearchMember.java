package client.member;

import dao.MemberDAO;
import dto.MemberDTO;
import service.MemberService;
import util.Sizes;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class SearchMember extends JPanel implements ActionListener {

    private final JComboBox<String> combo;
    private final String[] searchMethod = {"전체검색", "이름", "아이디"};
    private final ImageIcon searchimg = new ImageIcon(getClass().getResource("/imgs/searchimg.png"));
    JTextField searchField;
    JButton searchBtn;
    JLabel total;

    Vector<String> columnNames;
    Vector<Vector<String>> rows;
    DefaultTableModel model;
    JTable table;
    JScrollPane scroll;

    public SearchMember() {

        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, Sizes.PANEL_HEIGHT));

        setLayout(new BorderLayout());
        JPanel searchPanel = new JPanel();
        JPanel tablePanel = new JPanel();

        //총 회원 수
        total = new JLabel("총 회원 수 : ");

        add(searchPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(total, BorderLayout.SOUTH);


        //회원 검색
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        combo = new JComboBox<>(searchMethod);
        searchField = new JTextField(30);
        searchBtn = new JButton(searchimg);

        searchBtn.setBorder(BorderFactory.createEmptyBorder());
        searchBtn.setContentAreaFilled(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));


        searchPanel.add(combo);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        searchBtn.addActionListener(this);

        //회원 테이블
        columnNames = new Vector<>();
        columnNames.add("연령대");
        columnNames.add("이름");
        columnNames.add("아이디");
        columnNames.add("생년월일");
        columnNames.add("성별");
        columnNames.add("나이");
        columnNames.add("잔여시간");
        columnNames.add("휴대폰");

        // 수정 불가 모델
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;   // 모든 셀 수정 불가
            }
        };

        rows = new Vector<>();

        tablePanel.setLayout(new BorderLayout());
        table = new JTable(model);
        scroll = new JScrollPane(table);
        tablePanel.add(scroll);


        // 마지막 컬럼 가변길이 설정
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        //헤더 이동 및 크기 조절 불가
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        //컬럼 별 크기 고정
        int[] preferredWidths = {100, 100, 130, 130, 60, 60, 170 };
        int lastIndex = table.getColumnCount() - 1; // 휴대폰 컬럼 index = 7

        // 배열의 길이와 컬럼 수 중 작은 값 만큼 반복, i번째 컬럼을 가져와 크기 적용 조건
        for (int i = 0; i < preferredWidths.length && i < table.getColumnModel().getColumnCount(); i++) {
            TableColumn col = table.getColumnModel().getColumn(i); // 컬럼 객체 생성
            col.setPreferredWidth(preferredWidths[i]); // 컬럼 객체 기본 크기
            col.setMinWidth(preferredWidths[i]);       // 컬럼 최소 크기
            col.setMaxWidth(preferredWidths[i]);       // 컬럼 최대 크기
            col.setResizable(false);                   // 사용자가 컬럼 크기 조절 불가
        }

        // 마지막 컬럼만 가변길이 허용 (휴대폰)
        TableColumn last = table.getColumnModel().getColumn(lastIndex);
        last.setMinWidth(150);
        last.setPreferredWidth(200);          // 기본 크기
        last.setMaxWidth(Integer.MAX_VALUE);  // 최대 무제한
        last.setResizable(true);              // 가변 허용


        // 가운데 정렬
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRender);
        }

        refresh();

    }

    // 테이블을 새로고침 메서드
    public void refresh() {
        model.setRowCount(0); // 리셋
        MemberService.getInstance().loadTable(model);
        updateTotal(); // 총 인원 수 갱신
    }

    // 선택된 회원 ID가져오는 메서드(수정, 삭제 / 인덱스 번호로 2 = 아이디)
    public String getSelectedMemberId() {
        int row = table.getSelectedRow();
        if(row != -1) {
            return (String) table.getValueAt(row, 2);
        }
        return null;
    }

    // 선택된 회원 DTO 가져오는 메서드 (수정)
    public MemberDTO getSelectedMemberDTO() {
        String id = getSelectedMemberId();
        if(id != null) {
            return MemberDAO.getInstance().getMemberById(id);
        }
        return null;
    }


    // 총 회원수 갱신
    private void updateTotal() {
        total.setText("총 회원 수 : " + model.getRowCount());
    }

    // 선택된 행 번호 반환
    public int getSelectedRow() {
        return table.getSelectedRow();
    }

    // 회원 삭제 시 줄 삭제 후 갱신
    public void removeRow(int row) {
        model.removeRow(row);
        updateTotal();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchBtn) {
            String keyword = searchField.getText().trim();
            String type = combo.getSelectedItem().toString();

            if(type.equals("전체검색")) {
                // 검색창에 값이 있으면 전체를 불러오고 필터 적용
                if(!keyword.isEmpty()) {
                    MemberService.getInstance().loadTable(model, keyword, "전체검색");
                } else {
                    MemberService.getInstance().loadTable(model);
                }
            } else {
                MemberService.getInstance().loadTable(model, keyword, type);
            }

            updateTotal(); // 검색 후 회원 수 갱신
        }
    }
}
