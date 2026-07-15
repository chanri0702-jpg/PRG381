/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.bc.cleaninginventory.model.entity;

import java.util.Date;
/**
 *
 * @author chanr
 */
public class Order {
    private String orderID;
    private String empID;
    private Date orderDate;
    
    public Order(){}
    
    public Order(String id, String emp, Date dt){
        this.orderID = id;
        this.empID = emp;
        this.orderDate = dt;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getEmpID() {
        return empID;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public void setEmpID(String empID) {
        this.empID = empID;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    

}
