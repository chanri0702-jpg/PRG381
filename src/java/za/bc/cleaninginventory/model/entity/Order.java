/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.bc.cleaninginventory.model.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
/**
 *
 * @author chanr
 */
public class Order {
    private int id;
    private int empID;
    private Date orderDate;
    private List<OrderItem> items = new ArrayList<>();
    private List<Integer> reqIds = new ArrayList<>();
    private List<String> status = new ArrayList<>();

    public Order() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEmpID() { return empID; }
    public void setEmpID(int empID) { this.empID = empID; }
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public void addItem(OrderItem item) { this.items.add(item); }

    public List<Integer> getReqIds() { return reqIds; }
    public void setReqIds(List<Integer> reqIds) { this.reqIds = reqIds; }
    public List<String> getStatus() { return status; }
    public void setStatus(List<String> status) { this.status = status; }

    public BigDecimal getGrandTotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (OrderItem item : items) {
            if (item.getTotal() != null) sum = sum.add(item.getTotal());
        }
        return sum;
    }

    public static class OrderItem {
        private int prodId;
        private String name;
        private int quantity;
        private BigDecimal total;

        public OrderItem() {}
        public OrderItem(int prodId, String name, int quantity, BigDecimal total) {
            this.prodId = prodId;
            this.name = name;
            this.quantity = quantity;
            this.total = total;
        }

        public int getProdId() { return prodId; }
        public void setProdId(int prodId) { this.prodId = prodId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }
    }
}
