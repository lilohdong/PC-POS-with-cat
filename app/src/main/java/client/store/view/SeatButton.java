package client.store.view;

import client.store.SeatStatus;


import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SeatButton extends JPanel {
    private static final Color COL_AVAIL = new Color(200, 200, 200);
    private static final Color COL_CHILD = new Color(255, 180, 180);
    private static final Color COL_ADULT = new Color(180, 180, 255);
    private static final Color COL_UNAVAIL = new Color(150, 150, 150);
    private static final Color COL_SELECT = new Color(255, 255, 150);

    private int seatNumber;
    private SeatStatus status = SeatStatus.AVAILABLE;
    private boolean isSelected = false;

    private JLabel numberLabel;
    private JLabel nameLabel;
    private JLabel timeLabel;

    public SeatButton(int seatNumber) {
        this.seatNumber = seatNumber;
        setLayout(new BorderLayout());
        setBorder(new LineBorder(Color.GRAY, 1));
        setPreferredSize(new Dimension(80, 65));



        initLabels();
        updateAppearance();
    }

    private void addHoverEffect() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isSelected) {
                    setBorder(new LineBorder(Color.BLUE, 2));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isSelected) {
                    setBorder(new LineBorder(Color.GRAY, 1));
                }
            }
        });
    }

    private void initLabels() {
        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.setOpaque(false);

        numberLabel = createLabel("PC " + seatNumber, Font.BOLD, 11);
        nameLabel = createLabel("", Font.PLAIN, 10);
        timeLabel = createLabel("", Font.PLAIN, 10);

        infoPanel.add(numberLabel);
        infoPanel.add(nameLabel);
        infoPanel.add(timeLabel);
        add(infoPanel, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text, int style, int size) {
        JLabel lbl = new JLabel(text, JLabel.CENTER);
        lbl.setFont(new Font("맑은 고딕", style, size));
        return lbl;
    }

    public void updateStatus(SeatStatus status, String name, String timeStr) {
        this.status = status;
        this.nameLabel.setText(name == null ? "" : name);
        this.timeLabel.setText(timeStr == null ? "" : timeStr);
        updateAppearance();
    }

    private void updateAppearance() {
        if (isSelected) {
            setBackground(COL_SELECT);
        } else {
            switch (status) {
                case AVAILABLE -> { setBackground(COL_AVAIL); nameLabel.setText("이용가능"); }
                case OCCUPIED_CHILD -> setBackground(COL_CHILD);
                case OCCUPIED_ADULT -> setBackground(COL_ADULT);
                case UNAVAILABLE -> { setBackground(COL_UNAVAIL); nameLabel.setText("이용불가"); }
            }
        }
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        setBorder(new LineBorder(selected ? Color.BLUE : Color.GRAY, selected ? 3 : 1));
        updateAppearance();
    }

    public int getSeatNumber() { return seatNumber; }
    public SeatStatus getStatus() { return status; }
}
