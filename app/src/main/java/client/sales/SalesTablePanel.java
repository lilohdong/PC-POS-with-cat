package client.sales;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import util.Sizes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

public class SalesTablePanel extends JPanel {
    private JTable mainTable;
    private JComboBox<String> selectD;
    private String[] duration = { "일간", "주간", "연간" };
    private final ImageIcon cal = new ImageIcon(getClass().getResource("/imgs/calendar.png"));
    private JButton calBtn;
    private final String[] column = {"매출번호", "아이디", "매출발생일", "매출발생시간", "상품", "매출액"};
    private DefaultTableModel tm;

    private JPanel dateAppearance;
    private DatePicker datePicker;
    private JComboBox<String> startTime;
    private JComboBox<String> endTime;
    private JLabel period; // 날짜 표시용 라벨


    public SalesTablePanel() {
        initUI();
    }

    private void initUI() {
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, Sizes.SALES_TABLE_HEIGHT));
        setLayout(new BorderLayout());

        // DatePicker 설정 (먼저 생성해야 패널에 넣을 수 있음)
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra("yyyy-MM-dd");
        settings.setAllowKeyboardEditing(false);
        datePicker = new DatePicker(settings);
        datePicker.setDateToToday();
        // datePicker 리스너 설정
        datePicker.addDateChangeListener((e)->{
                LocalDate date = e.getNewDate();
                if(date != null) {
                    period.setText(date.toString());
                    datePicker.setVisible(false);
                }
        });

        // 레전드 패널 생성
        dateAppearance = createMainHeaderPanel();
        add(dateAppearance, BorderLayout.NORTH);

        tm = new DefaultTableModel(column,0);
        mainTable = new JTable(tm);

        JTableHeader header = mainTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 35));

        TableColumnModel tcm = header.getColumnModel();
        tcm.setColumnSelectionAllowed(false);
        JScrollPane sp = new JScrollPane(mainTable);
        sp.setBorder(null);
        add(sp);
    }

    private JPanel createMainHeaderPanel() {
        JPanel jp = new JPanel();
        jp.setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, 40));
        jp.setBackground(Color.white);
        jp.setLayout(new FlowLayout(FlowLayout.LEFT));

        selectD = new JComboBox<>(duration);
        selectD.setPreferredSize(new Dimension(248, 40));

        calBtn = new JButton(cal);
        calBtn.setPreferredSize(new Dimension(24, 24));
        calBtn.addActionListener(new calListener());
        period = new JLabel("날짜를 선택하세요");

        jp.add(selectD);
        jp.add(calBtn);
        jp.add(datePicker);

        startTime = new JComboBox<>();
        endTime = new JComboBox<>();
        for (int i = 0; i <= 24; i++) {
            // 분은 항상 00으로 고정
            String time = String.format("%02d:00", i);
            startTime.addItem(time);
            endTime.addItem(time);
        }
        startTime.setMaximumRowCount(6);
        endTime.setMaximumRowCount(6);
        jp.add(startTime);
        jp.add(endTime);

        jp.add(period, FlowLayout.CENTER);
        return jp;
    }

    class calListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 버튼 클릭 시 토글 (보이면 숨기고, 안 보이면 보이게)
            boolean currentStatus = datePicker.isVisible();
            datePicker.setVisible(!currentStatus);
            // 만약 보이게 될 때 달력 팝업을 바로 띄우고 싶다면:
            if (!currentStatus) {
                datePicker.openPopup();
            }
        }
    }
}
