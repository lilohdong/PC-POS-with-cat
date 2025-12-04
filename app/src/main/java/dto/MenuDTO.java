package dto;

public class MenuDTO {
    private String menuId;
    private String mName;
    private int mPrice;
    private String cId;

    public  MenuDTO(String menuId, String mName, int mPrice, String cId) {
        this.menuId = menuId;
        this.mName = mName;
        this.mPrice = mPrice;
        this.cId = cId;
    }

    public String getMenuId() {return menuId;}
    public void setMenuId(String menuId) {this.menuId = menuId;}

    public String getMName() {return mName;}
    public void setMName(String mName) {this.mName = mName;}

    public int getMPrice() {return mPrice;}
    public void setMPrice(int mPrice) {this.mPrice = mPrice;}

    public String getMId() {return cId;}
    public void setMId(String cId) {this.cId = cId;}
}
