<%@ page import="java.util.List" %>
<%@ page import="za.bc.cleaninginventory.model.dto.ReportDTO" %>

<thead>

<tr>

    <th>Product</th>

    <th>Campus</th>

    <th>Stock</th>

    <th>Status</th>

</tr>

</thead>

<tbody>

<%

    List<ReportDTO> stock =
            (List<ReportDTO>) request.getAttribute("stock");

    if (stock != null && !stock.isEmpty()) {

        for (ReportDTO item : stock) {

            String badgeClass;
            String status;

            if (item.getStock() <= 2) {

                badgeClass = "badge badge-danger";
                status = "Critical";

            } else if (item.getStock() <= 10) {

                badgeClass = "badge badge-warning";
                status = "Low Stock";

            } else {

                badgeClass = "badge badge-success";
                status = "Available";

            }

%>

<tr>

    <td><%= item.getProductName() %></td>

    <td><%= item.getCampus() %></td>

    <td><%= item.getStock() %></td>

    <td>

        <span class="<%= badgeClass %>">

            <%= status %>

        </span>

    </td>

</tr>

<%

        }

    } else {

%>

<tr>

    <td colspan="4" class="text-center">

        No stock records found.

    </td>

</tr>

<%

    }

%>

</tbody>