<%@ page import="java.util.List" %>
<%@ page import="za.bc.cleaninginventory.model.dto.ReportDTO" %>

<thead>

    <tr>

        <th>Product</th>

        <th>Supplier</th>

        <th>Price</th>

        <th>Stock</th>

        <th>Status</th>

    </tr>

</thead>

<tbody>

    <%

        List<ReportDTO> products =
                (List<ReportDTO>) request.getAttribute("products");

        if (products != null && !products.isEmpty()) {

            for (ReportDTO product : products) {

                String badgeClass;
                String status;

                if (product.getStock() <= 2) {

                    badgeClass = "badge badge-danger";
                    status = "Critical";

                } else if (product.getStock() <= 10) {

                    badgeClass = "badge badge-warning";
                    status = "Low Stock";

                } else {

                    badgeClass = "badge badge-success";
                    status = "Available";

                }

    %>

    <tr>

        <td><%= product.getProductName() %></td>

        <td><%= product.getSupplier() %></td>

        <td>R <%= String.format("%.2f", product.getPrice()) %></td>

        <td><%= product.getStock() %></td>

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

        <td colspan="5" class="text-center">

            No products found.

        </td>

    </tr>

    <%

        }

    %>

</tbody>