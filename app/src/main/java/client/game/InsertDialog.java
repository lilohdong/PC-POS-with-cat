package client.game;

import dao.GameDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InsertDialog extends JDialog {

    private JTextField titleField;
    private JTextField publisherField;

    // 모달 다이얼로그를 위해 부모 Frame을 받습니다.
    public InsertDialog(JFrame parent) {
        super(parent, "게임 정보 삽입", true); // true: 모달 (Modal) 설정
        setSize(350, 200);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(parent); // 부모 창 중앙에 배치

        // --- 입력 필드 패널 ---
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));

        inputPanel.add(new JLabel("제목 (title):"));
        titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("배급사 (publisher):"));
        publisherField = new JTextField();
        inputPanel.add(publisherField);

        add(inputPanel, BorderLayout.CENTER);

        // --- 버튼 패널 ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton insertButton = new JButton("삽입 실행");
        JButton cancelButton = new JButton("취소");

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performInsert();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // 다이얼로그 닫기
            }
        });

        buttonPanel.add(insertButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // DB 삽입 로직 (DB 연결 로직 필요)
    private void performInsert() {
        String title = titleField.getText();
        String publisher = publisherField.getText();

        if (title.isEmpty() || publisher.isEmpty()) {
            JOptionPane.showMessageDialog(this, "제목과 배급사를 모두 입력하세요.",
                    "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        GameDAO.getInstance().insertGame(title, publisher);

        JOptionPane.showMessageDialog(this, String.format("게임 삽입 성공!\n제목: %s, 배급사: %s", title, publisher),
                "성공", JOptionPane.INFORMATION_MESSAGE);

        dispose(); // 성공하면 다이얼로그 닫기
    }
}