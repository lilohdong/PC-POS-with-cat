package client.member;

import javax.swing.*;
import java.awt.*;

public class JoinForm extends JFrame {

    public JoinForm() {
        setTitle("회원가입");
        setSize(480, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 전체 패널
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // 중앙 입력 폼
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.insets = new Insets(8, 0, 8, 0);

        int row = 0;

        // 타이틀
        JLabel title = new JLabel("회원가입");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = row++;
        formPanel.add(title, gbc);

        // 아이디 + 중복확인
        JPanel idPanel = new JPanel(new BorderLayout(10, 0));
        JTextField tid = new JTextField();
        JButton idCheck = new JButton("중복확인");
        idPanel.add(tid, BorderLayout.CENTER);
        idPanel.add(idCheck, BorderLayout.EAST);

        gbc.gridy = row++;
        formPanel.add(labelWithPanel("아이디", idPanel), gbc);

        // 비밀번호
        gbc.gridy = row++;
        formPanel.add(labelWithField("비밀번호", new JPasswordField(20), true), gbc);

        // 비밀번호 확인
        gbc.gridy = row++;
        formPanel.add(labelWithField("비밀번호 확인", new JPasswordField(20), true), gbc);

        // 생년월일
        gbc.gridy = row++;
        formPanel.add(labelWithField("생년월일", new JTextField(), true), gbc);

        // 이름
        gbc.gridy = row++;
        formPanel.add(labelWithField("이름", new JTextField(), true), gbc);

        // 휴대폰 번호
        gbc.gridy = row++;
        formPanel.add(labelWithField("휴대폰 번호", new JTextField(), true), gbc);

        // 성별
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JRadioButton male = new JRadioButton("남");
        JRadioButton female = new JRadioButton("여");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(male);
        genderGroup.add(female);
        genderPanel.add(male);
        genderPanel.add(female);

        gbc.gridy = row++;
        formPanel.add(labelWithPanel("성별", genderPanel), gbc);

        // 약관동의 체크박스들
        JPanel agreePanel = new JPanel();
        agreePanel.setLayout(new BoxLayout(agreePanel, BoxLayout.Y_AXIS));

        agreePanel.add(new JCheckBox("전체 동의"));
        agreePanel.add(new JCheckBox("[필수] 이용 약관"));
        agreePanel.add(new JCheckBox("[필수] 개인정보 수집"));

        gbc.gridy = row++;
        formPanel.add(labelWithPanel("이용 약관 동의", agreePanel), gbc);

        // 가입 버튼
        JButton submit = new JButton("회원가입 완료");
        submit.setPreferredSize(new Dimension(200, 60));
        submit.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(submit);

        // 레이아웃 조립
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // 라벨 + 텍스트필드 조합
    private JPanel labelWithField(String labelText, JComponent field, boolean editable) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        JLabel label = new JLabel(labelText);
        field.setEnabled(editable);

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    // 라벨 + 특정 패널 조합
    private JPanel labelWithPanel(String labelText, JComponent comp) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        JLabel label = new JLabel(labelText);
        panel.add(label, BorderLayout.NORTH);
        panel.add(comp, BorderLayout.CENTER);
        return panel;
    }
}
