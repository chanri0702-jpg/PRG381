/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.bc.cleaninginventory.model.entity;
import java.math.BigDecimal;
/**
 *
 * @author chanr
 */
public class Product {
    private int id;
    private int bussID;
    private String name;
    private String description;
    private BigDecimal price;
    
    public Product(){}

    public Product(int id, int bussID, String name, String description, BigDecimal price) {
        this.id = id;
        this.bussID = bussID;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Product(int bussID, String name, String description, BigDecimal price) {
        this.bussID = bussID;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBussID(int bussID) {
        this.bussID = bussID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal isPrice() {
        return price;
    }
    
    

    public int getId() {
        return id;
    }

    public int getBussID() {
        return bussID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    
}
