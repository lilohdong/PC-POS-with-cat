package client.handover;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HandOverLoginPanel extends JPanel implements ActionListener {

    private HandOverFrame parent;

    // 패널들
    private JPanel mainContainer; // 전체 내용을 담을 그릇
    private JPanel giverPanel, receiverPanel, btnPanel;

    // 인계자용 컴포넌트
    private JTextField giverNameField;
    private JPasswordField giverPassField;

    // 인수자용 컴포넌트
    private JComboBox<String> receiverNameCombo;
    private JPasswordField receiverPassField;

    // 버튼
    private JButton btnConfirm, btnCancel;

    public HandOverLoginPanel(HandOverFrame parent) {
        this.parent = parent;

        setLayout(new BorderLayout());
        setSize(500,450);
        setBorder(new EmptyBorder(20, 40, 20, 40)); // 전체 화면 여백

        // 메인 컨테이너 (위에서 아래로 쌓기 위해 BoxLayout 사용)
        mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));

        // 각 구역 초기화
        initGiverPanel();
        initReceiverPanel();
        initButtonPanel();

        // 컨테이너에 순서대로 담기
        mainContainer.add(giverPanel);
        mainContainer.add(Box.createVerticalStrut(20)); // 패널 사이 간격
        mainContainer.add(receiverPanel);
        mainContainer.add(Box.createVerticalStrut(20)); // 패널 사이 간격
        mainContainer.add(btnPanel);

        add(mainContainer, BorderLayout.CENTER);
    }

    // 인계자 화면 구성
    private void initGiverPanel() {
        giverPanel = new JPanel(new BorderLayout(0, 10)); // 컴포넌트 간 수직 간격 10
        giverPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true), // 둥근 테두리
                new EmptyBorder(20, 20, 20, 20) // 내부 여백
        ));

        // 제목
        JLabel titleLabel = new JLabel("인계자");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        // 입력 폼
        JPanel formPanel = new JPanel(new GridLayout(2, 1, 0, 10)); // 2행 1열, 간격 10

        // 첫 번째 줄 (관리자 이름)
        JPanel row1 = new JPanel(new BorderLayout(10, 0));
        JLabel label1 = new JLabel("관리자   "); // 글자수 맞춰서 간단히 정렬
        giverNameField = new JTextField("오동준");
        giverNameField.setEditable(false); // 수정 불가
        giverNameField.setBackground(Color.WHITE); // 읽기 전용이라 회색되는 것 방지

        row1.add(label1, BorderLayout.WEST);
        row1.add(giverNameField, BorderLayout.CENTER);

        // 두 번째 줄 (비밀번호)
        JPanel row2 = new JPanel(new BorderLayout(10, 0));
        JLabel label2 = new JLabel("비밀번호");
        giverPassField = new JPasswordField();

        row2.add(label2, BorderLayout.WEST);
        row2.add(giverPassField, BorderLayout.CENTER);

        formPanel.add(row1);
        formPanel.add(row2);

        giverPanel.add(titleLabel, BorderLayout.NORTH);
        giverPanel.add(formPanel, BorderLayout.CENTER);
    }

    // 인수자 화면 구성 (구조는 인계자와 동일)
    private void initReceiverPanel() {
        receiverPanel = new JPanel(new BorderLayout(0, 10));
        receiverPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // 제목
        JLabel titleLabel = new JLabel("인수자");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        // 입력 폼
        JPanel formPanel = new JPanel(new GridLayout(2, 1, 0, 10));

        // 첫 번째 줄 (콤보박스)
        JPanel row1 = new JPanel(new BorderLayout(10, 0));
        JLabel label1 = new JLabel("관리자   ");
        receiverNameCombo = new JComboBox<>(new String[]{"사장님", "오동준", "최성균", "이원호", "조민규"});
        receiverNameCombo.setBackground(Color.WHITE);

        row1.add(label1, BorderLayout.WEST);
        row1.add(receiverNameCombo, BorderLayout.CENTER);

        // 두 번째 줄 (비밀번호)
        JPanel row2 = new JPanel(new BorderLayout(10, 0));
        JLabel label2 = new JLabel("비밀번호");
        receiverPassField = new JPasswordField();

        row2.add(label2, BorderLayout.WEST);
        row2.add(receiverPassField, BorderLayout.CENTER);

        formPanel.add(row1);
        formPanel.add(row2);

        receiverPanel.add(titleLabel, BorderLayout.NORTH);
        receiverPanel.add(formPanel, BorderLayout.CENTER);
    }

    // 버튼 화면 구성
    private void initButtonPanel() {
        btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // 오른쪽 정렬

        btnConfirm = new JButton("확인");
        btnConfirm.setPreferredSize(new Dimension(90, 35));
        btnConfirm.setBackground(new Color(50, 100, 255)); // 파란색
        btnConfirm.setForeground(Color.WHITE);

        btnCancel = new JButton("닫기 (C)");
        btnCancel.setPreferredSize(new Dimension(90, 35));
        btnCancel.setBackground(Color.WHITE);

        // 리스너 연결
        btnConfirm.addActionListener(this);
        btnCancel.addActionListener(this);

        btnPanel.add(btnConfirm);
        btnPanel.add(btnCancel);
    }

    // 버튼 클릭 이벤트 처리
    @Override
    public void actionPerformed(ActionEvent e) {

        // 닫기 버튼
        if (e.getSource() == btnCancel) {
            // 현재 창을 찾아서 닫음
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        }

        // 확인 버튼
        else if (e.getSource() == btnConfirm) {
            String inputPw = new String(receiverPassField.getPassword());

            // 임시 비밀번호 체크 로직 (DB 없이 1234면 통과)
            if ("1234".equals(inputPw)) {
                System.out.println("로그인 성공");

                // 화면 전환: 메인 패널로 교체
                parent.setContentPane(new HandOverMainPanel(parent));
                parent.revalidate();
                parent.repaint();

            } else {
                // 비밀번호 틀림
                JOptionPane.showMessageDialog(this,
                        "비밀번호가 일치하지 않습니다.",
                        "오류",
                        JOptionPane.ERROR_MESSAGE);

                receiverPassField.setText(""); // 비밀번호창 비우기
                receiverPassField.requestFocus(); // 다시 입력하라고 포커스 주기
            }
        }
    }
}
