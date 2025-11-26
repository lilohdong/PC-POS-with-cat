package client.sales;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import util.Sizes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class SalesTablePanel extends JPanel {
    private JTable mainTable;
    private JComboBox<String> selectD;
    private String[] duration = { "일간", "주간", "월간" };
    private final ImageIcon cal = new ImageIcon(getClass().getResource("/imgs/calendar.png"));
    private JButton calBtn;
    private final String[] column = {"매출번호", "아이디", "매출발생일", "매출발생시간", "상품", "매출액"};
    private DefaultTableModel tm;

    private JPanel dateAppearance;
    private DatePicker datePicker;
    private JComboBox<String> startTime;
    private JComboBox<String> endTime;
    private JLabel period; // 날짜 표시용 라벨

    private LocalDate startDate;
    private LocalDate endDate;

    private JButton searchBtn;

    public SalesTablePanel() {
        initUI();
        updatePeriodDisplay(datePicker.getDate(), (String) selectD.getSelectedItem());
    }

    private void initUI() {
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, Sizes.SALES_TABLE_HEIGHT));
        setLayout(new BorderLayout());

        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra("yyyy-MM-dd");
        settings.setAllowKeyboardEditing(false);
        datePicker = new DatePicker(settings);
        datePicker.setDateToToday();
        // DatePicker 리스너 -> 서낵하면 updatePeriodDisplay
        datePicker.addDateChangeListener((e)->{
            LocalDate date = e.getNewDate();
            if(date != null) {
                // 선택된 기간 타입에 맞춰 기간 표시 및 계산
                updatePeriodDisplay(date, (String) selectD.getSelectedItem());
                datePicker.setVisible(false);
            }
        });

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

        selectD.addActionListener(e -> {
            if(e.getActionCommand().equals("주간") || e.getActionCommand().equals("월간")) {
                startTime.setSelectedIndex(0);
                endTime.setSelectedItem("24:00");
                startTime.setEnabled(false);
                endTime.setEnabled(false);
            } else {
                startTime.setEnabled(true);
                endTime.setEnabled(true);
            }
            updatePeriodDisplay(datePicker.getDate(), (String) selectD.getSelectedItem());
        });

        calBtn = new JButton(cal);
        calBtn.setPreferredSize(new Dimension(24, 24));
        calBtn.addActionListener(new CalListener());

        period = new JLabel("날짜를 선택하세요");
        period.setFont(period.getFont().deriveFont(Font.BOLD, 14f));

        jp.add(selectD);
        jp.add(calBtn);

        datePicker.setVisible(false);
        jp.add(datePicker);

        startTime = new JComboBox<>();
        endTime = new JComboBox<>();
        for (int i = 0; i <= 24; i++) {
            String time = String.format("%02d:00", i);
            startTime.addItem(time);
            endTime.addItem(time);
        }
        startTime.setMaximumRowCount(6);
        endTime.setMaximumRowCount(6);

        jp.add(startTime);
        jp.add(endTime);

        jp.add(period);

        searchBtn = new JButton("조회");
        searchBtn.addActionListener(new SearchListener());
        jp.add(searchBtn);
        return jp;
    }


    /**
     * 선택된 날짜와 기간 타입에 따라 시작일/종료일을 계산하고 라벨을 업데이트합니다.
     * @param selectedDate DatePicker에서 선택된 단일 날짜
     * @param durationType JComboBox에서 선택된 기간 타입 ("일간", "주간", "연간")
     */
    private void updatePeriodDisplay(LocalDate selectedDate, String durationType) {
        if (selectedDate == null) {
            period.setText("날짜를 선택하세요");
            return;
        }

        LocalDate start;
        LocalDate end;

        switch (durationType) {
            case "일간":
                start = selectedDate;
                end = selectedDate;
                period.setText(start.toString());
                break;
            case "주간":
                // 주의 시작일 (월요일)
                start = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                // 주의 종료일 (일요일)
                end = selectedDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                period.setText(start.toString() + " ~ " + end.toString());
                break;
            case "월간":
                // 월 1일
                start = selectedDate.with(TemporalAdjusters.firstDayOfMonth());
                // 월 30일
                end = selectedDate.with(TemporalAdjusters.lastDayOfMonth());
                period.setText(start.toString() + " ~ " + end.toString());
                break;
            default:
                // 기본값: 일간
                start = selectedDate;
                end = selectedDate;
                period.setText(start.toString());
                break;
        }
        this.startDate = start;
        this.endDate = end;

    }

    class CalListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            boolean currentStatus = datePicker.isVisible();
            datePicker.setVisible(!currentStatus);

            if (!currentStatus) {
                datePicker.openPopup();
            }
        }
    }
    class SearchListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LocalDate start = startDate;
            LocalDate end = endDate;

            String startTimeStr = (String) startTime.getSelectedItem();
            String endTimeStr = (String) endTime.getSelectedItem();

            if (start == null || end == null) {
                JOptionPane.showMessageDialog(SalesTablePanel.this,
                        "날짜 기간이 설정되지 않았습니다.",
                        "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 4. 데이터 조회 로직 호출 (TODO: 실제 서비스 계층 메서드)


        }
    }
}