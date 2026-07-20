<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    request.setAttribute("pageTitle", "Reports");
    request.setAttribute("activePage","reports");
%>

<!DOCTYPE html>

<html>

<head>

    <meta charset="UTF-8">

    <title>Reports | Cleaning Inventory & Issuance System</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/reports.css">

</head>

<body>

<div class="parent-container">

    <%@ include file="../components/sidebar.jsp" %>

    <main class="main-content">

        <!--=====================================
                    PAGE HEADER
        ======================================-->

        <%@ include file="../components/topbar.jsp" %>

        <div class="page-header">

            <div class="page-header-text">

                <p>View inventory information and export reports.</p>

            </div>

            <div class="page-actions">

                <button class="btn btn-danger">
                    <i class="fas fa-file-pdf"></i>
                    Export PDF
                </button>

                <button class="btn btn-success">
                    <i class="fas fa-file-excel"></i>
                    Export Excel
                </button>

            </div>

        </div>



        <!--=====================================
                REPORT CATEGORIES
        ======================================-->

        <section class="report-cards">

            <div class="report-card active">

                <i class="fas fa-boxes-stacked"></i>

                <h3>Products</h3>

            </div>

            <div class="report-card">

                <i class="fas fa-file-circle-check"></i>

                <h3>Requests</h3>

            </div>

            <div class="report-card">

                <i class="fas fa-cart-shopping"></i>

                <h3>Orders</h3>

            </div>

            <div class="report-card">

                <i class="fas fa-warehouse"></i>

                <h3>Stock</h3>

            </div>

            <div class="report-card">

                <i class="fas fa-truck"></i>

                <h3>Suppliers</h3>

            </div>

        </section>



        <!--=====================================
                    SEARCH
        ======================================-->

        <div class="search-container">

            <i class="fas fa-search"></i>

            <input type="text" placeholder="Search report...">

        </div>



        <!--=====================================
                    REPORT TABLE
        ======================================-->

        <div class="table-card">

            <h2>Products Report</h2>

            <table>

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

                <tr>

                    <td>Bleach</td>

                    <td>ABC Supplies</td>

                    <td>R95.00</td>

                    <td>15</td>

                    <td>

                        <span class="badge badge-success">

                            Available

                        </span>

                    </td>

                </tr>

                <tr>

                    <td>Mop Heads</td>

                    <td>CleanPro</td>

                    <td>R48.00</td>

                    <td>5</td>

                    <td>

                        <span class="badge badge-warning">

                            Low Stock

                        </span>

                    </td>

                </tr>

                <tr>

                    <td>Sanitizer</td>

                    <td>Prime Chemicals</td>

                    <td>R125.00</td>

                    <td>2</td>

                    <td>

                        <span class="badge badge-danger">

                            Critical

                        </span>

                    </td>

                </tr>

                </tbody>

            </table>

        </div>

    </main>

</div>

</body>

</html>