package client.member;

import dto.MemberDTO;
import dao.MemberDAO;
import util.Sizes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MemberHeader extends JPanel implements ActionListener {

    private JButton btnJoin, btnUpdate, btnDelete;

    private SearchMember searchMember;

    //SearchMember 받아오기
    public MemberHeader(SearchMember searchMember) {
        this.searchMember = searchMember;
        initUI();
    }

    private void initUI() {
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, Sizes.MEMBER_HEADER_HEIGHT ));
        setLayout(new FlowLayout(FlowLayout.LEFT));

        btnJoin = new JButton("가입");
        btnUpdate = new JButton("수정");
        btnDelete = new JButton("삭제");

        add(btnJoin);
        add(btnUpdate);
        add(btnDelete);

        btnJoin.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnDelete.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        // 현재 패널을 포함한 상위 JFrame을 찾아 Dialog의 부모로 사용
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);

        if (src == btnJoin) {
            new JoinDialog(parent,searchMember);
        }
        else if (src == btnUpdate) {


            int row = searchMember.getSelectedRow(); // 선택된 줄 번호 가져옴
            if (row == -1) {
                JOptionPane.showMessageDialog(parent, "수정할 회원을 선택해주세요.");
                return;
            }
            String id = (String) searchMember.table.getValueAt(row, 2); // 아이디 가져옴

            MemberDTO targetMember = MemberDAO.getInstance().getMemberById(id);

            new UpdateDialog(parent,searchMember,targetMember);
        }
        else if (src == btnDelete) {
            int row = searchMember.getSelectedRow(); // SearchMember에게 선택된 줄 번호 물어보기

            if (row == -1) { // 선택 안 했으면 경고
                JOptionPane.showMessageDialog(parent, "삭제할 회원을 선택해주세요.");
                return;
            }

            new DeleteDialog(parent, searchMember, row); // DeleteDialog 호출 (searchMember와 줄 번호를 넘겨줌)
        }
    }
}
