package client.uiUtil;

import javax.swing.border.AbstractBorder;
import java.awt.*;

public class RoundBorder extends AbstractBorder {
    private int radius;
    private Color color;
    private final int thicknessTop, thicknessBottom, thicknessLeft, thicknessRight;

    public RoundBorder(int radius, Color color, int thicknessTop, int thicknessLeft, int thicknessBottom, int thicknessRight) {
        this.radius = radius;
        this.color = color;
        this.thicknessTop = thicknessTop;
        this.thicknessBottom = thicknessBottom;
        this.thicknessLeft = thicknessLeft;
        this.thicknessRight = thicknessRight;
    }

    // Top Bottom, Left Right로 생성
    public RoundBorder(int radius, Color color, int thicknessTb, int thicknessLr) {
        this(radius, color, thicknessTb, thicknessLr, thicknessTb, thicknessLr);
    }

    // 1 thickness로 생성
    public RoundBorder(int radius, Color color, int thickness) {
        this(radius, color, thickness, thickness, thickness, thickness);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);

        // 실제 usable width/height는 -1 해줘야 테두리가 안 끊어짐
        int w = width - 1;
        int h = height - 1;

    /*
        핵심 원리:
        1. 테두리를 thickness 만큼 안쪽으로 줄여가면서 반복해서 그린다.
        2. border 두께는 왼쪽/오른쪽, 위/아래가 각각 다를 수 있으므로,
          drawRoundRect의 x, y, width, height를 각 반복에서 개별 조정해야 한다.
    */

        int maxThickness = Math.max(Math.max(thicknessTop, thicknessBottom),
                Math.max(thicknessLeft, thicknessRight));

        for (int i = 0; i < maxThickness; i++) {

            int dx1 = Math.min(i, thicknessLeft);
            int dy1 = Math.min(i, thicknessTop);
            int dx2 = Math.min(i, thicknessRight);
            int dy2 = Math.min(i, thicknessBottom);

            g2d.drawRoundRect(
                    x + dx1,
                    y + dy1,
                    w - dx1 - dx2,
                    h - dy1 - dy2,
                    radius,
                    radius
            );
        }

        g2d.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(thicknessTop, thicknessLeft, thicknessBottom, thicknessRight);
    }
}
