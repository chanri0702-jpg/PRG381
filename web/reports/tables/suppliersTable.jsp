<%@ page import="java.util.List" %>
<%@ page import="za.bc.cleaninginventory.model.dto.ReportDTO" %>

<thead>

<tr>

    <th>Supplier</th>

    <th>Description</th>

    <th>City</th>
    
    <th>Province</th>

</tr>

</thead>

<tbody>

<%

    List<ReportDTO> suppliers =
            (List<ReportDTO>) request.getAttribute("suppliers");

    if (suppliers != null && !suppliers.isEmpty()) {

        for (ReportDTO supplier : suppliers) {

%>

<tr>

    <td><%= supplier.getSupplier() %></td>

    <td><%= supplier.getSupplierDescription()%></td>

    <td><%= supplier.getCity() %></td>
    
    <td><%= supplier.getProvince() %></td>

</tr>

<%

        }

    } else {

%>

<tr>

    <td colspan="3" class="text-center">

        No suppliers found.

    </td>

</tr>

<%

    }

%>

</tbody>