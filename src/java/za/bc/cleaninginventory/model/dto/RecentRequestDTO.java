package za.bc.cleaninginventory.model.dto;

public class RecentRequestDTO {

    private String employee;
    private String product;
    private int quantity;
    private String status;
    private int requestId;
    private String priority;

    public RecentRequestDTO() {
    }
    
    public RecentRequestDTO(int requestId, String employee, String product, int quantity, String status) {
        this.requestId = requestId;
        this.employee = employee;
        this.product = product;
        this.quantity = quantity;
        this.status = status;
    }
    
    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRequestId() {
        return requestId;
    }

    public String getEmployee() {
        return employee;
    }

    public String getProduct() {
        return product;
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

    public void setPriority(String priority) {
        this.priority = priority;
    }

    
}