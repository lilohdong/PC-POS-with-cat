package client.handover;

import javax.swing.*;
import java.awt.*;

public class HandOverLoginPanel extends JPanel {

    private HandOverFrame parent;
    // 인계자
    private JTextField giverName;
    private JPasswordField giverPassword;
    private JLabel giverLabel;

    // 인수자
    private JComboBox<String> receiverName;
    private JPasswordField receiverPassword;
    private JLabel receiverLabel;

    private JButton buttonConfirm;
    private JButton buttonCancel;

    public HandOverLoginPanel(HandOverFrame parent) {
        this.parent = parent;
        initUI();
    }

    private void initUI() {
        setLayout(new BoxLayout(this,  BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20,20));

        //인계자
        giverLabel = new JLabel("인계자");
        giverLabel.setFont(new Font("맑은 고딕", Font.BOLD,18));
        giverLabel.setAlignmentX(Component.LEFT_ALIGNMENT);   // 왼쪽 정렬

        JPanel giverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        giverPanel.setLayout(new BoxLayout(giverPanel, BoxLayout.Y_AXIS));
        giverPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        giverName = new JTextField();
        giverPassword = new JPasswordField();

        giverPanel.add(giverName);
        giverPanel.add(giverPassword);

        //인수자
        receiverLabel = new JLabel("인수자");
        receiverLabel.setFont(new Font("맑은 고딕", Font.BOLD,18));
        receiverLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel receiverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        receiverPanel.setLayout(new BoxLayout(receiverPanel, BoxLayout.Y_AXIS));
        receiverPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        receiverName = new JComboBox<>(new String[]{"사장님", "오동준", "최성균", "이원호", "조민규"});
        receiverPassword = new JPasswordField();

        receiverPanel.add(receiverName);
        receiverPanel.add(receiverPassword);

        add(giverPanel);
        add(receiverPanel);

        //버튼
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        buttonConfirm = new JButton("확인");
        buttonCancel = new JButton("닫기");

        btnPanel.add(buttonConfirm);
        btnPanel.add(buttonCancel);

        add(btnPanel);



    }

}
