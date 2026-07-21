/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.bc.cleaninginventory.model.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
/**
 *
 * @author chanr
 */
public class Order {
    private int Id;
    private int empID;
    private Date orderDate;
    
    //if joined with products (for webpage display)
    private int prodID;
    private String name; 
    private int quantity;
    private BigDecimal total;
    
    private List<Integer> reqIds;
    private List<String> status;
    
    public Order(){}
    
    public int getId() {
        return Id;
    }

    public void setId(int ordId) {
        this.Id = ordId;
    }

    public int getEmpID() {
        return empID;
    }

    public void setEmpID(int empId) {
        this.empID = empId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date ordDate) {
        this.orderDate = ordDate;
    }

    public int getProdID() {
        return prodID;
    }

    public void setProdID(int prodId) {
        this.prodID = prodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String productName) {
        this.name = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<Integer> getReqIds() {
        return reqIds;
    }

    public List<String> getStatus() {
        return status;
    }

    public void setReqIds(List<Integer> reqIds) {
        this.reqIds = reqIds;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }
    
}
