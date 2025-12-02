package client.member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.sql.Date;
import dto.MemberDTO;
import service.MemberService;

public class UpdateDialog extends JDialog {

    private JTextField tid, tname, tphone;
    private JPasswordField tpassword;
    private JRadioButton male, female;

    private SearchMember searchMember;
    private MemberDTO targetMember;

    // 연령대 버튼
    private JRadioButton ele, mid, high, adult;
    private JComboBox<String> yearCombo, monthCombo, dayCombo;

    // 비밀번호 초기화 버튼
    private JButton resetPwBtn;

    public UpdateDialog(JFrame parents, SearchMember searchMember, MemberDTO targetMember) {
        super(parents, "회원정보 수정", true);
        this.searchMember = searchMember;
        this.targetMember = targetMember;
        setLayout(new BorderLayout());

        // 상단 타이틀
        JLabel titleLabel = new JLabel("회원정보 수정", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        // 메인 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(10, 30, 10, 30));

        // 아이디 (수정 불가)
        tid = new JTextField();
        tid.setEditable(false); // 수정 불가
        tid.setBackground(new Color(230, 230, 230)); // 회색 배경
        tid.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // 테두리 유지
        addLabelAndComponent(mainPanel, "아이디", tid);

        // 비밀번호 + 초기화 버튼
        JPanel pwPanel = new JPanel(new BorderLayout(5, 0)); // 간격 5
        pwPanel.setBackground(Color.WHITE);

        tpassword = new JPasswordField();
        resetPwBtn = new JButton("초기화");
        resetPwBtn.setBackground(new Color(65, 105, 225)); // 파란색 포인트
        resetPwBtn.setForeground(Color.WHITE);
        resetPwBtn.setFont(new Font("맑은 고딕", Font.BOLD, 12));

        pwPanel.add(tpassword, BorderLayout.CENTER);
        pwPanel.add(resetPwBtn, BorderLayout.EAST);

        addLabelAndComponent(mainPanel, "비밀번호", pwPanel);

        // 생년월일
        JPanel birthPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        birthPanel.setBackground(Color.WHITE);
        yearCombo = new JComboBox<>();
        monthCombo = new JComboBox<>();
        dayCombo = new JComboBox<>();

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i >= 1900; i--) yearCombo.addItem(i + "년");
        for (int i = 1; i <= 12; i++) monthCombo.addItem(i + "월");
        for (int i = 1; i <= 31; i++) dayCombo.addItem(i + "일");

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

        // 연령대
        JPanel agePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        agePanel.setBackground(Color.WHITE);

        ButtonGroup ageGroup = new ButtonGroup();
        ele = new JRadioButton("초등학생");
        mid = new JRadioButton("중학생");
        high = new JRadioButton("고등학생");
        adult = new JRadioButton("성인");

        JRadioButton[] ages = {ele, mid, high, adult};
        for(JRadioButton rb : ages) {
            rb.setBackground(Color.WHITE);
            ageGroup.add(rb);
            agePanel.add(rb);
            agePanel.add(new JLabel(" "));
        }

        addLabelAndComponent(mainPanel, "연령대", agePanel);


        // 스크롤 패널
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);


        // 하단 버튼 (취소 / 수정)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton cancelBtn = new JButton("취소");
        cancelBtn.setPreferredSize(new Dimension(100, 40));
        cancelBtn.setBackground(new Color(230, 230, 230));

        JButton updateBtn = new JButton("수정");
        updateBtn.setPreferredSize(new Dimension(200, 40));
        updateBtn.setBackground(new Color(65, 105, 225));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        buttonPanel.add(cancelBtn);
        buttonPanel.add(updateBtn);
        add(buttonPanel, BorderLayout.SOUTH);


