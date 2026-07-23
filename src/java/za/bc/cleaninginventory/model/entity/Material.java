package za.bc.cleaninginventory.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Material{
    private Long prodId; //product ID (prod_id)
    private Long busId; //supplier business ID
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity; //from product_stock table
    private Integer campId; //campus ID for stock location
    private String supplierName; //For display purposes (from supplier_businesses)
    private String campusName; //For display purposes (from campuses)
    private Boolean isActive; //For soft delete (we'll add a column if needed)

    //Default constructor used
    public Material(){
        this.isActive = true;
    }

    //Parameterized constructor
    public Material(Long prodId, Long busId, String name, String description, 
                    BigDecimal price, Integer stockQuantity, Integer campId) {
        this.prodId = prodId;
        this.busId = busId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.campId = campId;
        this.isActive = true;
    }

    //Getters and Setters
    public Long getProdId(){return prodId;}
    public void setProdId(Long prodId){this.prodId = prodId;}

    public Long getBusId(){return busId;}
    public void setBusId(Long busId){this.busId = busId;}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public BigDecimal getPrice(){return price;}
    public void setPrice(BigDecimal price){this.price = price;}

    public Integer getStockQuantity(){return stockQuantity;}
    public void setStockQuantity(Integer stockQuantity){this.stockQuantity = stockQuantity;}

    public Integer getCampId(){return campId;}
    public void setCampId(Integer campId){this.campId = campId;}

    public String getSupplierName(){return supplierName;}
    public void setSupplierName(String supplierName){this.supplierName = supplierName;}

    public String getCampusName(){return campusName;}
    public void setCampusName(String campusName){this.campusName = campusName;}

    public Boolean getIsActive(){return isActive;}
    public void setIsActive(Boolean isActive){this.isActive = isActive;}

    //Operation methods
    public boolean isLowStock(){
        //Defining the low stock threshold (e.g., 10 units)
        return this.stockQuantity != null && this.stockQuantity <= 10;
    }

    public boolean hasStock(int requestedQuantity){
        return this.stockQuantity != null && this.stockQuantity >= requestedQuantity;
    }

    public void deductStock(int quantity){
        if (this.stockQuantity != null && this.stockQuantity >= quantity) {
            this.stockQuantity -= quantity;
        }else{
            throw new IllegalArgumentException("Insufficient stock available");
        }
    }

    public void addStock(int quantity){
        if (quantity > 0 && this.stockQuantity != null){
            this.stockQuantity += quantity;
        }else{
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    @Override
    public String toString(){
        return "Material{" +
                "prodId=" + prodId +
                ", name='" + name + '\'' +
                ", stockQuantity=" + stockQuantity +
                '}';
    }
}