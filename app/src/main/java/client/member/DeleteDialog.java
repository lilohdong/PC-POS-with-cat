package client.member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class DeleteDialog extends JDialog implements  ActionListener {

    private JPanel confirmPanel, donePanel;

    private JLabel titleLabel, descLabel, dateLabel;
    private JButton okButton, cancelButton, doneButton;

    private String currentDate;

    // 부모 창 가져오기
    private SearchMember searchMember;
    private int selectedRow; // 지울 행 번호


    public DeleteDialog(JFrame parents, SearchMember searchMember, int row){
        super(parents, "회원 삭제", true);
        this.searchMember = searchMember;
        this.selectedRow = row;

        setLayout(new BorderLayout());
        setUndecorated(true); // 타이틀바 없애기
        setSize(450, 250);
        setLocationRelativeTo(parents);
        getContentPane().setBackground(Color.WHITE);

        // 현재 날짜 구하기(형식에 맞춰)
        LocalDateTime now = LocalDateTime.now();
        currentDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        initConfirmPanel();
        initDonePanel();

        add(confirmPanel, BorderLayout.CENTER);

        setVisible(true);
    }


    // 첫번째 화면
    private void initConfirmPanel(){

        confirmPanel = new JPanel();
        confirmPanel.setLayout(new BorderLayout());
        confirmPanel.setBackground(Color.WHITE);
        confirmPanel.setBorder(new EmptyBorder(30,30,30,30));  // 여백 설정

        // 상단 텍스트 + 경고 문구
        JPanel titlePanel = new JPanel(new GridLayout(2,1));
        titlePanel.setBackground(Color.WHITE);

        titleLabel = new JLabel("회원을 삭제 하시겠습니까?");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        descLabel = new JLabel("삭제된 회원의 정보는 복구할 수 없습니다.");
        descLabel.setFont(new Font("맑은고딕", Font.PLAIN, 14));
        descLabel.setForeground(Color.GRAY);

        titlePanel.add(titleLabel);
        titlePanel.add(descLabel);

        //하단 버튼 + 날짜
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);

        cancelButton = new JButton("취소");
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(100,30));

        okButton = new JButton("확인");
        okButton.setBackground(Color.RED);
        okButton.setForeground(Color.WHITE);
        okButton.setPreferredSize(new Dimension(100,30));

        cancelButton.addActionListener(this);
        okButton.addActionListener(this);

        btnPanel.add(okButton);
        btnPanel.add(cancelButton);

        dateLabel = new JLabel("삭제 일시 : "+ currentDate);
        dateLabel.setForeground(Color.LIGHT_GRAY);

        bottomPanel.add(btnPanel,BorderLayout.CENTER);
        bottomPanel.add(dateLabel,BorderLayout.SOUTH);

        confirmPanel.add(titlePanel,BorderLayout.NORTH);
        confirmPanel.add(bottomPanel,BorderLayout.SOUTH);


    }

    // 두번째 화면
    private void initDonePanel(){
        donePanel = new JPanel();
        donePanel.setLayout(new BorderLayout());
        donePanel.setBackground(Color.WHITE);
        donePanel.setBorder(new EmptyBorder(30,30,30,30));

        // 상단 텍스트 + 경고 문구
        JPanel titlePanel = new JPanel(new GridLayout(2,1));
        titlePanel.setBackground(Color.WHITE);

        titleLabel = new JLabel("회원이 정상적으로 삭제 되었습니다.");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        descLabel = new JLabel("삭제된 회원의 정보는 복구할 수 없습니다.");
        descLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        descLabel.setForeground(Color.GRAY);

        titlePanel.add(titleLabel);
        titlePanel.add(descLabel);

        // 하단 버튼 + 날짜
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);

        doneButton = new JButton("확인");
        doneButton.setBackground(Color.BLUE);
        doneButton.setForeground(Color.WHITE);
        doneButton.setPreferredSize(new Dimension(100,30));

        doneButton.addActionListener(this);

        btnPanel.add(doneButton);

        dateLabel = new JLabel("삭제 일시 : "+ currentDate);
        dateLabel.setForeground(Color.LIGHT_GRAY);

        bottomPanel.add(btnPanel,BorderLayout.CENTER);
        bottomPanel.add(dateLabel,BorderLayout.SOUTH);

        donePanel.add(titlePanel,BorderLayout.NORTH);
        donePanel.add(bottomPanel,BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == cancelButton) { // 취소 버튼 (그냥 닫기)
            dispose();
        } else if (e.getSource() == okButton) {  // 빨간 확인 버튼 (삭제하고, 화면 전환)
            searchMember.removeRow(selectedRow); // 실제 데이터를 지우는 부분

            remove(confirmPanel); // 첫번째 화면 지우기
            add(donePanel);       //  두번째 화면 붙이기

            revalidate(); // 화면 갱신
            repaint();    // 다시 생성
        }

        // 파란 확인 버튼 (진짜 닫기)
        else if (e.getSource() == doneButton) {
            dispose();
        }

    }

}
