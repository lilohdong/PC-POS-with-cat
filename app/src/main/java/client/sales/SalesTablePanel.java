package client.sales;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import dao.SalesDAO;
import dto.SalesDTO;
import java.util.List;

import service.SalesTableService;
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
    private JComboBox<String> selectD;
    private String[] duration = { "일간", "주간", "월간" };
    private final ImageIcon cal = new ImageIcon(getClass().getResource("/imgs/calendar.png"));
    private JButton calBtn;
    private final String[] column = {"주문번호", "아이디", "매출발생일", "매출발생시간", "상품","수량","매출액"};
    private DefaultTableModel tm;

    private JPanel dateAppearance;
    private DatePicker datePicker;
    private JComboBox<String> startTime;
    private JComboBox<String> endTime;
    private JLabel period; // 날짜 표시용 라벨

    private LocalDate startDate;
    private LocalDate endDate;

    private JButton searchBtn;

    private JLabel allSales;
    SalesTableService sts = SalesTableService.getInstance();
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
        // DatePicker 리스너 -> 선택하면 updatePeriodDisplay
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
        JTable mainTable = new JTable(tm);
        sts.initTable(tm);
        JTableHeader header = mainTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 35));

        TableColumnModel tcm = header.getColumnModel();
        tcm.setColumnSelectionAllowed(false);
        JScrollPane sp = new JScrollPane(mainTable);
        sp.setBorder(null);
        add(sp);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        allSales = new JLabel(); // 한 줄 안에 바로
        allSales.setText(SalesTableService.getInstance().calculateTotalSales(tm));
        bottomPanel.add(allSales);
        bottomPanel.setMinimumSize(new Dimension(Sizes.PANEL_WIDTH,100));
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createMainHeaderPanel() {
        JPanel jp = new JPanel();
        jp.setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, 40));
        jp.setLayout(new FlowLayout(FlowLayout.LEFT));

        selectD = new JComboBox<>(duration);
        selectD.setPreferredSize(new Dimension(248, 35));

        selectD.addActionListener(e -> {
            String selectedDuration = (String) selectD.getSelectedItem();

            if("주간".equals(selectedDuration) || "월간".equals(selectedDuration)) {
                startTime.setSelectedIndex(0); // 00:00
                endTime.setSelectedItem("24:00");
                startTime.setEnabled(false);
                endTime.setEnabled(false);
            } else { // "일간"일 경우
                datePicker.setDateToToday(); // 오늘로 설정
                startTime.setEnabled(true);
                endTime.setEnabled(true);
                endTime.setSelectedItem("24:00"); // 00 ~ 24 초기화
            }

            // 기간 표시 업데이트
            updatePeriodDisplay(datePicker.getDate(), selectedDuration);
        });

        calBtn = new JButton(cal);
        calBtn.setPreferredSize(new Dimension(24, 24));
        // 동그래지는거 해결하는 코드
        calBtn.setBorder(BorderFactory.createEmptyBorder());
        calBtn.setContentAreaFilled(false);
        calBtn.setFocusPainted(false);
        // 동그래지는거 해결하는 코드
        calBtn.addActionListener(new CalListener());

        period = new JLabel("날짜를 선택하세요");
        period.setFont(period.getFont().deriveFont(Font.BOLD, 14f));

        jp.add(selectD);
        jp.add(calBtn);

        datePicker.setVisible(false);
        jp.add(datePicker);

        startTime = new JComboBox<>();
        endTime = new JComboBox<>();
        // 00:00 ~ 24:00까지 전부 추가 (startTime이랑 endTime 전부)
        for (int i = 0; i <= 24; i++) {
            String time = String.format("%02d:00", i);
            startTime.addItem(time);
            endTime.addItem(time);
        }
        startTime.setMaximumRowCount(6);
        endTime.setMaximumRowCount(6);
        endTime.setSelectedItem("24:00");
        jp.add(startTime);
        jp.add(endTime);

        jp.add(period);

        searchBtn = new JButton("조회");
        searchBtn.addActionListener(new SearchListener());
        jp.add(searchBtn);
        return jp;
    }


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
                // 월요일
                start = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                // 일요일
                end = selectedDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                period.setText(start + " ~ " + end);
                break;
            case "월간":
                // 월 1일
                start = selectedDate.with(TemporalAdjusters.firstDayOfMonth());
                // 월 30일
                end = selectedDate.with(TemporalAdjusters.lastDayOfMonth());
                period.setText(start + " ~ " + end);
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
            if ("24:00".equals(endTimeStr)) {
                endTimeStr = "23:59:59"; // HH:mm:ss 형식으로 맞춤
            }

            SalesDAO salesDAO = SalesDAO.getInstance();
            try {
                // start.toString()과 end.toString()은 yyyy-mm-dd 형식
                List<SalesDTO> salesList = salesDAO.getSalesList(
                        start.toString(),
                        end.toString(),
                        startTimeStr,
                        endTimeStr
                );

                sts.updateTable(salesList,tm);
                allSales.setText(SalesTableService.getInstance().calculateTotalSales(tm));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(SalesTablePanel.this,
                        "매출 조회 중 오류 발생: " + ex.getMessage(),
                        "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}