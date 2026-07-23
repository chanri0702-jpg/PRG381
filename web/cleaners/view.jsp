<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="za.bc.cleaninginventory.model.entity.Cleaner" %>

<%
    request.setAttribute("pageTitle", "Cleaner Details");
    request.setAttribute("activePage", "cleaners");

    Cleaner cleaner =
            (Cleaner) request.getAttribute("cleaner");

    if (cleaner == null) {
        response.sendRedirect(request.getContextPath() + "/cleaners");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Cleaner Details | Cleaning Inventory & Issuance System</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/cims_logo.png">

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap"
          rel="stylesheet">

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/cleaners.css">
</head>

<body>

<div class="parent-container">

    <%@ include file="../components/sidebar.jsp" %>

    <main class="main-content cleaners-page">

        <%@ include file="../components/topbar.jsp" %>

        <div class="page-header">

            <div class="page-header-text">
                <p>
                    Review the cleaner's identity, contact information and
                    campus assignment.
                </p>
            </div>

            <div class="page-actions">

                <a class="btn btn-outline"
                   href="<%= request.getContextPath() %>/cleaners">

                    <i class="fas fa-arrow-left"></i>
                    Back
                </a>

                <a class="btn btn-primary"
                   href="<%= request.getContextPath() %>/cleaners?action=edit&id=<%= cleaner.getCleanerId() %>">

                    <i class="fas fa-pen"></i>
                    Edit Cleaner
                </a>

            </div>

        </div>

        <section class="cleaner-profile-card">

            <div class="profile-banner">

                <div class="profile-avatar">
                    <%= cleaner.getName() == null
                            || cleaner.getName().isBlank()
                            ? "?"
                            : cleaner.getName()
                                    .substring(0, 1)
                                    .toUpperCase() %>
                </div>

                <div class="profile-title">

                    <span class="id-pill">
                        Cleaner #<%= cleaner.getCleanerId() %>
                    </span>

                    <h2>
                        <%= cleaner.getName() %>
                        <%= cleaner.getSurname() %>
                    </h2>

                    <p>
                        Assigned to
                        <strong>
                            <%= cleaner.getCampusName() == null
                                    ? "an unassigned campus"
                                    : cleaner.getCampusName() %>
                        </strong>
                    </p>

                </div>

            </div>

            <div class="details-sections">

                <section class="details-section">

                    <div class="details-heading">

                        <div class="section-icon">
                            <i class="fas fa-user"></i>
                        </div>

                        <div>
                            <h3>Personal Information</h3>
                            <p>Cleaner record details</p>
                        </div>

                    </div>

                    <div class="details-grid">

                        <div class="detail-item">
                            <span class="detail-label">Cleaner ID</span>
                            <strong>#<%= cleaner.getCleanerId() %></strong>
                        </div>

                        <div class="detail-item">
                            <span class="detail-label">Name</span>
                            <strong><%= cleaner.getName() %></strong>
                        </div>

                        <div class="detail-item">
                            <span class="detail-label">Surname</span>
                            <strong><%= cleaner.getSurname() %></strong>
                        </div>

                    </div>

                </section>

                <section class="details-section">

                    <div class="details-heading">

                        <div class="section-icon">
                            <i class="fas fa-address-card"></i>
                        </div>

                        <div>
                            <h3>Contact Information</h3>
                            <p>Communication details</p>
                        </div>

                    </div>

                    <div class="details-grid">

                        <div class="detail-item">
                            <span class="detail-label">Email</span>

                            <a class="detail-link"
                               href="mailto:<%= cleaner.getEmail() %>">

                                <i class="fas fa-envelope"></i>
                                <%= cleaner.getEmail() %>
                            </a>
                        </div>

                        <div class="detail-item">
                            <span class="detail-label">Phone</span>

                            <a class="detail-link"
                               href="tel:<%= cleaner.getPhone() %>">

                                <i class="fas fa-phone"></i>
                                <%= cleaner.getPhone() %>
                            </a>
                        </div>

                    </div>

                </section>

                <section class="details-section campus-details-section">

                    <div class="details-heading">

                        <div class="section-icon">
                            <i class="fas fa-building-columns"></i>
                        </div>

                        <div>
                            <h3>Campus Assignment</h3>
                            <p>Cleaner workplace location</p>
                        </div>

                    </div>

                    <div class="campus-detail-banner">

                        <div class="campus-detail-icon">
                            <i class="fas fa-location-dot"></i>
                        </div>

                        <div>
                            <span>Assigned campus</span>

                            <strong>
                                <%= cleaner.getCampusName() == null
                                        ? "Unassigned"
                                        : cleaner.getCampusName() %>
                            </strong>
                        </div>

                    </div>

                </section>

            </div>

            <div class="profile-actions">

                <a class="btn btn-outline"
                   href="<%= request.getContextPath() %>/cleaners">
                    Back to Cleaners
                </a>

                <form method="post"
                      action="<%= request.getContextPath() %>/cleaners?action=delete"
                      onsubmit="return confirm('Are you sure you want to delete this cleaner?');">

                    <input type="hidden"
                           name="cleanerId"
                           value="<%= cleaner.getCleanerId() %>">

                    <button type="submit" class="btn btn-danger">
                        <i class="fas fa-trash"></i>
                        Delete Cleaner
                    </button>

                </form>

            </div>

        </section>

    </main>

</div>

</body>
</html>
