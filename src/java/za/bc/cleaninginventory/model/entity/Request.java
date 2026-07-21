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
    private int Id;
    private int empID;
    private int prodID;
    private String name; 
    private String requesterName;
    private int quantity;
    private String status;
    private String priority;
    private String description;
    private Date reqDate;

    public Request(int empId, int prodId, int quantity, String priority, String description) {
        this.empID = empId;
        this.prodID = prodId;
        this.quantity = quantity;
        this.priority = priority;
        this.description = description;
    }
    
    public Request(){}

    public int getId() {
        return Id;
    }

    public void setId(int reqId) {
        this.Id = reqId;
    }

    public int getEmpID() {
        return empID;
    }

    public void setEmpID(int empId) {
        this.empID = empId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getReqDate() {
        return reqDate;
    }

    public void setReqDate(Date reqDate) {
        this.reqDate = reqDate;
    }
    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }
}