        // 데이터 세팅
        if(targetMember != null) {
            tid.setText(targetMember.getmId());
            tname.setText(targetMember.getName());
            tphone.setText(targetMember.getPhone());
            tpassword.setText(targetMember.getPasswd());

            if("남".equals(targetMember.getSex())) male.setSelected(true);
            else female.setSelected(true);

            // 날짜 세팅
            Calendar cal = Calendar.getInstance();
            cal.setTime(targetMember.getBirth());
            yearCombo.setSelectedItem(cal.get(Calendar.YEAR) + "년");
            monthCombo.setSelectedItem((cal.get(Calendar.MONTH) + 1) + "월");
            dayCombo.setSelectedItem(cal.get(Calendar.DAY_OF_MONTH) + "일");

            // 연령대 자동 체크 (나이 계산)
            int age = currentYear - cal.get(Calendar.YEAR) + 1;
            if(age <= 13) ele.setSelected(true);
            else if(age <= 16) mid.setSelected(true);
            else if(age <= 19) high.setSelected(true);
            else adult.setSelected(true);
        }


        // 취소 버튼
        cancelBtn.addActionListener(e -> dispose());

        // 비밀번호 초기화 버튼
        resetPwBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /int result = JOptionPane.showConfirmDialog(null,
                        "비밀번호를 '1234'로 초기화하시겠습니까?\n(수정 버튼을 눌러야 저장됩니다.)",
                        "비밀번호 초기화",
                        JOptionPane.YES_NO_OPTION);

                if(result == JOptionPane.YES_OPTION) {
                    tpassword.setText("1234");
                }
            }
        });

        // 수정 버튼
        updateBtn.addActionListener(e -> {

            // 비밀번호 검사
            String pw = new String(tpassword.getPassword());
            if (pw.isEmpty()) {
                showMsg("비밀번호를 입력하거나 초기화 버튼을 눌러주세요.", tpassword);
                return;
            }

            if (tname.getText().trim().isEmpty()) {
                showMsg("이름을 입력해주세요.", tname);
                return;
            }
            if (tphone.getText().trim().isEmpty()) {
                showMsg("휴대폰 번호를 입력해주세요.", tphone);
                return;
            }

            if (!male.isSelected() && !female.isSelected()) {
                JOptionPane.showMessageDialog(null, "성별을 선택해주세요.");
                return;
            }
            if (!ele.isSelected() && !mid.isSelected() && !high.isSelected() && !adult.isSelected()) {
                JOptionPane.showMessageDialog(null, "연령대(직업)를 선택해주세요.");
                return;
            }

            // 성공 시 DB 업데이트 로직 추가
            // MemberDTO 생성 및 값 설정
            MemberDTO updateDto = new MemberDTO();
            updateDto.setmId(tid.getText());
            updateDto.setPasswd(new String(tpassword.getPassword()));
            updateDto.setName(tname.getText());
            updateDto.setPhone(tphone.getText());
            updateDto.setSex(male.isSelected() ? "남" : "여");
            updateDto.setRemainTime(targetMember.getRemainTime()); // 시간 유지

            String y = ((String)yearCombo.getSelectedItem()).replace("년", "");
            String m = ((String)monthCombo.getSelectedItem()).replace("월", "");
            String d = ((String)dayCombo.getSelectedItem()).replace("일", "");
            if(m.length() == 1) m = "0" + m;
            if(d.length() == 1) d = "0" + d;
            updateDto.setBirth(Date.valueOf(y + "-" + m + "-" + d));

            // Service 호출
            boolean success = MemberService.getInstance().updateMember(updateDto);

            if(success) {
                JOptionPane.showMessageDialog(null, "회원정보가 수정되었습니다.");
                searchMember.refresh(); // 테이블 새로고침
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "수정 실패 (DB 오류)");
            }
        });

        setSize(450, 750);
        setLocationRelativeTo(parents);
        setVisible(true);
    }

    //  함수
    private void showMsg(String msg, JComponent comp) {
        JOptionPane.showMessageDialog(null, msg);
        comp.requestFocus();
    }

    // 라벨 + 컴포넌트 추가
    private void addLabelAndComponent(JPanel parent, String labelText, Component comp) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 12));

        panel.add(label, BorderLayout.NORTH);
        panel.add(comp, BorderLayout.CENTER);
        parent.add(panel);
    }

}
