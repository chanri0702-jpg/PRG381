<%@ page import="java.util.List" %>
<%@ page import="za.bc.cleaninginventory.model.dto.ReportDTO" %>

<thead>

<tr>

    <th>Order ID</th>

    <th>Date</th>

    <th>Product</th>

    <th>Quantity</th>

    <th>Total</th>

</tr>

</thead>

<tbody>

<%

    List<ReportDTO> orders =
            (List<ReportDTO>) request.getAttribute("orders");

    if (orders != null && !orders.isEmpty()) {

        for (ReportDTO order : orders) {

%>

<tr>

    <td>#<%= order.getOrderId() %></td>

    <td><%= order.getRequestDate() %></td>

    <td><%= order.getProductName() %></td>

    <td><%= order.getQuantity() %></td>

    <td>R <%= String.format("%.2f", order.getTotal()) %></td>

</tr>

<%

        }

    } else {

%>

<tr>

    <td colspan="5" class="text-center">

        No orders found.

    </td>

</tr>

<%

    }

%>

</tbody>