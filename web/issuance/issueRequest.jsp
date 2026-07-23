<%-- 
    Document   : issueRequest
    Created on : 22 Jul 2026, 16:59:57
    Author     : chanr
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="za.bc.cleaninginventory.model.entity.Product" %>
<%@ page import="za.bc.cleaninginventory.model.entity.Request" %>

<%
    request.setAttribute("pageTitle", "Stock Requests");
    request.setAttribute("activePage","request");
%>
<%
    List<Product> products = (List<Product>) request.getAttribute("products");
    List<Request> myRequests = (List<Request>) request.getAttribute("myRequests");
    Request editingRequest = (Request) request.getAttribute("editingRequest");
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");

    boolean isEditing = (editingRequest != null);
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
     
     <link rel="stylesheet" href="${pageContext.request.contextPath}/css/issuance.css">
     <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">

</head>

<body>

    <div class="parent-container">

        <%@ include file="../components/sidebar.jsp" %>

        <main class="main-content">
            
            <section class="stock-cards">

       
                <a href="${pageContext.request.contextPath}/issuance" class="<%= "issuance".equals(request.getAttribute("activePage")) ? "active" : "" %>">
                    
                    <span>Stock Issuance</span>
                </a>
                <a href="${pageContext.request.contextPath}/request" class="<%= "request".equals(request.getAttribute("activePage")) ? "active" : "" %>">
                 
                    <span>Stock Requests</span>
                </a>
           
                <a href="${pageContext.request.contextPath}/orders" class="<%= "orders".equals(request.getAttribute("activePage")) ? "active" : "" %>">
                  
                    <span>Stock Orders</span>
                </a>

    </section>


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
        Request stock from supervisor.
    </p>
    <div class="card mb-4">
        <div class="card-header bg-white text-dark">
            <h4><%= isEditing ? "Edit Request #" + editingRequest.getId() : "New Stock Request" %> </h4>
            
        </div>
        <div class="card-body">
            <form method="post" action="request" class="row g-3">
                <input type="hidden" name="action" value="<%= isEditing ? "update" : "create" %>">
                <% if (isEditing) { %>
                    <input type="hidden" name="reqId" value="<%= editingRequest.getId() %>">
                <% } %>

                <div class="col-md-6">
                    <label class="form-label">Product</label>
                    <select name="prodId" class="form-select" required>
                        <option value="" disabled <%= !isEditing ? "selected" : "" %>>Select a product...</option>
                        <%
                            if (products != null) {
                                for (Product p : products) {
                                    boolean selected = isEditing && editingRequest.getProdID() == p.getId();
                        %>
                            <option value="<%= p.getId() %>" <%= selected ? "selected" : "" %>>
                                <%= p.getName() %> (R<%= p.isPrice() %>)
                            </option>
                        <%
                                }
                            }
                        %>
                    </select>
                </div>

                <div class="col-md-3">
                    <label class="form-label">Quantity</label>
                    <input type="number" name="quantity" class="form-control" min="1"
                           value="<%= isEditing ? editingRequest.getQuantity() : 1 %>" required>
                </div>

                <div class="col-md-3">
                    <label class="form-label">Priority</label>
                    <select name="priority" class="form-select" required>
                        <%
                            String[] priorities = {"LOW", "NORMAL", "HIGH", "URGENT"};
                            for (String p : priorities) {
                                boolean selected = isEditing
                                        ? p.equals(editingRequest.getPriority())
                                        : p.equals("NORMAL");
                        %>
                            <option value="<%= p %>" <%= selected ? "selected" : "" %>><%= p %></option>
                        <%
                            }
                        %>
                    </select>
                </div>

                <div class="col-12">
                    <label class="form-label">Description</label>
                    <textarea name="description" class="form-control" rows="2" required><%=
                        isEditing ? editingRequest.getDescription() : ""
                    %></textarea>
                </div>

                <div class="col-12">
                    <button type="submit" class="btn btn-primary">
                        <%= isEditing ? "Save Changes" : "Submit Request" %>
                    </button>
                    <% if (isEditing) { %>
                        <a href="issue-request" class="btn btn-outline-secondary">Cancel</a>
                    <% } %>
                </div>
            </form>
        </div>
    </div>

    <div class="card">
        <div class="card-header bg-white text-dark">
            <h4>My Requests </h4>
            
        </div>
        <div class="card-body p-0">
            <table class="table table-hover mb-0 align-middle">
                <thead class="">
                    <tr>
                        <th>Req #</th>
                        <th>Product</th>
                        <th>Quantity</th>
                        <th>Priority</th>
                        <th>Status</th>
                        <th>Description</th>
                        <th>Date</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                <%
                    if (myRequests == null || myRequests.isEmpty()) {
                %>
                    <tr><td colspan="8" class="text-center text-muted py-3">You haven't submitted any requests yet.</td></tr>
                <%
                    } else {
                        for (Request r : myRequests) {
                            boolean canModify = "PENDING".equals(r.getStatus());
                %>
                    <tr>
                        <td>#<%= r.getId() %></td>
                        <td><%= r.getName() %></td>
                        <td><%= r.getQuantity() %></td>
                        <td>
                            <span class="badge
                                <%= "URGENT".equals(r.getPriority()) ? "bg-danger" :
                                    "HIGH".equals(r.getPriority()) ? "bg-warning text-dark" :
                                    "NORMAL".equals(r.getPriority()) ? "bg-info text-dark" : "bg-secondary" %>">
                                <%= r.getPriority() %>
                            </span>
                        </td>
                        <td>
                            <span class="badge
                                <%= "APPROVED".equals(r.getStatus()) ? "bg-success" :
                                    "REJECTED".equals(r.getStatus()) ? "bg-danger" :
                                    "ISSUED".equals(r.getStatus()) ? "bg-primary" : "bg-secondary" %>">
                                <%= r.getStatus() %>
                            </span>
                        </td>
                        <td><%= r.getDescription() %></td>
                        <td><%= r.getReqDate() %></td>
                        <td class="text-nowrap">
                            <% if (canModify) { %>
                                <a href="request?editReqId=<%= r.getId() %>" class="btn btn-sm btn-outline-primary">Edit</a>
                                <form method="post" action="request" class="d-inline"
                                      onsubmit="return confirm('Delete this request?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="reqId" value="<%= r.getId() %>">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">Delete</button>
                                </form>
                            <% } else { %>
                                <span class="text-muted">&mdash;</span>
                            <% } %>
                        </td>
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
        </main>
                </div>
</body>
</html>
