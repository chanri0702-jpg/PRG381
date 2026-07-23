<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="za.bc.cleaninginventory.model.entity.Cleaner" %>
<%@ page import="za.bc.cleaninginventory.model.entity.Issuance" %>
<%@ page import="za.bc.cleaninginventory.model.dto.ProductStockDTO" %>
<%
    request.setAttribute("pageTitle", "Stock Issuance");
    request.setAttribute("activePage","issuance");
%>

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

    <title>Dashboard | Cleaning Inventory & Issuance System</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/cims_logo.png">

    <!-- Google Font -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <!-- Font Awesome -->
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css">

    
     <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
     <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
     <link rel="stylesheet" href="${pageContext.request.contextPath}/css/issuance.css">

</head>

<body>

    <div class="parent-container">

        <%@ include file="../components/sidebar.jsp" %>

        <main class="main-content">

            <%@ include file="../components/topbar.jsp" %>
            
            <div class="dashboard-content">
            
              
<div >

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


    <div class="card mb-4">
        <div class="card-header bg-white text-dark">
            <h4> Issue Stock </h4>
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

    <div class="card">
        <div class="card-header card-header bg-white text-dark">
            <h4> Issuance History </h4>
        </div>
        <div class="card-body p-0">
            <table class="table  mb-0 align-middle">
                <thead>
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
</main>
</body>
</html>