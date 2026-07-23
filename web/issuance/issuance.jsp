<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="za.bc.cleaninginventory.model.entity.Cleaner" %>
<%@ page import="za.bc.cleaninginventory.model.entity.Issuance" %>
<%@ page import="za.bc.cleaninginventory.model.dto.ProductStockDTO" %>
<%
    List<Cleaner> cleaners = (List<Cleaner>) request.getAttribute("cleaners");
    List<ProductStockDTO> availableStock = (List<ProductStockDTO>) request.getAttribute("availableStock");
    List<Issuance> issuanceHistory = (List<Issuance>) request.getAttribute("issuanceHistory");
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Issue Stock - Cleaning Inventory System</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/app.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container py-4">

    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0">Issue Stock to Cleaners</h2>
        <a href="dashboard" class="btn btn-outline-secondary btn-sm">&larr; Back to Dashboard</a>
    </div>

    <% if (successMessage != null) { %>
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <%= successMessage %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <% } %>
    <% if (errorMessage != null) { %>
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <%= errorMessage %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <% } %>

    <p class="text-muted small">
        Issuing stock deducts directly from your campus's inventory — it isn't tied to any request.
    </p>

    <!-- Issue Stock Form -->
    <div class="card mb-4">
        <div class="card-header bg-dark text-white">
            Issue Stock
        </div>
        <div class="card-body">
            <%
                if (availableStock == null || availableStock.isEmpty()) {
            %>
                <p class="text-muted mb-0">No stock currently available at your campus.</p>
            <%
                } else {
            %>
            <form method="post" action="issuance" class="row g-3" id="issueForm">
                <div class="col-md-4">
                    <label class="form-label">Product</label>
                    <select name="prodId" id="prodSelect" class="form-select" required>
                        <option value="" disabled selected>Select a product...</option>
                        <%
                            for (ProductStockDTO p : availableStock) {
                        %>
                            <option value="<%= p.getProdId() %>" data-stock="<%= p.getStock() %>">
                                <%= p.getName() %> (<%= p.getStock() %> in stock)
                            </option>
                        <%
                            }
                        %>
                    </select>
                </div>

                <div class="col-md-4">
                    <label class="form-label">Cleaner</label>
                    <select name="cleanerId" class="form-select col-wide" required>
                        <option value="" disabled selected>Select a cleaner...</option>
                        <%
                            if (cleaners != null) {
                                for (Cleaner c : cleaners) {
                        %>
                            <option value="<%= c.getCleanerId() %>"><%= c.getFullName() %></option>
                        <%
                                }
                            }
                        %>
                    </select>
                </div>

                <div class="col-md-2">
                    <label class="form-label">Quantity</label>
                    <input type="number" name="quantity" id="quantityInput" class="form-control" min="1" value="1" required>
                    <div class="form-text" id="stockHint">&nbsp;</div>
                </div>

                <div class="col-md-2 d-flex align-items-end">
                    <button type="submit" class="btn btn-success w-100">Issue Stock</button>
                </div>
            </form>
            <%
                }
            %>
        </div>
    </div>

    <!-- Issuance History -->
    <div class="card">
        <div class="card-header bg-dark text-white">
            Issuance History
        </div>
        <div class="card-body p-0">
            <table class="table table-striped mb-0 align-middle">
                <thead class="table-light">
                    <tr>
                        <th>#</th>
                        <th>Cleaner</th>
                        <th>Product</th>
                        <th>Quantity</th>
                        <th>Issued By</th>
                        <th>Date</th>
                    </tr>
                </thead>
                <tbody>
                <%
                    if (issuanceHistory == null || issuanceHistory.isEmpty()) {
                %>
                    <tr><td colspan="6" class="text-center text-muted py-3">No issuance records yet.</td></tr>
                <%
                    } else {
                        for (Issuance i : issuanceHistory) {
                %>
                    <tr>
                        <td><%= i.getIssuanceId() %></td>
                        <td><%= i.getCleanerName() %></td>
                        <td><%= i.getProductName() %></td>
                        <td><%= i.getQuantity() %></td>
                        <td><%= i.getIssuedByName() %></td>
                        <td><%= i.getIssueDate() %></td>
                    </tr>
                <%
                        }
                    }
                %>
                </tbody>
            </table>
        </div>
    </div>

</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    var prodSelect = document.getElementById('prodSelect');
    var quantityInput = document.getElementById('quantityInput');
    var stockHint = document.getElementById('stockHint');

    if (prodSelect) {
        prodSelect.addEventListener('change', function () {
            var selected = this.options[this.selectedIndex];
            var stock = parseInt(selected.dataset.stock, 10) || 0;
            quantityInput.max = stock;
            stockHint.textContent = stock + ' available';
        });
    }
</script>
</body>
</html>