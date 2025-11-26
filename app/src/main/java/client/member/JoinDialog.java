package client.member;

import javax.swing.*;
import java.awt.*;

public class JoinDialog extends JDialog {
    private JLabel ID, password, passwordcheck, birth, name, phone, gender;
    private JTextField tid, tbirth, tname, tphone;
    private JPasswordField tpassword, tpasswordcheck;
    private JRadioButton male, female;
    private  JButton checkId, Submit;

    // 변수명은 진짜 내가 봐도 아닌데 이거맞나요

    public JoinDialog(JFrame parents) {
        super(parents,"회원가입", true);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        ID = new JLabel("아이디");
        tid = new JTextField();
        checkId = new JButton("중복확인");
        panel.add(ID);
        panel.add(tid);
        panel.add(checkId);

        password = new JLabel("비밀번호");
        tpassword = new JPasswordField();
        panel.add(password);
        panel.add(tpassword);

        passwordcheck = new JLabel("비밀번호 확인");
        tpasswordcheck = new JPasswordField();
        panel.add(passwordcheck);
        panel.add(tpasswordcheck);

        birth = new JLabel("생년월일");
        tbirth = new JTextField();
        panel.add(birth);
        panel.add(tbirth);

        name = new JLabel("이름");
        tname = new JTextField();
        panel.add(name);
        panel.add(tname);

        phone = new JLabel("핸드폰");
        tphone = new JTextField();
        panel.add(phone);
        panel.add(tpassword);

        gender = new JLabel("성별");
        male = new JRadioButton("남");
        female = new JRadioButton("여");
        panel.add(gender);
        panel.add(male);
        panel.add(female);

        Submit = new JButton("회원가입 완료");
        panel.add(Submit);




    }
}
