package za.bc.cleaninginventory.model.dto;

public class LowStockDTO {

    private String productName;
    private String campus;
    private int stock;

    public LowStockDTO() {
    }

    public LowStockDTO(String productName, String campus, int stock) {
        this.productName = productName;
        this.campus = campus;
        this.stock = stock;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}