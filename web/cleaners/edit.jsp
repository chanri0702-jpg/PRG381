<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%@ page import="za.bc.cleaninginventory.model.entity.Cleaner" %>

<%
    request.setAttribute("pageTitle", "Edit Cleaner");
    request.setAttribute("activePage", "cleaners");

    Cleaner cleaner =
            (Cleaner) request.getAttribute("cleaner");

    Map<Integer, String> campuses =
            (Map<Integer, String>) request.getAttribute("campuses");

    String errorMessage =
            (String) request.getAttribute("errorMessage");

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

    <title>Edit Cleaner | Cleaning Inventory & Issuance System</title>

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
                    Update the cleaner's personal details, contact information
                    and assigned campus.
                </p>
            </div>

            <div class="page-actions">
                <a class="btn btn-outline"
                   href="<%= request.getContextPath() %>/cleaners">

                    <i class="fas fa-arrow-left"></i>
                    Back to Cleaners
                </a>
            </div>

        </div>

        <% if (errorMessage != null && !errorMessage.isBlank()) { %>
            <div class="alert alert-danger">
                <i class="fas fa-circle-exclamation"></i>
                <span><%= errorMessage %></span>
            </div>
        <% } %>

        <form class="cleaner-form"
              method="post"
              action="<%= request.getContextPath() %>/cleaners?action=update">

            <input type="hidden"
                   name="cleanerId"
                   value="<%= cleaner.getCleanerId() %>">

            <section class="form-card">

                <div class="form-section-header">

                    <div class="section-icon">
                        <i class="fas fa-user"></i>
                    </div>

                    <div>
                        <h2>Personal Information</h2>
                        <p>Enter the cleaner's full name.</p>
                    </div>

                </div>

                <div class="form-grid">

                    <div class="form-field">
                        <label for="name">
                            Name
                            <span class="required">*</span>
                        </label>

                        <input type="text"
                               id="name"
                               name="name"
                               maxlength="60"
                               placeholder="e.g. Nomvula"
                               value="<%= cleaner.getName() == null
                                       ? ""
                                       : cleaner.getName() %>"
                               required>
                    </div>

                    <div class="form-field">
                        <label for="surname">
                            Surname
                            <span class="required">*</span>
                        </label>

                        <input type="text"
                               id="surname"
                               name="surname"
                               maxlength="60"
                               placeholder="e.g. Sithole"
                               value="<%= cleaner.getSurname() == null
                                       ? ""
                                       : cleaner.getSurname() %>"
                               required>
                    </div>

                </div>

            </section>

            <section class="form-card">

                <div class="form-section-header">

                    <div class="section-icon">
                        <i class="fas fa-address-card"></i>
                    </div>

                    <div>
                        <h2>Contact Information</h2>
                        <p>Details used to contact the cleaner.</p>
                    </div>

                </div>

                <div class="form-grid">

                    <div class="form-field">
                        <label for="email">
                            Email address
                            <span class="required">*</span>
                        </label>

                        <input type="email"
                               id="email"
                               name="email"
                               placeholder="cleaner@example.com"
                               value="<%= cleaner.getEmail() == null
                                       ? ""
                                       : cleaner.getEmail() %>"
                               required>
                    </div>

                    <div class="form-field">
                        <label for="phone">
                            Phone number
                            <span class="required">*</span>
                        </label>

                        <input type="tel"
                               id="phone"
                               name="phone"
                               maxlength="10"
                               pattern="[0-9]{10}"
                               inputmode="numeric"
                               placeholder="0712345678"
                               value="<%= cleaner.getPhone() == null
                                       ? ""
                                       : cleaner.getPhone() %>"
                               required>
                    </div>

                </div>

            </section>

            <section class="form-card">

                <div class="form-section-header">

                    <div class="section-icon">
                        <i class="fas fa-building-columns"></i>
                    </div>

                    <div>
                        <h2>Campus Assignment</h2>
                        <p>Select the campus where the cleaner works.</p>
                    </div>

                </div>

                <div class="form-grid">

                    <div class="form-field full-width">
                        <label for="campusId">
                            Campus
                            <span class="required">*</span>
                        </label>

                        <select id="campusId"
                                name="campusId"
                                required>

                            <option value="">Select a campus</option>

                            <% if (campuses != null) { %>
                                <% for (Map.Entry<Integer, String> campus
                                        : campuses.entrySet()) { %>

                                    <option
                                        value="<%= campus.getKey() %>"
                                        <%= cleaner.getCampusId()
                                                == campus.getKey()
                                                ? "selected"
                                                : "" %>>

                                        <%= campus.getValue() %>
                                    </option>

                                <% } %>
                            <% } %>

                        </select>
                    </div>

                </div>

            </section>

            <div class="form-actions">

                <a class="btn btn-outline"
                   href="<%= request.getContextPath() %>/cleaners">
                    Cancel
                </a>

                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-floppy-disk"></i>
                    Save Changes
                </button>

            </div>

        </form>

    </main>

</div>

</body>
</html>
