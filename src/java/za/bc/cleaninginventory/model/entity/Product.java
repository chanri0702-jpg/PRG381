/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.bc.cleaninginventory.model.entity;
/**
 *
 * @author chanr
 */
public class Product {
    private String id;
    private String bussID;
    private String name;
    private String description;
    private float price;
    
    public Product(){}

    public Product(String id, String bussID, String name, String description, float price) {
        this.id = id;
        this.bussID = bussID;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Product(String bussID, String name, String description, float price) {
        this.bussID = bussID;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBussID(String bussID) {
        this.bussID = bussID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPrice() {
        return price;
    }
    
    

    public String getId() {
        return id;
    }

    public String getBussID() {
        return bussID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(float price) {
        this.price = price;
    }
    
    
}
