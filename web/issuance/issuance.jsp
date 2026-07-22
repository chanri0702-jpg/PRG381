<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<%
    request.setAttribute("pageTitle", "Stock Issuance Management");
    request.setAttribute("activePage","issuance");
%>

<!DOCTYPE html>

<html>

<head>

    <meta charset="UTF-8">

    <title>Issuance | Cleaning Inventory & Issuance System</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/cims_logo.png">

    <!-- Google Font -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <!-- Font Awesome -->
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css">

    <!-- CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/issuance.css">

</head>

<body>

    <div class="parent-container">

        <%@ include file="../components/sidebar.jsp" %>

        <main class="main-content">

            <%@ include file="../components/topbar.jsp" %>
            
            <div class="page-header">

                <div class="page-header-text">

                    <p>Issue stock to cleaners, record quantities, apply approval status, and track stock impact.</p>

                </div> 
            </div>
        </main>
    </div>
        
</body>
</html>