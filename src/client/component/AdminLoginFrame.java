package client.component;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import util.AdminLoginCheck;

public class AdminLoginFrame extends JFrame implements ActionListener{
    private JTextField idField;
    private JPasswordField pwField;

    public AdminLoginFrame() {
        setTitle("POS PLACE");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 메인 패널
        JPanel mainPanel = getMainPanel();

        // 타이틀 레이블
        JLabel titleLabel = new JLabel("POS PLACE");
        titleLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 48));
        titleLabel.setBounds(430, 180, 300, 60);
        mainPanel.add(titleLabel);

        // ID 패널
        JPanel idPanel = new JPanel();
        idPanel.setLayout(new BorderLayout());
        idPanel.setBounds(430, 285, 300, 40);
        idPanel.setBackground(Color.WHITE);
        idPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel idLabel = new JLabel(" ID : ");
        idLabel.setFont(new Font("Serif", Font.BOLD, 14));
        idField = new JTextField();
        idField.setFont(new Font("Arial", Font.PLAIN, 14));
        idField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        idPanel.add(idLabel, BorderLayout.WEST);
        idPanel.add(idField, BorderLayout.CENTER);
        mainPanel.add(idPanel);

        // PW 패널
        JPanel pwPanel = new JPanel();
        pwPanel.setLayout(new BorderLayout());
        pwPanel.setBounds(430, 345, 300, 40);
        pwPanel.setBackground(Color.WHITE);
        pwPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel pwLabel = new JLabel(" PW : ");
        pwLabel.setFont(new Font("Serif", Font.BOLD, 14));
        pwField = new JPasswordField();
        pwField.setFont(new Font("Arial", Font.PLAIN, 14));
        pwField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        pwPanel.add(pwLabel, BorderLayout.WEST);
        pwPanel.add(pwField, BorderLayout.CENTER);
        mainPanel.add(pwPanel);

        // GO 버튼
        JButton goButton = new JButton("GO!");
        goButton.setBounds(520, 405, 120, 35);
        goButton.setFont(new Font("Serif", Font.BOLD, 18));
        goButton.setBackground(new Color(173, 216, 230));
        goButton.setForeground(Color.BLACK);
        goButton.setFocusPainted(false);
        goButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        goButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 버튼 액션
        goButton.addActionListener(this);
        // Enter 키로 로그인
        pwField.addActionListener(this);

        mainPanel.add(goButton);

        add(mainPanel);
    }
// Main Panel return method
    private static JPanel getMainPanel() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 배경색
                g2d.setColor(new Color(255, 255, 255));
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // 분홍색 원
                g2d.setColor(new Color(255, 228, 225));
                g2d.fillOval(-200, 0, 500, getHeight());

                // 파란색 원
                g2d.setColor(new Color(135, 206, 250));
                g2d.fillOval(-150, 128, 300, 300);

            }
        };
        mainPanel.setLayout(null);
        return mainPanel;
    }

    public void actionPerformed(ActionEvent e) {
        if(AdminLoginCheck.COUNT == 0) {
            JOptionPane.showMessageDialog(this,
                    "로그인 시도 횟수를 초과했습니다.\n프로그램을 종료합니다.",
                    "로그인 제한",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return;
        }
        if(AdminLoginCheck.check(idField.getText(), new String(pwField.getPassword()))) {
            JOptionPane.showMessageDialog(this,
                    "환영합니다 "+idField.getText()+"님",
                    "환영합니다.",JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "아이디 또는 비밀번호가 일치하지 않습니다.\n남은 시도 횟수: " + AdminLoginCheck.COUNT,
                    "로그인 실패",
                    JOptionPane.WARNING_MESSAGE);
        }
    }



    public static void start() {
        SwingUtilities.invokeLater(() -> {
            AdminLoginFrame alf = new AdminLoginFrame();
            alf.setVisible(true);
        });
    }
}

