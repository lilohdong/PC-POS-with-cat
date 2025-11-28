package client.member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

class JoinDialog extends JDialog {

    private JTextField tid, tname, tphone;
    private JPasswordField tpassword, tpasswordcheck;
    private JRadioButton male, female;
    private JComboBox<String> yearCombo, monthCombo, dayCombo;
    private JCheckBox checkAll, checkTerm1, checkTerm2;
    private JLabel idMsgLabel; // 아이디 중복여부 메시지 띄울 라벨
    private boolean isIdChecked = false; // 아이디 중복확인을 했는지 체크하는 변수

    public JoinDialog(JFrame parents) {
        super(parents, "회원가입", true);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // 상단 타이틀 (회원가입)
        JLabel titleLabel = new JLabel("회원가입", SwingConstants.CENTER); // 가운데 정렬
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(20, 0, 10, 0)); // 위아래 여백
        titleLabel.setOpaque(true); // 불투명
        titleLabel.setBackground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        // 메인 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // 세로로 쌓기
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(10, 30, 10, 30)); // 양옆 여백


        // 아이디
        JPanel idGroupPanel = new JPanel(new BorderLayout(5, 0));
        idGroupPanel.setBackground(Color.WHITE);
        tid = new JTextField();
        JButton checkIdBtn = new JButton("중복확인");
        checkIdBtn.setBackground(new Color(240, 240, 240));
        idGroupPanel.add(tid, BorderLayout.CENTER);
        idGroupPanel.add(checkIdBtn, BorderLayout.EAST);

        // 아이디 메시지 표시
        idMsgLabel = new JLabel(" ");
        idMsgLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        addLabelComponentAndMsg(mainPanel, "아이디", idGroupPanel, idMsgLabel);

        // 비밀번호
        tpassword = new JPasswordField();
        addLabelAndComponent(mainPanel, "비밀번호", tpassword);

        // 비밀번호 확인
        tpasswordcheck = new JPasswordField();
        addLabelAndComponent(mainPanel, "비밀번호 확인", tpasswordcheck);

        // 생년월일
        JPanel birthPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        birthPanel.setBackground(Color.WHITE);
        yearCombo = new JComboBox<>();
        monthCombo = new JComboBox<>();
        dayCombo = new JComboBox<>();

        // 날짜 추가
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for(int i = currentYear; i >= 1900; i--) yearCombo.addItem(i + "년");
        for(int i = 1; i <= 12; i++) monthCombo.addItem(i + "월");
        for(int i = 1; i <= 31; i++) dayCombo.addItem(i + "일");

        yearCombo.setBackground(Color.WHITE);
        monthCombo.setBackground(Color.WHITE);
        dayCombo.setBackground(Color.WHITE);

        birthPanel.add(yearCombo);
        birthPanel.add(monthCombo);
        birthPanel.add(dayCombo);
        addLabelAndComponent(mainPanel, "생년월일", birthPanel);

        // 이름
        tname = new JTextField();
        addLabelAndComponent(mainPanel, "이름", tname);

        // 휴대폰
        tphone = new JTextField();
        addLabelAndComponent(mainPanel, "휴대폰 번호", tphone);

        // 성별
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        genderPanel.setBackground(Color.WHITE);
        ButtonGroup genderGroup = new ButtonGroup();
        male = new JRadioButton("남");
        female = new JRadioButton("여");

        male.setBackground(Color.WHITE);
        female.setBackground(Color.WHITE);

        genderGroup.add(male);
        genderGroup.add(female);
        genderPanel.add(male);
        genderPanel.add(new JLabel("   "));
        genderPanel.add(female);
        addLabelAndComponent(mainPanel, "성별", genderPanel);

        // 약관 동의
        JPanel termsPanel = new JPanel();
        termsPanel.setLayout(new BoxLayout(termsPanel, BoxLayout.Y_AXIS));
        termsPanel.setBackground(Color.WHITE);
        termsPanel.setAlignmentX(Component.LEFT_ALIGNMENT); //왼쪽 정렬

        checkAll = new JCheckBox("전체 동의");
        checkTerm1 = new JCheckBox("[필수] 이용 약관");
        checkTerm2 = new JCheckBox("[필수] 개인정보 수집");

        checkAll.setBackground(Color.WHITE);
        checkTerm1.setBackground(Color.WHITE);
        checkTerm2.setBackground(Color.WHITE);

        // 전체 동의
        checkAll.addActionListener(e -> {
            boolean isChecked = checkAll.isSelected();
            checkTerm1.setSelected(isChecked);
            checkTerm2.setSelected(isChecked);
        });


        ActionListener subListener = e -> {
            if (checkTerm1.isSelected() && checkTerm2.isSelected()) checkAll.setSelected(true);
            else checkAll.setSelected(false);
        };
        checkTerm1.addActionListener(subListener);
        checkTerm2.addActionListener(subListener);

        termsPanel.add(checkAll);
        termsPanel.add(Box.createVerticalStrut(5));
        termsPanel.add(checkTerm1);
        termsPanel.add(checkTerm2);
        addLabelAndComponent(mainPanel, "약관 동의", termsPanel);

        // 스크롤 패널
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 휠 한칸당 16필셀 설정
        add(scrollPane, BorderLayout.CENTER);


        // 하단 버튼
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JButton submitBtn = new JButton("회원가입 완료");
        submitBtn.setPreferredSize(new Dimension(250, 40));
        submitBtn.setBackground(new Color(65, 105, 225));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        buttonPanel.add(submitBtn);
        add(buttonPanel, BorderLayout.SOUTH);



        // 아이디 중복 확인 버튼 기능
        checkIdBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputId = tid.getText().trim();

                if (inputId.isEmpty()) {
                    idMsgLabel.setText("아이디를 입력해주세요.");
                    idMsgLabel.setForeground(Color.RED);
                    return;
                }

                // 일단 admin만 넣었습니다.DB 설계 후 적용예정
                if (inputId.equals("admin")) {
                    idMsgLabel.setText("중복된 아이디입니다. 다른 아이디를 입력해주세요.");
                    idMsgLabel.setForeground(Color.RED);
                    isIdChecked = false;
                } else {
                    idMsgLabel.setText("사용 가능한 아이디입니다.");
                    idMsgLabel.setForeground(Color.BLUE);
                    isIdChecked = true;
                }
            }
        });


        // 2. 회원가입 완료 버튼 기능 (검사, 이동)
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 아이디 검사
                if (tid.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "아이디를 입력해주세요.");
                    tid.requestFocus(); // 커서 이동
                    return;
                }
                if (!isIdChecked) {
                    JOptionPane.showMessageDialog(null, "아이디 중복 확인을 해주세요.");
                    tid.requestFocus();
                    return;
                }

                // 비밀번호 검사
                String pw = new String(tpassword.getPassword());
                if (pw.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "비밀번호를 입력해주세요.");
                    tpassword.requestFocus();
                    return;
                }

                // 비밀번호 확인 검사
                String pwCheck = new String(tpasswordcheck.getPassword());
                if (pwCheck.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "비밀번호 확인을 입력해주세요.");
                    tpasswordcheck.requestFocus();
                    return;
                }
                if (!pw.equals(pwCheck)) {
                    JOptionPane.showMessageDialog(null, "비밀번호가 일치하지 않습니다.");
                    tpasswordcheck.setText("");
                    tpasswordcheck.requestFocus();
                    return;
                }

                // 이름 검사
                if (tname.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "이름을 입력해주세요.");
                    tname.requestFocus();
                    return;
                }

                // 휴대폰 검사
                if (tphone.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "휴대폰 번호를 입력해주세요.");
                    tphone.requestFocus();
                    return;
                }

                // 성별 검사
                if (!male.isSelected() && !female.isSelected()) {
                    JOptionPane.showMessageDialog(null, "성별을 선택해주세요.");
                    return;
                }

                // 약관 동의 검사
                if (!checkTerm1.isSelected() || !checkTerm2.isSelected()) {
                    JOptionPane.showMessageDialog(null, "필수 약관에 모두 동의해주세요.");
                    return;
                }

                // 모든 검사 통과 시
                JOptionPane.showMessageDialog(null, "회원가입이 완료되었습니다!");
                dispose(); // 창 닫기
            }
        });

        // 다이얼로그 설정
        setSize(450, 750);
        setLocationRelativeTo(parents);
        setVisible(true);
    }


    // 라벨 + 컴포넌트 추가
    private void addLabelAndComponent(JPanel parent, String labelText, Component comp) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); // 높이 고정
        panel.setBorder(new EmptyBorder(0, 0, 10, 0)); // 하단 여백

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 12));

        panel.add(label, BorderLayout.NORTH);
        panel.add(comp, BorderLayout.CENTER);
        parent.add(panel);
    }

    // 라벨 + 컴포넌트 + 메시지라벨 추가 (아이디)
    private void addLabelComponentAndMsg(JPanel parent, String labelText, Component comp, JLabel msgLabel) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 12));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(comp, BorderLayout.CENTER);
        centerPanel.add(msgLabel, BorderLayout.SOUTH); // 입력창 바로 아래 메시지

        panel.add(label, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        parent.add(panel);
    }
}
