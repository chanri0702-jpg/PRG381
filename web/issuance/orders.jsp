<%-- 
    Document   : orders
    Created on : 22 Jul 2026, 16:58:40
    Author     : chanr
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="za.bc.cleaninginventory.model.entity.Request" %>
<%@ page import="za.bc.cleaninginventory.model.entity.Order" %>
<%@ page import="za.bc.cleaninginventory.model.entity.Product" %>
<%
    List<Request> pendingRequests = (List<Request>) request.getAttribute("pendingRequests");
    List<Order> orderHistory = (List<Order>) request.getAttribute("orderHistory");
    Map<Integer, String> campuses = (Map<Integer, String>) request.getAttribute("campuses");
    Map<String, List<Product>> businessProducts = (Map<String, List<Product>>) request.getAttribute("businessProducts");
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");

    Map<String, List<Request>> byBusiness = new LinkedHashMap<>();
    if (pendingRequests != null) {
        for (Request r : pendingRequests) {
            String bkey = r.getBusinessName() != null ? r.getBusinessName() : "Unknown Supplier";
            byBusiness.computeIfAbsent(bkey, k -> new ArrayList<>()).add(r);
        }
    }

    StringBuilder campusJson = new StringBuilder("[");
    if (campuses != null) {
        boolean firstC = true;
        for (Map.Entry<Integer, String> ce : campuses.entrySet()) {
            if (!firstC) campusJson.append(",");
            campusJson.append("{\"id\":").append(ce.getKey())
                      .append(",\"name\":\"").append(ce.getValue().replace("\"", "\\\"")).append("\"}");
            firstC = false;
        }
    }
    campusJson.append("]");
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

    <!-- CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
     <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

</head>

<body>

    <div class="parent-container">

        <%@ include file="../components/sidebar.jsp" %>

        <main class="main-content">

            <%@ include file="../components/topbar.jsp" %>
            
            <div class="dashboard-content">
            
              
