package client.handover;

import javax.swing.*;

public class HandOverFrame extends JFrame {

    public HandOverFrame() {

        setTitle("인수인계");
        setSize(1200, 832);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setContentPane(new HandOverLoginPanel(this));
        setVisible(true);

    }

    public static void main(String[] args) {
        new HandOverFrame();
    }

}
