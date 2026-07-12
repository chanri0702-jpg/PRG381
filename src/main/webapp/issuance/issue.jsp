<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String employeeNumber = (String) session.getAttribute("employeeNumber");
    String employeeName = (String) session.getAttribute("employeeName");

    if (employeeNumber == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Issue Stock Request</title>
    <link rel="stylesheet" href=",,/css/style.css">
</head>
<body>
<div class="orderFrom">
    <h1>Order Cleaning Materials</h1>

    <% if (request.getAttribute("successMessage") != null) { %>
    <div class="message success"><%= request.getAttribute("successMessage") %></div>
    <% } %>
    <% if (request.getAttribute("errorMessage") != null) { %>
    <div class="message error"><%= request.getAttribute("errorMessage") %></div>
    <% } %>

    <!-- This form will POST to StockIssuanceServlet once the Servlet/Service/DAO layers are wired up -->
    <form action="order" method="post">
        <input type="hidden" name="action" value="add">

        <label>Employee Number</label>
        <input type="text" value="<%= employeeNumber %>" readonly>

        <label>Employee Name</label>
        <input type="text" value="<%= employeeName %>" readonly>

        <label for="material">Material</label>
        <input type="text" id="material" name="material" placeholder="e.g. Floor Cleaner" required>

        <label for="quantity">Quantity</label>
        <input type="number" id="quantity" name="quantity" min="1" required>

        <label for="reason">Reason for Request</label>
        <textarea id="reason" name="reason" rows="3" required></textarea>

        <button type="submit">Submit Request</button>
    </form>
</div>
</body>
</html>