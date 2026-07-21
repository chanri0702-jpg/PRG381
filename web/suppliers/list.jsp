<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="za.bc.cleaninginventory.model.entity.Supplier" %>

<%
    request.setAttribute("pageTitle", "Suppliers Management");
    request.setAttribute("activePage", "suppliers");

    List<Supplier> suppliers =
            (List<Supplier>) request.getAttribute("suppliers");

    String errorMessage =
            (String) request.getAttribute("errorMessage");

    String successMessage =
            request.getParameter("success");

    String queryError =
            request.getParameter("error");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Suppliers | Cleaning Inventory & Issuance System</title>

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
                    Manage supplier businesses, office addresses and primary
                    contact information.
                </p>
            </div>

            <div class="page-actions">
                <a class="btn btn-primary"
                   href="<%= request.getContextPath() %>/suppliers?action=add">

                    <i class="fas fa-plus"></i>
                    Add Supplier
                </a>
            </div>

        </div>

        <% if (successMessage != null && !successMessage.isBlank()) { %>
            <div class="alert alert-success">
                <i class="fas fa-circle-check"></i>
                <span><%= successMessage %></span>
            </div>
        <% } %>

        <% if (errorMessage != null && !errorMessage.isBlank()) { %>
            <div class="alert alert-danger">
                <i class="fas fa-circle-exclamation"></i>
                <span><%= errorMessage %></span>
            </div>
        <% } %>

        <% if (queryError != null && !queryError.isBlank()) { %>
            <div class="alert alert-danger">
                <i class="fas fa-circle-exclamation"></i>
                <span><%= queryError %></span>
            </div>
        <% } %>

        <section class="table-card supplier-table-card">

            <div class="card-heading">

                <div>
                    <h2>Supplier Directory</h2>
                    <p>
                        <%= suppliers == null ? 0 : suppliers.size() %>
                        supplier<%= suppliers != null && suppliers.size() == 1 ? "" : "s" %>
                        registered
                    </p>
                </div>

                <div class="heading-icon">
                    <i class="fas fa-truck"></i>
                </div>

            </div>

            <div class="table-responsive">

                <table>

                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Business</th>
                        <th>Location</th>
                        <th>Contact Person</th>
                        <th>Contact Details</th>
                        <th class="actions-column">Actions</th>
                    </tr>
                    </thead>

                    <tbody>

                    <% if (suppliers == null || suppliers.isEmpty()) { %>

                        <tr>
                            <td colspan="6">

                                <div class="empty-state">

                                    <div class="empty-icon">
                                        <i class="fas fa-truck-ramp-box"></i>
                                    </div>

                                    <h3>No suppliers found</h3>

                                    <p>
                                        Add the first supplier to start managing
                                        vendor and contact information.
                                    </p>

                                    <a class="btn btn-primary"
                                       href="<%= request.getContextPath() %>/suppliers?action=add">

                                        <i class="fas fa-plus"></i>
                                        Add Supplier
                                    </a>

                                </div>

                            </td>
                        </tr>

                    <% } else { %>

                        <% for (Supplier supplier : suppliers) { %>

                            <tr>

                                <td>
                                    <span class="id-pill">
                                        #<%= supplier.getBusinessId() %>
                                    </span>
                                </td>

                                <td>
                                    <div class="business-cell">
                                        <div class="business-icon">
                                            <i class="fas fa-building"></i>
                                        </div>

                                        <div>
                                            <strong>
                                                <%= supplier.getBusinessName() %>
                                            </strong>

                                            <span>
                                                <%= supplier.getDescription() == null
                                                        || supplier.getDescription().isBlank()
                                                        ? "No description provided"
                                                        : supplier.getDescription() %>
                                            </span>
                                        </div>
                                    </div>
                                </td>

                                <td>
                                    <div class="stacked-detail">
                                        <strong>
                                            <%= supplier.getCity() %>
                                        </strong>

                                        <span>
                                            <%= supplier.getProvince() %>
                                        </span>
                                    </div>
                                </td>

                                <td>
                                    <div class="stacked-detail">
                                        <strong>
                                            <%= supplier.getContactName() %>
                                            <%= supplier.getContactSurname() %>
                                        </strong>

                                        <span>Primary contact</span>
                                    </div>
                                </td>

                                <td>
                                    <div class="contact-details">

                                        <span>
                                            <i class="fas fa-envelope"></i>
                                            <%= supplier.getContactEmail() %>
                                        </span>

                                        <span>
                                            <i class="fas fa-phone"></i>
                                            <%= supplier.getContactPhone() %>
                                        </span>

                                    </div>
                                </td>

                                <td>

                                    <div class="row-actions">

                                        <a class="icon-btn view-action"
                                           title="View supplier"
                                           href="<%= request.getContextPath() %>/suppliers?action=view&id=<%= supplier.getBusinessId() %>">

                                            <i class="fas fa-eye"></i>
                                        </a>

                                        <a class="icon-btn edit-action"
                                           title="Edit supplier"
                                           href="<%= request.getContextPath() %>/suppliers?action=edit&id=<%= supplier.getBusinessId() %>">

                                            <i class="fas fa-pen"></i>
                                        </a>

                                        <form method="post"
                                              action="<%= request.getContextPath() %>/suppliers?action=delete"
                                              onsubmit="return confirm('Are you sure you want to delete this supplier?');">

                                            <input type="hidden"
                                                   name="businessId"
                                                   value="<%= supplier.getBusinessId() %>">

                                            <button type="submit"
                                                    class="icon-btn delete-action"
                                                    title="Delete supplier">

                                                <i class="fas fa-trash"></i>
                                            </button>

                                        </form>

                                    </div>

                                </td>

                            </tr>

                        <% } %>

                    <% } %>

                    </tbody>

                </table>

            </div>

        </section>

    </main>

</div>

</body>
</html>