<div class="container py-4">

    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0">Orders</h2>
       
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
        Showing pending requests from employees on your campus only. Check items to include them in an order,
        reject items you don't want to action, or add products manually even if there's no matching request.
        One order can only contain products from a single business, so each business has its own form below.
    </p>

    <script type="application/json" id="campuses-json"><%= campusJson.toString() %></script>

    <%
        int formIndex = 0;
        if (businessProducts != null) {
            for (String businessName : businessProducts.keySet()) {
                List<Request> group = byBusiness.getOrDefault(businessName, new ArrayList<>());
                List<Product> businessProductList = businessProducts.get(businessName);
                String formId = "orderForm" + (formIndex++);
                String bkey = businessName.replaceAll("[^a-zA-Z0-9]", "_");

                StringBuilder productJson = new StringBuilder("[");
                boolean firstP = true;
                if (businessProductList != null) {
                    for (Product p : businessProductList) {
                        if (!firstP) productJson.append(",");
                        BigDecimal pPrice = p.isPrice() != null ? p.isPrice() : BigDecimal.ZERO;
                        productJson.append("{\"id\":").append(p.getId())
                                   .append(",\"name\":\"").append(p.getName().replace("\"", "\\\"")).append("\"")
                                   .append(",\"price\":").append(pPrice)
                                   .append("}");
                        firstP = false;
                    }
                }
                productJson.append("]");
    %>
    <script type="application/json" id="products-<%= bkey %>"><%= productJson.toString() %></script>

    <div class="card mb-4">
        <div class="card-header bg-dark text-white">
            <%= businessName %>
        </div>
        <div class="card-body p-0">
            <form method="post" action="orders" id="<%= formId %>" data-order-form>
                <div class="table-responsive">
                    <table class="table table-hover mb-0 align-middle">
                        <thead class="table-light">
                            <tr>
                                <th class="col-checkbox"></th>
                                <th>Req #</th>
                                <th>Requested By</th>
                                <th>Product</th>
                                <th>Requested Qty</th>
                                <th class="col-qty">Order Qty</th>
                                <th class="col-price">Unit Price (R)</th>
                                <th class="col-campus">Campus</th>
                                <th class="col-total">Line Total</th>
                                <th class="col-actions">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                        <%
                            if (group.isEmpty()) {
                        %>
                            <tr><td colspan="10" class="text-center text-muted py-2">No pending requests for this business from your campus.</td></tr>
                        <%
                            } else {
                                for (Request r : group) {
                                    int rid = r.getId();
                                    String priceStr = r.getPrice() != null ? r.getPrice().toPlainString() : "0.00";
                        %>
                            <tr>
                                <td>
                                    <input type="checkbox" name="selectedReqIds" value="<%= rid %>"
                                           class="form-check-input row-check" data-req="<%= rid %>">
                                </td>
                                <td>#<%= rid %></td>
                                <td><%= r.getRequesterName() != null ? r.getRequesterName() : "Emp #" + r.getEmpID() %></td>
                                <td><%= r.getName() %></td>
                                <td><%= r.getQuantity() %></td>
                                <td>
                                    <input type="hidden" name="prodId_<%= rid %>" value="<%= r.getProdID() %>">
                                    <input type="number" name="quantity_<%= rid %>" class="form-control form-control-sm qty-input"
                                           data-req="<%= rid %>" min="1" value="<%= r.getQuantity() %>">
                                </td>
                                <td>
                                    <input type="number" step="0.01" name="price_<%= rid %>" class="form-control form-control-sm price-input"
                                           data-req="<%= rid %>" min="0" value="<%= priceStr %>">
                                </td>
                                <td class="text-muted small">Requester's campus</td>
                                <td class="line-total" data-req="<%= rid %>">R0.00</td>
                                <td>
                                    <button type="submit" form="reject-<%= rid %>" class="btn btn-sm btn-outline-danger"
                                            onclick="return confirm('Reject request #<%= rid %>?');">Reject</button>
                                </td>
                            </tr>
                        <%
                                }
                            }
                        %>
                        </tbody>
                    </table>
                </div>
                <div class="p-3 border-top">
                    <button type="button" class="btn btn-outline-secondary btn-sm"
                            onclick="addManualRow('<%= formId %>', '<%= bkey %>')">+ Add Product Manually</button>
                    <button type="submit" class="btn btn-primary btn-sm ms-2">Place Order for <%= businessName %></button>
                    <span class="float-end fw-bold">Order Total: <span class="form-total">R0.00</span></span>
                </div>
            </form>

            <%
                for (Request r : group) {
                    int rid = r.getId();
            %>
            <form id="reject-<%= rid %>" method="post" action="orders" class="d-none">
                <input type="hidden" name="action" value="reject">
                <input type="hidden" name="reqId" value="<%= rid %>">
            </form>
            <%
                }
            %>
        </div>
    </div>
    <%
            }
        }
    %>

    <!-- Order History, grouped per order -->
    <div class="card">
        <div class="card-header bg-dark text-white">
            Order History
        </div>
        <div class="card-body">
            <%
                if (orderHistory == null || orderHistory.isEmpty()) {
            %>
                <p class="text-muted text-center py-3 mb-0">No orders placed yet.</p>
            <%
                } else {
                    for (Order o : orderHistory) {
            %>
            <div class="order-history-item">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <div>
                        <strong>Order #<%= o.getId() %></strong>
                        <span class="text-muted small">&mdash; <%= o.getOrderDate() %></span>
                    </div>
                    <div>
                        <%
                            if (o.getStatus() != null) {
                                for (String s : o.getStatus()) {
                        %>
                            <span class="badge bg-secondary"><%= s %></span>
                        <%
                                }
                            }
                        %>
                    </div>
                </div>
                <table class="table table-sm mb-2">
                    <thead class="table-light">
                        <tr><th>Product</th><th>Quantity</th><th>Line Total</th></tr>
                    </thead>
                    <tbody>
                    <%
                        for (Order.OrderItem item : o.getItems()) {
                    %>
                        <tr>
                            <td><%= item.getName() %></td>
                            <td><%= item.getQuantity() %></td>
                            <td>R<%= item.getTotal() %></td>
                        </tr>
                    <%
                        }
                    %>
                    </tbody>
                </table>
                <div class="text-end fw-bold">Order Total: R<%= o.getGrandTotal() %></div>
            </div>
            <%
                    }
                }
            %>
        </div>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function getJSON(id) {
        var el = document.getElementById(id);
        return el ? JSON.parse(el.textContent) : [];
    }

    function recalcForm(form) {
        var total = 0;

        form.querySelectorAll('.row-check').forEach(function (cb) {
            var reqId = cb.dataset.req;
            var qtyInput = form.querySelector('.qty-input[data-req="' + reqId + '"]');
            var priceInput = form.querySelector('.price-input[data-req="' + reqId + '"]');
            var lineCell = form.querySelector('.line-total[data-req="' + reqId + '"]');

            var qty = parseFloat(qtyInput.value) || 0;
            var price = parseFloat(priceInput.value) || 0;
            var lineTotal = qty * price;

            lineCell.textContent = 'R' + lineTotal.toFixed(2);
            if (cb.checked) total += lineTotal;
        });

        form.querySelectorAll('.manual-row').forEach(function (row) {
            var qty = parseFloat(row.querySelector('.manual-qty').value) || 0;
            var price = parseFloat(row.querySelector('.manual-price').value) || 0;
            var lineTotal = qty * price;
            row.querySelector('.line-total').textContent = 'R' + lineTotal.toFixed(2);
            total += lineTotal;
        });

        form.querySelector('.form-total').textContent = 'R' + total.toFixed(2);
    }

    function addManualRow(formId, businessKey) {
        var form = document.getElementById(formId);
        var tbody = form.querySelector('tbody');
        var products = getJSON('products-' + businessKey);
        var campuses = getJSON('campuses-json');

        if (products.length === 0) return;

        var row = document.createElement('tr');
        row.className = 'manual-row';

        var productOptions = products.map(function (p) {
            return '<option value="' + p.id + '" data-price="' + p.price + '">' + p.name + '</option>';
        }).join('');

        var campusOptions = campuses.map(function (c) {
            return '<option value="' + c.id + '">' + c.name + '</option>';
        }).join('');

        row.innerHTML =
            '<td></td>' +
            '<td colspan="2" class="text-muted small">Manually added</td>' +
            '<td><select name="manualProdId" class="form-select form-select-sm manual-prod-select">' + productOptions + '</select></td>' +
            '<td class="text-muted">&mdash;</td>' +
            '<td><input type="number" name="manualQuantity" class="form-control form-control-sm manual-qty" min="1" value="1"></td>' +
            '<td><input type="number" step="0.01" name="manualPrice" class="form-control form-control-sm manual-price" min="0" value="' + products[0].price + '"></td>' +
            '<td><select name="manualCampId" class="form-select form-select-sm">' + campusOptions + '</select></td>' +
            '<td class="line-total">R0.00</td>' +
            '<td><button type="button" class="btn btn-sm btn-outline-danger remove-manual-row">&times;</button></td>';

        tbody.appendChild(row);

        row.querySelector('.manual-prod-select').addEventListener('change', function () {
            var selected = this.options[this.selectedIndex];
            row.querySelector('.manual-price').value = selected.dataset.price;
            recalcForm(form);
        });
        row.querySelector('.remove-manual-row').addEventListener('click', function () {
            row.remove();
            recalcForm(form);
        });

        recalcForm(form);
    }

    document.querySelectorAll('form[data-order-form]').forEach(function (form) {
        form.addEventListener('input', function () { recalcForm(form); });
        form.addEventListener('change', function () { recalcForm(form); });
        recalcForm(form);

        form.addEventListener('submit', function (e) {
            var anyChecked = Array.from(form.querySelectorAll('.row-check')).some(function (cb) {
                return cb.checked;
            });
            var anyManual = form.querySelectorAll('.manual-row').length > 0;
            if (!anyChecked && !anyManual) {
                e.preventDefault();
                alert('Select at least one request or add a product manually before placing this order.');
            }
        });
    });
</script>
</body>
</html>
