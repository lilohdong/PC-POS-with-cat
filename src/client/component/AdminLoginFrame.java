package client.component;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminLoginFrame extends JFrame {
    private JTextField idField;
    private JPasswordField pwField;
    private int loginAttempts = 0;
    private static final int MAX_ATTEMPTS = 5;

    public AdminLoginFrame() {
        setTitle("POS PLACE");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 메인 패널
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
        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Enter 키로 로그인
        pwField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        mainPanel.add(goButton);

        add(mainPanel);
        setVisible(true);
    }

    private void handleLogin() {
        if (loginAttempts >= MAX_ATTEMPTS) {
            JOptionPane.showMessageDialog(this,
                    "로그인 시도 횟수를 초과했습니다.\n프로그램을 종료합니다.",
                    "로그인 제한",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return;
        }

        String id = idField.getText();
        String pw = new String(pwField.getPassword());

        if (id.equals("admin") && pw.equals("1234")) {
            // 로그인 성공
            this.dispose();
            new MainFrame();
        } else {
            // 로그인 실패
            loginAttempts++;
            int remainingAttempts = MAX_ATTEMPTS - loginAttempts;

            if (remainingAttempts > 0) {
                JOptionPane.showMessageDialog(this,
                        "아이디 또는 비밀번호가 일치하지 않습니다.\n남은 시도 횟수: " + remainingAttempts,
                        "로그인 실패",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "로그인 시도 횟수를 초과했습니다.\n프로그램을 종료합니다.",
                        "로그인 제한",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }

            pwField.setText("");
            pwField.requestFocus();
        }
    }

    public static void start() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AdminLoginFrame();
            }
        });
    }
}

