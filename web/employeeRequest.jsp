<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="cleaninginventory.model.entity.Product" %>
<%@ page import="cleaninginventory.model.entity.Request" %>

<%
    String employeeNumber = (String) session.getAttribute("employeeNumber");

    if (employeeNumber == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    List<Product> products = (List<Product>) request.getAttribute("products");
    List<Request> myRequests = (List<Request>) request.getAttribute("myRequests");

    Request editingRequest = (Request) request.getAttribute("editingRequest");
    boolean isEditMode = editingRequest != null;
%>


<!DOCTYPE html>
<html>
<head>
    <title>Issue Stock Request</title>
    <link rel="stylesheet" href=",,/css/style.css">
</head>
<body>

<div class="orderFrom">
    <h1><%= isEditMode ? "Edit Request" : "Order Cleaning Materials" %></h1>

    <% if (request.getAttribute("successMessage") != null) { %>
        <div class="message success"><%= request.getAttribute("successMessage") %></div>
    <% } %>
    <% if (request.getAttribute("errorMessage") != null) { %>
        <div class="message error"><%= request.getAttribute("errorMessage") %></div>
    <% } %>

    <form action="issue-request" method="post">
        <input type="hidden" name="action" value="<%= isEditMode ? "update" : "create" %>">
        <% if (isEditMode) { %>
            <input type="hidden" name="reqId" value="<%= editingRequest.getReqId() %>">
        <% } %>

        <label for="empId">Employee Number</label>
        <input type="text" id="empId" value="<%= employeeNumber %>" readonly>
        <input type="hidden" name="employeeNumber" value="<%= employeeNumber %>">

        <label for="prodId">Product</label>
        <select id="prodId" name="prodId" required>
            <option value="" disabled <%= !isEditMode ? "selected" : "" %>>-- Select a product --</option>
            <%
                if (products != null) {
                    for (Product p : products) {
                        boolean selected = isEditMode && editingRequest.getProdId() == p.getProdId();
            %>
                <option value="<%= p.getProdId() %>" <%= selected ? "selected" : "" %>><%= p.getName() %></option>
            <%
                    }
                }
            %>
        </select>

        <label for="quantity">Quantity</label>
        <input type="number" id="quantity" name="quantity" min="1" required
               value="<%= isEditMode ? editingRequest.getQuantity() : "" %>">

        <label for="priority">Priority</label>
        <select id="priority" name="priority">
            <% String currentPriority = isEditMode ? editingRequest.getPriority() : "NORMAL"; %>
            <option value="LOW" <%= "LOW".equals(currentPriority) ? "selected" : "" %>>Low</option>
            <option value="NORMAL" <%= "NORMAL".equals(currentPriority) ? "selected" : "" %>>Normal</option>
            <option value="HIGH" <%= "HIGH".equals(currentPriority) ? "selected" : "" %>>High</option>
            <option value="URGENT" <%= "URGENT".equals(currentPriority) ? "selected" : "" %>>Urgent</option>
        </select>

        <label for="description">Reason for Request</label>
        <textarea id="description" name="description" rows="3" required><%= isEditMode ? editingRequest.getDescription() : "" %></textarea>

        <button type="submit"><%= isEditMode ? "Save Changes" : "Submit Request" %></button>
        <% if (isEditMode) { %>
            <a class="cancel-link" href="issue-request">Cancel</a>
        <% } %>
    </form>
</div>

<div class="requestsList">
    <h2>My Requests</h2>
    <table>
        <thead>
            <tr>
                <th>Product</th>
                <th>Qty</th>
                <th>Priority</th>
                <th>Status</th>
                <th>Date</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
        <%
            if (myRequests != null) {
                for (Request r : myRequests) {
        %>
            <tr>
                <td><%= r.getProductName() %></td>
                <td><%= r.getQuantity() %></td>
                <td><%= r.getPriority() %></td>
                <td><span class="status <%= r.getStatus() %>"><%= r.getStatus() %></span></td>
                <td><%= r.getReqDate() %></td>
                <td>
                    <% if ("PENDING".equals(r.getStatus())) { %>
                        <form action="issue-request" method="get" style="display:inline;">
                            <input type="hidden" name="editReqId" value="<%= r.getReqId() %>">
                            <button type="submit" class="action-btn edit-btn">Edit</button>
                        </form>
                        <form action="issue-request" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="reqId" value="<%= r.getReqId() %>">
                            <button type="submit" class="action-btn delete-btn"
                                    onclick="return confirm('Delete this request?');">Delete</button>
                        </form>
                    <% } else { %>
                        &mdash;
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

</body>
</html>