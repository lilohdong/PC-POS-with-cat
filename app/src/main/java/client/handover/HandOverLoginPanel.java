package client.handover;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class HandOverLoginPanel extends JPanel implements ActionListener {

    private HandOverFrame parent;
    private JTextField giverNameField;
    private JComboBox<String> receiverNameCombo;
    private JPasswordField receiverPassField;
    private JButton btnConfirm, btnCancel;

    // DB 직원 목록 저장용
    private List<String> staffList;

    public HandOverLoginPanel(HandOverFrame parent, String currentGiverName) {
        this.parent = parent;

        setBackground(new Color(50, 50, 50));
        setLayout(new GridBagLayout());

        // 직원 목록 불러오기
        staffList = parent.getService().getStaffNames();

        // 인계자 본인은 인수자 목록에서 제외
        staffList.remove(currentGiverName);

        JPanel loginBox = new JPanel();
        loginBox.setLayout(new BoxLayout(loginBox, BoxLayout.Y_AXIS));
        loginBox.setPreferredSize(new Dimension(500, 480));
        loginBox.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel giverPanel = createInputPanel("인계자", currentGiverName, true);
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
            giverNameField.setEditable(false);
            giverNameField.setHorizontalAlignment(JTextField.CENTER);
            row1.add(giverNameField, BorderLayout.CENTER);
        } else {
            // DB 직원 이름 콤보박스
            receiverNameCombo = new JComboBox<>(staffList.toArray(new String[0]));
            row1.add(receiverNameCombo, BorderLayout.CENTER);
        }

        JPanel row2 = new JPanel(new BorderLayout(10, 0));
        row2.add(new JLabel("비밀번호"), BorderLayout.WEST);

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
        btnConfirm.setForeground(Color.WHITE);
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
        }
        else if (e.getSource() == btnConfirm) {

            String giver = giverNameField.getText();
            String receiver = (String) receiverNameCombo.getSelectedItem();
            String inputPw = new String(receiverPassField.getPassword());

            // 인계자와 인수자 동일 방지
            if (giver.equals(receiver)) {
                JOptionPane.showMessageDialog(this,
                        "인수자와 인계자가 같습니다.",
                        "경고",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // DB에서 비밀번호 검증
            boolean passOK = parent.getService().checkStaffPassword(receiver, inputPw);

            if (!passOK) {
                JOptionPane.showMessageDialog(this,
                        "비밀번호 불일치",
                        "오류",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 성공 → 메인 패널 이동
            parent.changeToMain(giver, receiver);
        }
    }
}
