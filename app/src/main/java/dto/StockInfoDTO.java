package dto;

public class StockInfoDTO {
    private String stockInfoId;
    private String ingredientId;
    private String unitName;
    private int unitQuantity;

    public StockInfoDTO() {}

    public StockInfoDTO(String stockInfoId, String ingredientId, String unitName, int unitQuantity) {
        this.stockInfoId = stockInfoId;
        this.ingredientId = ingredientId;
        this.unitName = unitName;
        this.unitQuantity = unitQuantity;
    }

    public String getStockInfoId() {
        return stockInfoId;
    }
    public void setStockInfoId(String stockInfoId) {
        this.stockInfoId = stockInfoId;
    }

    public String getIngredientId() {
        return ingredientId;
    }
    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getUnitName() {
        return unitName;
    }
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public int getUnitQuantity() {
        return unitQuantity;
    }
    public void setUnitQuantity(int unitQuantity) {
        this.unitQuantity = unitQuantity;
    }
}
