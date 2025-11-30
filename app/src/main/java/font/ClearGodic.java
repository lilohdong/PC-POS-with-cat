package font;

import java.awt.*;

public class ClearGodic extends Font {
    // 사이즈만 지정해서 넣는 맑은 고딕, 기본 = BOLD
    public ClearGodic(int size){
        super("맑은 고딕", Font.BOLD, size);
    }
    // FONT.PLAIN, FONT.BOLD, FONT.ITALIC 셋 중 하나
    public ClearGodic(int fontBolder,int size){
        super("맑은 고딕",fontBolder, size);
    }
}
