package client.game;
import dao.GameDAO;
import dto.GameDTO; // GameDTO 클래스 import가 필요합니다.

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional; // Java 8 이상에서 사용 가능

public class ModifyDeleteDialog extends JDialog {

    private JTextField idSearchField;
    private JTextField titleModifyField;
    private JTextField publisherModifyField;

    // DAO 인스턴스
    private GameDAO gameDAO = GameDAO.getInstance();

    public ModifyDeleteDialog(JFrame parent) {
        super(parent, "✏️ 게임 정보 수정/삭제", true);
        setSize(400, 300);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(parent);

        // ... (GUI 컴포넌트 초기화 및 이벤트 리스너 설정은 원본 코드와 동일) ...

        // --- 1. 검색 패널 ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("ID로 게임 검색"));

        searchPanel.add(new JLabel("게임 ID (g_id):"));
        idSearchField = new JTextField(10);
        searchPanel.add(idSearchField);

        JButton searchButton = new JButton("검색");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        // --- 2. 수정/표시 필드 패널 ---
        JPanel modifyPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        modifyPanel.setBorder(BorderFactory.createTitledBorder("수정 정보"));

        modifyPanel.add(new JLabel("제목 (title):"));
        titleModifyField = new JTextField();
        modifyPanel.add(titleModifyField);

        modifyPanel.add(new JLabel("배급사 (publisher):"));
        publisherModifyField = new JTextField();
        modifyPanel.add(publisherModifyField);

        add(modifyPanel, BorderLayout.CENTER);

        // --- 3. 버튼 패널 (수정/삭제/취소) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton modifyButton = new JButton("수정 실행");
        JButton deleteButton = new JButton("삭제 실행");
        JButton cancelButton = new JButton("닫기");

        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performModify();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performDelete();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(modifyButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 필드 초기 상태: 수정 필드는 비활성화
        setModifyFieldsEditable(false);
    }

    // 수정 필드 활성화/비활성화 메서드
    private void setModifyFieldsEditable(boolean editable) {
        titleModifyField.setEditable(editable);
        publisherModifyField.setEditable(editable);
    }

    // ==========================================================
    // 게임 검색 로직 (DAO 연동)
    // ==========================================================
    private void performSearch() {
        String idText = idSearchField.getText().trim();
        setModifyFieldsEditable(false); // 검색 전 초기화
        titleModifyField.setText("");
        publisherModifyField.setText("");

        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "검색할 게임 ID를 입력하세요.",
                    "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        GameDTO gameOpt = gameDAO.selectGame(idText);

        if (gameOpt != null) {
            // 데이터가 있을 경우, 수정 필드에 값 채우기
            titleModifyField.setText(gameOpt.getTitle());
            publisherModifyField.setText(gameOpt.getPublisher());
            setModifyFieldsEditable(true); // 수정 가능하도록 활성화

            JOptionPane.showMessageDialog(this,
                    "게임 ID " + idText + " 검색 완료.",
                    "성공", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // 데이터가 없을 경우
            JOptionPane.showMessageDialog(this,
                    "게임 ID " + idText + "에 해당하는 정보를 찾을 수 없습니다.",
                    "검색 실패", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ==========================================================
    // 게임 수정 로직 (DAO 연동)
    // ==========================================================
    private void performModify() {
        String idText = idSearchField.getText().trim();
        String newTitle = titleModifyField.getText().trim();
        String newPublisher = publisherModifyField.getText().trim();

        if (idText.isEmpty() || newTitle.isEmpty() || newPublisher.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID 검색 및 모든 수정 필드를 채우세요.",
                    "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // DAO의 수정 메서드 호출
        boolean success = gameDAO.updateGame(idText, newTitle, newPublisher);

        if (success) {
            JOptionPane.showMessageDialog(this, "게임 ID " + idText + " 수정 성공!",
                    "성공", JOptionPane.INFORMATION_MESSAGE);
            // 부모 프레임의 목록 갱신을 위해 부모에게 알림 (추가 로직 필요)
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "수정 실패. ID를 확인하거나 DB 연결을 점검하세요.",
                    "실패", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================================================
    // 게임 삭제 로직 (DAO 연동)
    // ==========================================================
    private void performDelete() {
        String idText = idSearchField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "삭제할 게임 ID를 먼저 검색하세요.",
                    "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "정말로 게임 ID " + idText + "를 삭제하시겠습니까?",
                "삭제 확인", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // DAO의 삭제 메서드 호출
            boolean success = gameDAO.deleteGame(idText);

            if (success) {
                JOptionPane.showMessageDialog(this, "게임 ID " + idText + " 삭제 성공!",
                        "성공", JOptionPane.INFORMATION_MESSAGE);
                // 부모 프레임의 목록 갱신을 위해 부모에게 알림 (추가 로직 필요)
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "삭제 실패. ID를 확인하거나 DB 연결을 점검하세요.",
                        "실패", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}