<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    request.setAttribute("pageTitle", "Reports");
    request.setAttribute("activePage", "reports");

    String reportType = (String) request.getAttribute("reportType");

    if (reportType == null || reportType.isEmpty()) {
        reportType = "products";
    }

    String reportTitle = reportType.substring(0, 1).toUpperCase()
            + reportType.substring(1);
%>

<!DOCTYPE html>

<html>

    <head>

        <meta charset="UTF-8">

        <title>Reports | Cleaning Inventory & Issuance System</title>
        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/cims_logo.png">

        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap"
              rel="stylesheet">

        <link rel="stylesheet"
              href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css">

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/css/common.css">

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/css/reports.css">

    </head>

    <body>

        <div class="parent-container">

            <%@ include file="../components/sidebar.jsp" %>

            <main class="main-content">

                <%@ include file="../components/topbar.jsp" %>


                <div class="reports-content">
                    <!--=====================================
                                PAGE HEADER
                    ======================================-->

                    <div class="page-header">

                        <div class="page-header-text">

                            <p>View inventory information and export reports.</p>

                        </div>

                        <div class="page-actions">

                            <a href="${pageContext.request.contextPath}/exportPdf?type=<%= reportType%>"
                               class="btn btn-danger">

                                <i class="fas fa-file-pdf"></i>

                                Export PDF

                            </a>

                            <a href="${pageContext.request.contextPath}/exportExcel?type=<%= reportType%>"
                               class="btn btn-success">

                                <i class="fas fa-file-excel"></i>

                                Export Excel

                            </a>

                        </div>

                    </div>



                    <!--=====================================
                            REPORT CATEGORIES
                    ======================================-->

                    <section class="report-cards">

                        <a href="${pageContext.request.contextPath}/reports?type=products"
                           class="report-card <%= reportType.equals("products") ? "active" : ""%>">

                            <i class="fas fa-boxes-stacked"></i>

                            <h3>Products</h3>

                        </a>


                        <a href="${pageContext.request.contextPath}/reports?type=requests"
                           class="report-card <%= reportType.equals("requests") ? "active" : ""%>">

                            <i class="fas fa-file-circle-check"></i>

                            <h3>Requests</h3>

                        </a>


                        <a href="${pageContext.request.contextPath}/reports?type=orders"
                           class="report-card <%= reportType.equals("orders") ? "active" : ""%>">

                            <i class="fas fa-cart-shopping"></i>

                            <h3>Orders</h3>

                        </a>


                        <a href="${pageContext.request.contextPath}/reports?type=stock"
                           class="report-card <%= reportType.equals("stock") ? "active" : ""%>">

                            <i class="fas fa-warehouse"></i>

                            <h3>Stock</h3>

                        </a>


                        <a href="${pageContext.request.contextPath}/reports?type=suppliers"
                           class="report-card <%= reportType.equals("suppliers") ? "active" : ""%>">

                            <i class="fas fa-truck"></i>

                            <h3>Suppliers</h3>

                        </a>

                    </section>



                    <!--=====================================
                                SEARCH
                    ======================================-->

                    <div class="search-container">

                        <i class="fas fa-search"></i>

                        <input type="text" id="reportSearch" placeholder="Search report...">

                    </div>



                    <!--=====================================
                                REPORT TABLE
                    ======================================-->

                    <div class="table-card">

                        <h2><%= reportTitle%> Report</h2>

                        <div class="table-wrapper">

                            <table id="reportTable">

                                <%
                                    if (reportType.equals("products")) {
                                %>

                                <jsp:include page="tables/productsTable.jsp" />

                                <%
                                } else if (reportType.equals("requests")) {
                                %>

                                <jsp:include page="tables/requestsTable.jsp" />

                                <%
                                } else if (reportType.equals("orders")) {
                                %>

                                <jsp:include page="tables/ordersTable.jsp" />

                                <%
                                } else if (reportType.equals("stock")) {
                                %>

                                <jsp:include page="tables/stockTable.jsp" />

                                <%
                                } else if (reportType.equals("suppliers")) {
                                %>

                                <jsp:include page="tables/suppliersTable.jsp" />

                                <%
                                    }
                                %>

                            </table>

                        </div>

                    </div>

                </div>

            </main>

        </div>

        <script>

            const searchBox = document.getElementById("reportSearch");
            const table = document.getElementById("reportTable");

            searchBox.addEventListener("keyup", function () {

                const filter = this.value.toLowerCase();

                const rows = table.getElementsByTagName("tbody")[0].getElementsByTagName("tr");

                for (let row of rows) {

                    let found = false;
                    const cells = row.getElementsByTagName("td");

                    for (let cell of cells) {

                        if (cell.textContent.toLowerCase().includes(filter)) {

                            found = true;
                            break;

                        }

                    }

                    row.style.display = found ? "" : "none";

                }

            });

        </script>

    </body>

</html>