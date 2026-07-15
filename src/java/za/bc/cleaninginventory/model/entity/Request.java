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
public class Request {
    private String id;
    private String empID;
    private String prodID;
    private int quantity;
    private String status;
    private String priority;
    private String description;
    private Date reqDate;

    public Request(String empID, String prodID, int quantity, String status, String priority, String description) {
        this.empID = empID;
        this.prodID = prodID;
        this.quantity = quantity;
        this.status = status;
        this.priority = priority;
        this.description = description;
    }

    public Request(String id, String empID, String prodID, int quantity, String status, String priority, String description, Date reqDate) {
        this.id = id;
        this.empID = empID;
        this.prodID = prodID;
        this.quantity = quantity;
        this.status = status;
        this.priority = priority;
        this.description = description;
        this.reqDate = reqDate;
    }
    
    public Request(){}

    public String getId() {
        return id;
    }

    public String getEmpID() {
        return empID;
    }

    public String getProdID() {
        return prodID;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }

    public Date getReqDate() {
        return reqDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmpID(String empID) {
        this.empID = empID;
    }

    public void setProdID(String prodID) {
        this.prodID = prodID;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReqDate(Date reqDate) {
        this.reqDate = reqDate;
    }
    
    
    
    
}
