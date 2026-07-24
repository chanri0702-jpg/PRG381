/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.bc.cleaninginventory.model.entity;
import java.sql.Timestamp;
/**
 *
 * @author chanr
 */
public class StockRequest {
   private int id;
   private String employeeNumber;
   private String employeeName;
   private String material;
   private int quantity;
   private String reason;
   private String status;
   private Timestamp requestDate;

   public StockRequest() {
   }

   public StockRequest(String employeeNumber, String employeeName, String material, int quantity, String reason) {
      this.employeeNumber = employeeNumber;
      this.employeeName = employeeName;
      this.material = material;
      this.quantity = quantity;
      this.reason = reason;
      this.status = "PENDING";
   }

   public int getId() {
      return this.id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getEmployeeNumber() {
      return this.employeeNumber;
   }

   public void setEmployeeNumber(String employeeNumber) {
      this.employeeNumber = employeeNumber;
   }

   public String getEmployeeName() {
      return this.employeeName;
   }

   public void setEmployeeName(String employeeName) {
      this.employeeName = employeeName;
   }

   public String getMaterial() {
      return this.material;
   }

   public void setMaterial(String material) {
      this.material = material;
   }

   public int getQuantity() {
      return this.quantity;
   }

   public void setQuantity(int quantity) {
      this.quantity = quantity;
   }

   public String getReason() {
      return this.reason;
   }

   public void setReason(String reason) {
      this.reason = reason;
   }

   public String getStatus() {
      return this.status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public Timestamp getRequestDate() {
      return this.requestDate;
   }

   public void setRequestDate(Timestamp requestDate) {
      this.requestDate = requestDate;
   }
}