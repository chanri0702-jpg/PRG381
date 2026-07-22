<%@ page import="java.util.List" %>
<%@ page import="za.bc.cleaninginventory.model.dto.ReportDTO" %>

<thead>

<tr>

    <th>Employee</th>

    <th>Product</th>

    <th>Quantity</th>

    <th>Status</th>

    <th>Priority</th>

    <th>Date</th>

</tr>

</thead>

<tbody>

<%

    List<ReportDTO> requests =
            (List<ReportDTO>) request.getAttribute("requests");

    if (requests != null && !requests.isEmpty()) {

        for (ReportDTO report : requests) {

            String statusBadge;
            String priorityBadge;

            switch (report.getStatus()) {

                case "PENDING":
                    statusBadge = "badge badge-warning";
                    break;

                case "APPROVED":
                    statusBadge = "badge badge-info";
                    break;

                case "ISSUED":
                    statusBadge = "badge badge-success";
                    break;

                default:
                    statusBadge = "badge badge-danger";
                    break;

            }

            switch (report.getPriority()) {

                case "URGENT":
                    priorityBadge = "badge badge-danger";
                    break;

                case "HIGH":
                    priorityBadge = "badge badge-warning";
                    break;

                case "NORMAL":
                    priorityBadge = "badge badge-info";
                    break;

                default:
                    priorityBadge = "badge badge-success";
                    break;

            }

%>

<tr>

    <td><%= report.getEmployee() %></td>

    <td><%= report.getProductName() %></td>

    <td><%= report.getQuantity() %></td>

    <td>

        <span class="<%= statusBadge %>">

            <%= report.getStatus() %>

        </span>

    </td>

    <td>

        <span class="<%= priorityBadge %>">

            <%= report.getPriority() %>

        </span>

    </td>

    <td><%= report.getRequestDate() %></td>

</tr>

<%

        }

    } else {

%>

<tr>

    <td colspan="6" class="text-center">

        No requests found.

    </td>

</tr>

<%

    }

%>

</tbody>