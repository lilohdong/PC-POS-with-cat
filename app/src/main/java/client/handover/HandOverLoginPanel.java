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
    private JComboBox<String> receiverNameCombo;
    private JPasswordField receiverPassField;
    private JButton btnConfirm, btnCancel;

    
    public HandOverLoginPanel(HandOverFrame parent, String currentGiverName) {
        this.parent = parent;

        setBackground(new Color(50, 50, 50));
        setLayout(new GridBagLayout());

        JPanel loginBox = new JPanel();
        loginBox.setLayout(new BoxLayout(loginBox, BoxLayout.Y_AXIS));
        loginBox.setPreferredSize(new Dimension(500, 480));
        loginBox.setBorder(new EmptyBorder(30, 40, 30, 40));

        // 인계자: 받아온 이름으로 고정
        JPanel giverPanel = createInputPanel("인계자", currentGiverName, true);
        // 인수자: 콤보박스 선택
        JPanel receiverPanel = createInputPanel("인수자", null, false);
        JPanel btnPanel = createButtonPanel();

        loginBox.add(giverPanel);
        loginBox.add(Box.createVerticalStrut(20));
        loginBox.add(receiverPanel);
        loginBox.add(Box.createVerticalStrut(30));
        loginBox.add(btnPanel);

        add(loginBox, new GridBagConstraints());
    }

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
        JPanel row1 = new JPanel(new BorderLayout(10, 0));
        row1.add(new JLabel("관리자   "), BorderLayout.WEST);

        if (isFixedName) {
            giverNameField = new JTextField(defaultName);
            giverNameField.setEditable(false); // 수정 불가
            giverNameField.setHorizontalAlignment(JTextField.CENTER);
            row1.add(giverNameField, BorderLayout.CENTER);
        } else {
            // 직원 목록
            receiverNameCombo = new JComboBox<>(new String[]{"사장님", "오동준", "최성균", "이원호", "조민규"});
            row1.add(receiverNameCombo, BorderLayout.CENTER);
        }

        JPanel row2 = new JPanel(new BorderLayout(10, 0));
        row2.add(new JLabel("비밀번호"), BorderLayout.WEST);

        // 인계자는 이미 근무중이므로 인수자만 비밀번호 입력
        if (!isFixedName) {
            receiverPassField = new JPasswordField();
            row2.add(receiverPassField, BorderLayout.CENTER);
        } else {

            JTextField dummy = new JTextField("로그인 상태");
            dummy.setEditable(false);
            dummy.setEnabled(false);
            row2.add(dummy, BorderLayout.CENTER);
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
            parent.dispose();
        } else if (e.getSource() == btnConfirm) {
            String inputPw = new String(receiverPassField.getPassword());

            if ("1234".equals(inputPw)) {
                String giver = giverNameField.getText();
                String receiver = (String) receiverNameCombo.getSelectedItem();

                // 본인에게 인수인계 불가 처리
                if(giver.equals(receiver)){
                    JOptionPane.showMessageDialog(this, "인수자와 인계자가 같습니다.", "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                parent.changeToMain(giver, receiver);
            } else {
                JOptionPane.showMessageDialog(this, "비밀번호 불일치", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
