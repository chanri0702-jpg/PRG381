<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="za.bc.cleaninginventory.model.entity.Supplier" %>

<%
    request.setAttribute("pageTitle", "Supplier Details");
    request.setAttribute("activePage", "suppliers");

    Supplier supplier =
            (Supplier) request.getAttribute("supplier");

    if (supplier == null) {
        response.sendRedirect(request.getContextPath() + "/suppliers");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Supplier Details | Cleaning Inventory & Issuance System</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap"
          rel="stylesheet">

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/suppliers.css">
</head>

<body>

<div class="parent-container">

    <%@ include file="../components/sidebar.jsp" %>

    <main class="main-content suppliers-page">

        <%@ include file="../components/topbar.jsp" %>

        <div class="page-header">

            <div class="page-header-text">
                <p>
                    Review the supplier business, office and contact-person
                    information.
                </p>
            </div>

            <div class="page-actions">

                <a class="btn btn-outline"
                   href="<%= request.getContextPath() %>/suppliers">

                    <i class="fas fa-arrow-left"></i>
                    Back
                </a>

                <a class="btn btn-primary"
                   href="<%= request.getContextPath() %>/suppliers?action=edit&id=<%= supplier.getBusinessId() %>">

                    <i class="fas fa-pen"></i>
                    Edit Supplier
                </a>

            </div>

        </div>

        <section class="supplier-profile-card">

            <div class="profile-banner">

                <div class="profile-icon">
                    <i class="fas fa-building"></i>
                </div>

                <div class="profile-title">
                    <span class="id-pill">
                        Supplier #<%= supplier.getBusinessId() %>
                    </span>

                    <h2><%= supplier.getBusinessName() %></h2>

                    <p>
                        <%= supplier.getDescription() == null
                                || supplier.getDescription().isBlank()
                                ? "No supplier description has been provided."
                                : supplier.getDescription() %>
                    </p>
                </div>

            </div>

            <div class="details-sections">

                <section class="details-section">

                    <div class="details-heading">

                        <div class="section-icon">
                            <i class="fas fa-location-dot"></i>
                        </div>

                        <div>
                            <h3>Office Address</h3>
                            <p>Primary supplier location</p>
                        </div>

                    </div>

                    <div class="details-grid">

                        <div class="detail-item">
                            <span class="detail-label">Street address</span>
                            <strong><%= supplier.getAddress() %></strong>
                        </div>

                        <div class="detail-item">
                            <span class="detail-label">Area</span>
                            <strong>
                                <%= supplier.getArea() == null
                                        || supplier.getArea().isBlank()
                                        ? "Not provided"
                                        : supplier.getArea() %>
                            </strong>
                        </div>

                        <div class="detail-item">
                            <span class="detail-label">City</span>
                            <strong><%= supplier.getCity() %></strong>
                        </div>

                        <div class="detail-item">
                            <span class="detail-label">Province</span>
                            <strong><%= supplier.getProvince() %></strong>
                        </div>

                        <div class="detail-item">
                            <span class="detail-label">Postal code</span>
                            <strong><%= supplier.getPostalCode() %></strong>
                        </div>

                    </div>

                </section>

                <section class="details-section">

                    <div class="details-heading">

                        <div class="section-icon">
                            <i class="fas fa-address-card"></i>
                        </div>

                        <div>
                            <h3>Primary Contact</h3>
                            <p>Supplier contact person</p>
                        </div>

                    </div>

                    <div class="details-grid">

                        <div class="detail-item">
                            <span class="detail-label">Full name</span>
                            <strong>
                                <%= supplier.getContactName() %>
                                <%= supplier.getContactSurname() %>
                            </strong>
                        </div>

                        <div class="detail-item">
                            <span class="detail-label">Email</span>
                            <a class="detail-link"
                               href="mailto:<%= supplier.getContactEmail() %>">

                                <i class="fas fa-envelope"></i>
                                <%= supplier.getContactEmail() %>
                            </a>
                        </div>

                        <div class="detail-item">
                            <span class="detail-label">Phone</span>
                            <a class="detail-link"
                               href="tel:<%= supplier.getContactPhone() %>">

                                <i class="fas fa-phone"></i>
                                <%= supplier.getContactPhone() %>
                            </a>
                        </div>

                    </div>

                </section>

            </div>

            <div class="profile-actions">

                <a class="btn btn-outline"
                   href="<%= request.getContextPath() %>/suppliers">
                    Back to Suppliers
                </a>

                <form method="post"
                      action="<%= request.getContextPath() %>/suppliers?action=delete"
                      onsubmit="return confirm('Are you sure you want to delete this supplier?');">

                    <input type="hidden"
                           name="businessId"
                           value="<%= supplier.getBusinessId() %>">

                    <button type="submit" class="btn btn-danger">
                        <i class="fas fa-trash"></i>
                        Delete Supplier
                    </button>

                </form>

            </div>

        </section>

    </main>

</div>

</body>
</html>
