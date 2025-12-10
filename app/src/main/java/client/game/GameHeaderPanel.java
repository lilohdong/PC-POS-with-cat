package client.game;

import font.ClearGodic;
import util.Sizes;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameHeaderPanel extends JPanel implements ActionListener {
    private JButton updateBtn, insertBtn;

    public GameHeaderPanel() {
        initUI();
    }
    // 단순 헤더 패널 생성
    private void initUI() {
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, 74));
        setBorder(new MatteBorder(1, 0, 1, 0, Color.BLACK));
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel titleLabel = new JLabel("게임 통계", JLabel.CENTER);
        titleLabel.setFont(new ClearGodic(36));
        add(titleLabel);

        JPanel btnPanel = new JPanel();
        updateBtn = new JButton("게임 수정/삭제");
        insertBtn = new JButton("게임 추가");
        btnPanel.add(insertBtn);
        btnPanel.add(updateBtn);
        add(btnPanel);

        insertBtn.addActionListener(this);
        updateBtn.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        // JPanel이 부착된 최상위 부모 Frame (JFrame)을 찾습니다.
        Window window = SwingUtilities.getWindowAncestor(this);
        JFrame parentFrame = (window instanceof JFrame) ? (JFrame) window : null;

        if (parentFrame == null) {
            JOptionPane.showMessageDialog(this, "부모 창을 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        switch (action) {
            case "게임 추가" -> {
                // InsertDialog (삽입 창) 띄우기
                InsertDialog insertDialog = new InsertDialog(parentFrame);
                insertDialog.setVisible(true);
            }
            case "게임 수정/삭제" -> {
                // ModifyDeleteDialog (수정/삭제 창) 띄우기
                ModifyDeleteDialog modDelDialog = new ModifyDeleteDialog(parentFrame);
                modDelDialog.setVisible(true);
            }
        }
    }
}
