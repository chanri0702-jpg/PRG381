/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.bc.cleaninginventory.model.dto;

/**
 *
 * @author chanr
 */
public class ProductStockDTO {
    private int prodId;
    private String name;
    private int stock;

    public ProductStockDTO() {}
    public ProductStockDTO(int prodId, String name, int stock) {
        this.prodId = prodId;
        this.name = name;
        this.stock = stock;
    }

    public int getProdId() {
        return prodId;
    }

    public void setProdId(int prodId) {
        this.prodId = prodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    
    
}
