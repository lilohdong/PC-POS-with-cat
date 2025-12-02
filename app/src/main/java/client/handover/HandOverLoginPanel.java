package client.handover;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HandOverLoginPanel extends JPanel implements ActionListener {

    private HandOverFrame parent;
    private JTextField giverNameField;
    private JPasswordField giverPassField;
    private JComboBox<String> receiverNameCombo;
    private JPasswordField receiverPassField;
    private JButton btnConfirm, btnCancel;

    public HandOverLoginPanel(HandOverFrame parent) {
        this.parent = parent;

        // 전체 배경색 설정
        setBackground(new Color(50, 50, 50)); // 예시: 어두운 배경 혹은 흰색

        //GridBagLayout을 사용하여 내부 박스를 정중앙에 배치
        setLayout(new GridBagLayout());

        // 실제 로그인 폼이 들어갈 컨테이너
        JPanel loginBox = new JPanel();
        loginBox.setLayout(new BoxLayout(loginBox, BoxLayout.Y_AXIS));
        loginBox.setPreferredSize(new Dimension(500, 480)); // 박스 크기 고정
        loginBox.setBorder(new EmptyBorder(30, 40, 30, 40)); // 내부 여백

        // 내부 패널들 생성
        JPanel giverPanel = createInputPanel("인계자", "오동준", true);
        JPanel receiverPanel = createInputPanel("인수자", null, false); // null이면 콤보박스 모드
        JPanel btnPanel = createButtonPanel();

        loginBox.add(giverPanel);
        loginBox.add(Box.createVerticalStrut(20));
        loginBox.add(receiverPanel);
        loginBox.add(Box.createVerticalStrut(30));
        loginBox.add(btnPanel);

        // GridBagLayout을 이용해 loginBox를 화면 중앙에 add
        add(loginBox, new GridBagConstraints());
    }

    // 패널 생성 메서드화
    private JPanel createInputPanel(String title, String defaultName, boolean isFixedName) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));

        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(2, 1, 0, 10));

        // 이름 입력줄
        JPanel row1 = new JPanel(new BorderLayout(10, 0));
        row1.add(new JLabel("관리자   "), BorderLayout.WEST);

        if (isFixedName) {
            giverNameField = new JTextField(defaultName);
            giverNameField.setEditable(false);

            row1.add(giverNameField, BorderLayout.CENTER);
        } else {
            receiverNameCombo = new JComboBox<>(new String[]{"사장님", "오동준", "최성균", "이원호", "조민규"});

            row1.add(receiverNameCombo, BorderLayout.CENTER);
        }

        // 비번 입력줄
        JPanel row2 = new JPanel(new BorderLayout(10, 0));

        row2.add(new JLabel("비밀번호"), BorderLayout.WEST);

        if (isFixedName) {
            giverPassField = new JPasswordField();
            row2.add(giverPassField, BorderLayout.CENTER);
        } else {
            receiverPassField = new JPasswordField();
            row2.add(receiverPassField, BorderLayout.CENTER);
        }

        formPanel.add(row1);
        formPanel.add(row2);
        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnConfirm = new JButton("확인");
        btnConfirm.setBackground(new Color(50, 100, 255));

        btnConfirm.setPreferredSize(new Dimension(80, 35));

        btnCancel = new JButton("닫기 (C)");
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setPreferredSize(new Dimension(80, 35));

        btnConfirm.addActionListener(this);
        btnCancel.addActionListener(this);

        p.add(btnConfirm);
        p.add(btnCancel);
        return p;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCancel) {
            parent.dispose(); // 닫기
        } else if (e.getSource() == btnConfirm) {
            String inputPw = new String(receiverPassField.getPassword());
            if ("1234".equals(inputPw)) {

                String giver = giverNameField.getText();
                parent.changeToMain(giver);
            } else {
                JOptionPane.showMessageDialog(this, "비밀번호 불일치", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
