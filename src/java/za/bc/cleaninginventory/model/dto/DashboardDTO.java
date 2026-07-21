package za.bc.cleaninginventory.model.dto;

import java.util.List;

public class DashboardDTO {

    private int totalProducts;
    private int totalEmployees;
    private int totalSuppliers;
    private int pendingRequests;
    private int lowStockProducts;

    private List<LowStockDTO> lowStockList;
    private List<RecentRequestDTO> recentRequests;

    public DashboardDTO() {
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public int getTotalSuppliers() {
        return totalSuppliers;
    }

    public void setTotalSuppliers(int totalSuppliers) {
        this.totalSuppliers = totalSuppliers;
    }

    public int getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(int pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    public int getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(int lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public List<LowStockDTO> getLowStockList() {
        return lowStockList;
    }

    public void setLowStockList(List<LowStockDTO> lowStockList) {
        this.lowStockList = lowStockList;
    }

    public List<RecentRequestDTO> getRecentRequests() {
        return recentRequests;
    }

    public void setRecentRequests(List<RecentRequestDTO> recentRequests) {
        this.recentRequests = recentRequests;
    }

}