package client.stock.view;

import javax.swing.*;
import java.awt.*;

//해당 java파일 chatGPT사용 -> 모르는 부분 주석 이용해서 공부하기
public class AddStockFrame extends JFrame{
    public AddStockFrame(){
        setTitle("입고 등록");
        setSize(700, 300);
        setLocationRelativeTo(null); //화면 중앙에 나타내는 명령어
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //입력 영역: 4행 2열 그리드 레이아웃(10px 간격), 가로/세로 간격 10px씩 + 20px padding
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //상품 코드, 이름, 입고수 입력(DB에서 코드가 auto_increment시 제거 가능성)
        JTextField tfCode = new JTextField();
        JTextField tfName = new JTextField();
        JTextField tfAmount = new JTextField();

        //콤보박스
        String[] categories = {"라면", "음료", "사이드", "토핑"};
        JComboBox<String> cbCategory = new JComboBox<>(categories);

        panel.add(new JLabel("코드:"));
        panel.add(tfCode);
        panel.add(new JLabel("이름:"));
        panel.add(tfName);
        panel.add(new JLabel("카테고리:"));
        panel.add(cbCategory);
        panel.add(new JLabel("입고 수량:"));
        panel.add(tfAmount);

        //등록 버튼
        JButton btnRegister = new JButton("등록");

        //등록 버튼 이벤트 리스너
        btnRegister.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "입고 등록 완료!");
            dispose(); //현재 창만 닫기 (메인 창 유지)
        });

        JPanel bottom = new JPanel();
        bottom.add(btnRegister);

        add(panel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }
}
