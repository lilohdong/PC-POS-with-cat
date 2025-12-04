package client.component;

import font.SideBarBtnFont;
import service.NowAdminListener;
import service.NowAdminService;
import util.Sizes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SideBar extends JPanel implements NowAdminListener {
    public SideBar() {
        initUI();
        NowAdminService.getInstance().addListener(this);
        // 초기 실행 시, 관리자 아님.
        updateAccess(NowAdminService.getInstance().isAdminMode());
    }
    // 버튼 MainFrame에서 Control 해야하기 때문에 public으로 선언
    public JButton manageBtn;
    public JButton orderBtn;
    public JButton stockBtn;
    public JButton memberBtn;
    public JButton handOverBtn;
    public JButton salesBtn;
    public JButton staffBtn;
    public JButton gameBtn;
    private JButton chmodBtn;
    protected JButton darkModeBtn;
    private final Color admin = new Color(170,209, 231);
    private final Color noAdmin = new Color(255, 102, 102);
    private void initUI() {
        // 초기 패널 설정 //
        setPreferredSize(new Dimension(Sizes.SIDEBAR_WIDTH, Sizes.SIDEBAR_HEIGHT));
        setBackground(Color.white);
        setBorder(BorderFactory.createLineBorder(Color.black, 1, true));
        setLayout(new BorderLayout());
        // 초기 패널 설정 끝 //

        // 관리자 모드 변경 버튼
        chmodBtn = new JButton("관리자 모드 변경");
        chmodBtn.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        chmodBtn.setPreferredSize(new Dimension(Sizes.SIDEBAR_WIDTH,64));
        chmodBtn.setBackground(admin);
        chmodBtn.setMinimumSize(new Dimension(Sizes.SIDEBAR_WIDTH,64));
        chmodBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE,64));
        chmodBtn.setFont(new SideBarBtnFont());
        chmodBtn.setForeground(Color.black);
        // 관리자 모드 변경 리스너
        chmodBtn.addActionListener(e -> {
            if(!NowAdminService.getInstance().isAdminMode()){
                showPasswordInputDialog();
            } else {
                disableAdmin();
            }
        });
        add(chmodBtn, BorderLayout.NORTH);
        // 관리자 모드 버튼 끝
        JPanel btnSets = new JPanel();

        BoxLayout box = new BoxLayout(btnSets, BoxLayout.Y_AXIS);
        btnSets.setLayout(box);

        initReal(btnSets);
        add(btnSets, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        darkModeBtn = new JButton("DarkMode");

        bottom.add(darkModeBtn);
        add(bottom, BorderLayout.SOUTH);
    }
    // 관리자 인증 매커니즘
    private void showPasswordInputDialog() {
        JPasswordField pf = new JPasswordField();
        int okCxl = JOptionPane.showConfirmDialog(
                this,
                pf,
                "관리자 비밀번호 입력",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (okCxl == JOptionPane.OK_OPTION) {
            String password = new String(pf.getPassword());
            // 비밀번호는 PasswdUtil 에 상수로 정의되어 있음.
            // 기본 비밀번호 = manager
            boolean success = NowAdminService.getInstance().authenticate(password);

            if (success) {
                JOptionPane.showMessageDialog(this, "관리자 모드가 활성화되었습니다.");
            } else {
                JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    // 접근 권한 업데이트 메소드, 버튼 색 변경까지 처리
    private void updateAccess(boolean isAdminMode) {
        salesBtn.setEnabled(isAdminMode);
        staffBtn.setEnabled(isAdminMode);

        String modeText = isAdminMode ? "관리자 모드" : "관리자 모드 변경";
        chmodBtn.setText(modeText);
        if(isAdminMode) {
            chmodBtn.setBackground(noAdmin);
        } else {
            chmodBtn.setBackground(admin);
        }
    }

    @Override
    public void onAdminModeChanged(Boolean isAdminMode) {
        updateAccess(isAdminMode);
    }

    private void disableAdmin(){
        NowAdminService.getInstance().disableAdminMode();
        JOptionPane.showMessageDialog(this, "관리자 모드가 해제되었습니다.");
        updateAccess(false);
    }
    // 관리자 모드 변경 루틴 끝

    private JButton initBtn(String name) {
        JButton jBtn = new JButton(name);
        jBtn.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        jBtn.setPreferredSize(new Dimension(200,60));
        jBtn.setMinimumSize(new Dimension(200,60));
        jBtn.setBackground(new Color(146, 160, 250));
        jBtn.setFont(new SideBarBtnFont());
        jBtn.setForeground(Color.black);

        return jBtn;
    }
    private void initReal(JPanel contentPanel) {
        contentPanel.add(Box.createVerticalStrut(Sizes.BOX_STRUT)); //간격 17

        JPanel[] p = new JPanel[8];
        for (int i = 0; i < p.length; i++) {
            p[i] = new JPanel(new FlowLayout(FlowLayout.CENTER));
            p[i].setMinimumSize(new Dimension(200,63));
            p[i].setBackground(null);
            p[i].setBorder(BorderFactory.createLineBorder(Color.black, 0, true));
        }

        manageBtn = initBtn("◎ 매장관리");
        orderBtn = initBtn("♣ 상품판매");
        stockBtn = initBtn("▒ 재고관리");
        memberBtn = initBtn("▣ 회원관리");
        handOverBtn = initBtn("§ 인수인계");
        salesBtn = initBtn("★ 매출관리");
        staffBtn = initBtn("★ 직원관리");
        gameBtn = initBtn("ⓖ 게임통계");

        p[0].add(manageBtn);
        p[1].add(orderBtn);
        p[2].add(stockBtn);
        p[3].add(memberBtn);
        p[4].add(handOverBtn);
        p[5].add(salesBtn);
        p[6].add(staffBtn);
        p[7].add(gameBtn);

        for (JPanel jPanel : p) {
            contentPanel.add(jPanel);
            contentPanel.add(Box.createVerticalStrut(Sizes.BOX_STRUT));
        }
        contentPanel.add(Box.createVerticalGlue());
    }
    public void setNavListener(ActionListener listener) {
        manageBtn.addActionListener(listener);
        orderBtn.addActionListener(listener);
        stockBtn.addActionListener(listener);
        memberBtn.addActionListener(listener);
        handOverBtn.addActionListener(listener);
        salesBtn.addActionListener(listener);
        staffBtn.addActionListener(listener);
        gameBtn.addActionListener(listener);
    }


}
