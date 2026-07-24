<%@page import="za.bc.cleaninginventory.model.dto.DashboardDTO"%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<%@ page import="java.util.List" %>
<%@ page import="za.bc.cleaninginventory.model.dto.LowStockDTO" %>
<%@ page import="za.bc.cleaninginventory.model.dto.RecentRequestDTO" %>

<%
    request.setAttribute("pageTitle", "Dashboard");
    request.setAttribute("activePage","dashboard");
%>

<!DOCTYPE html>

<html>

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

</head>

<body>

    <div class="parent-container">

        <%@ include file="../components/sidebar.jsp" %>

        <main class="main-content">

            <%@ include file="../components/topbar.jsp" %>
            
            <div class="dashboard-content">
            
                <% if (request.getAttribute("error") != null) { %>
                    <div class="alert-error-banner" style="background: #FEE2E2; color: #B91C1C; border: 1px solid #FECACA; padding: 15px; border-radius: 12px; margin-bottom: 20px; font-size: 14px; display: flex; align-items: center; gap: 10px; font-weight: 500; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);">
                        <i class="fas fa-circle-exclamation" style="font-size: 16px;"></i>
                        <span><%= request.getAttribute("error") %></span>
                    </div>
                <% } %>
            
                <div class="page-header">

                    <div class="page-header-text">

                        <p>Inventory summaries, issuance by cleaner, supplier activity, and low-stock reports.</p>

                    </div> 
                </div>

                <section class="cards">

                    <div class="card">

                        <div class="card-icon">
                            <i class="fas fa-boxes-stacked"></i>
                        </div>

                        <div class="card-info">

                            <h3>Products</h3>

                            <p>${dashboard.totalProducts}</p>

                            <div class="card-subtitle">
                                ${dashboard.lowStockProducts} below reorder level
                            </div>

                        </div>

                    </div>


                    <div class="card">

                        <div class="card-icon">
                            <i class="fas fa-users"></i>
                        </div>

                        <div class="card-info">

                            <h3>Employees</h3>

                            <p>${dashboard.totalEmployees}</p>

                            <div class="card-subtitle">
                                Storekeepers & Supervisors
                            </div>

                        </div>

                    </div>


                    <div class="card">

                        <div class="card-icon">
                            <i class="fas fa-truck"></i>
                        </div>

                        <div class="card-info">

                            <h3>Suppliers</h3>

                            <p>${dashboard.totalSuppliers}</p>

                            <div class="card-subtitle">
                                Active suppliers
                            </div>

                        </div>

                    </div>


                    <div class="card">

                        <div class="card-icon">
                            <i class="fas fa-file-circle-check"></i>
                        </div>

                        <div class="card-info">

                            <h3>Pending Requests</h3>

                            <p>${dashboard.pendingRequests}</p>

                            <div class="card-subtitle">
                                Awaiting approval
                            </div>

                        </div>

                    </div>

                </section>



                <!-- ======================================
                            TABLES
                ======================================= -->

                <section class="table-section">


                    <!-- LOW STOCK -->

                    <div class="table-card">

                        <h2>Low Stock Alerts</h2>
                        
                        <div class="table-wrapper">

                            <table>

                                <thead>

                                <tr>

                                    <th>Product</th>

                                    <th>Stock</th>

                                </tr>

                                </thead>

                                <tbody>

                                    <%
                                        DashboardDTO dashboard = (DashboardDTO) request.getAttribute("dashboard");

                                        List<LowStockDTO> lowStockProducts = dashboard.getLowStockList();

                                        if (lowStockProducts != null && !lowStockProducts.isEmpty()) {

                                            for (LowStockDTO product : lowStockProducts) {
                                    %>

                                    <tr>

                                        <td><%= product.getProductName() %></td>

                                        <td>

                                            <%
                                                String stockBadge;

                                                if (product.getStock() <= 2) {
                                                    stockBadge = "badge-danger";
                                                } else {
                                                    stockBadge = "badge-warning";
                                                }
                                            %>

                                            <span class="badge <%= stockBadge %>">
                                                <%= product.getStock() %> Left
                                            </span>

                                        </td>

                                    </tr>

                                    <%
                                            }

                                        } else {
                                    %>

                                    <tr>

                                        <td colspan="3" class="text-center">

                                            No low stock products found.

                                        </td>

                                    </tr>

                                    <%
                                        }
                                    %>

                                </tbody>

                            </table>
                        </div>

                    </div>



                    <!-- RECENT REQUESTS -->

                    <div class="table-card">

                        <h2>Recent Requests</h2>
                        
                        <div class="table-wrapper">

                            <table>

                                <thead>

                                <tr>

                                    <th>Employee</th>

                                    <th>Product</th>

                                    <th>Quantity</th>

                                    <th>Status</th>

                                    <th>Priority</th>

                                </tr>

                                </thead>

                                <tbody>

                                    <%
                                        List<RecentRequestDTO> recentRequests = dashboard.getRecentRequests();

                                        if (recentRequests != null && !recentRequests.isEmpty()) {

                                            for (RecentRequestDTO req : recentRequests) {
                                    %>

                                    <tr>

                                        <td><%= req.getEmployee() %></td>

                                        <td><%= req.getProduct() %></td>

                                        <td><%= req.getQuantity() %></td>

                                        <td>

                                            <%
                                                String statusBadge;

                                                switch(req.getStatus().toUpperCase()){

                                                    case "PENDING":
                                                        statusBadge = "badge-warning";
                                                        break;

                                                    case "APPROVED":
                                                        statusBadge = "badge-info";
                                                        break;

                                                    case "ISSUED":
                                                        statusBadge = "badge-success";
                                                        break;

                                                    case "REJECTED":
                                                        statusBadge = "badge-danger";
                                                        break;

                                                    default:
                                                        statusBadge = "badge-secondary";
                                                }
                                            %>

                                            <span class="badge <%= statusBadge %>">

                                                <%= req.getStatus() %>

                                            </span>

                                        </td>

                                        <td>

                                            <%
                                                String priorityBadge;

                                                switch(req.getPriority().toUpperCase()){

                                                    case "LOW":
                                                        priorityBadge = "badge-success";
                                                        break;

                                                    case "NORMAL":
                                                        priorityBadge = "badge-info";
                                                        break;

                                                    case "HIGH":
                                                        priorityBadge = "badge-warning";
                                                        break;

                                                    case "URGENT":
                                                        priorityBadge = "badge-danger";
                                                        break;

                                                    default:
                                                        priorityBadge = "badge-secondary";
                                                }
                                            %>

                                            <span class="badge <%= priorityBadge %>">

                                                <%= req.getPriority() %>

                                            </span>

                                        </td>

                                    </tr>

                                    <%
                                            }

                                        } else {
                                    %>

                                    <tr>

                                        <td colspan="5" class="text-center">

                                            No recent requests found.

                                        </td>

                                    </tr>

                                    <%
                                        }
                                    %>

                                </tbody>

                            </table>
                                    
                        </div>

                    </div>

                </section>
                                
            </div>

        </main>

    </div>

</body>

</html>