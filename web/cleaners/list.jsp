<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Set" %>
<%@ page import="za.bc.cleaninginventory.model.entity.Cleaner" %>

<%
    request.setAttribute("pageTitle", "Cleaners Management");
    request.setAttribute("activePage", "cleaners");

    List<Cleaner> cleaners =
            (List<Cleaner>) request.getAttribute("cleaners");

    Map<Integer, String> campuses =
            (Map<Integer, String>) request.getAttribute("campuses");

    String errorMessage =
            (String) request.getAttribute("errorMessage");

    String successMessage =
            request.getParameter("success");

    String queryError =
            request.getParameter("error");

    za.bc.cleaninginventory.model.entity.Employee listUser = (za.bc.cleaninginventory.model.entity.Employee) session.getAttribute("currentUser");
    String userRole = (listUser != null) ? listUser.getRole() : "";

    Set<Integer> representedCampuses = new HashSet<>();

    if (cleaners != null) {
        for (Cleaner cleaner : cleaners) {
            representedCampuses.add(cleaner.getCampusId());
        }
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Cleaners | Cleaning Inventory & Issuance System</title>
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
                    Manage cleaner records, campus assignments and contact
                    information.
                </p>
            </div>

            <% if ("SUPERVISOR".equalsIgnoreCase(userRole)) { %>
            <div class="page-actions">
                <a class="btn btn-primary"
                   href="<%= request.getContextPath() %>/cleaners?action=add">

                    <i class="fas fa-user-plus"></i>
                    Add Cleaner
                </a>
            </div>
            <% } %>

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

        <section class="cleaner-summary-grid">

            <article class="summary-card">

                <div class="summary-icon total-icon">
                    <i class="fas fa-users"></i>
                </div>

                <div>
                    <span>Total Cleaners</span>
                    <strong><%= cleaners == null ? 0 : cleaners.size() %></strong>
                </div>

            </article>

            <article class="summary-card">

                <div class="summary-icon campus-icon">
                    <i class="fas fa-building-columns"></i>
                </div>

                <div>
                    <span>Campuses Represented</span>
                    <strong><%= representedCampuses.size() %></strong>
                </div>

            </article>

        </section>

        <section class="filter-card">

            <div class="search-field">
                <i class="fas fa-magnifying-glass"></i>

                <input type="search"
                       id="cleanerSearch"
                       placeholder="Search by name, email or phone">
            </div>

            <div class="campus-filter">

                <label for="campusFilter">
                    <i class="fas fa-filter"></i>
                    Campus
                </label>

                <select id="campusFilter">
                    <option value="">All campuses</option>

                    <% if (campuses != null) { %>
                        <% for (Map.Entry<Integer, String> campus
                                : campuses.entrySet()) { %>

                            <option value="<%= campus.getKey() %>">
                                <%= campus.getValue() %>
                            </option>

                        <% } %>
                    <% } %>
                </select>

            </div>

        </section>

        <section class="table-card cleaner-table-card">

            <div class="card-heading">

                <div>
                    <h2>Cleaner Roster</h2>
                    <p id="resultCount">
                        <%= cleaners == null ? 0 : cleaners.size() %>
                        cleaner<%= cleaners != null && cleaners.size() == 1 ? "" : "s" %>
                        shown
                    </p>
                </div>

                <div class="heading-icon">
                    <i class="fas fa-broom"></i>
                </div>

            </div>

            <div class="table-responsive">

                <table>

                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Cleaner</th>
                        <th>Campus</th>
                        <th>Contact Details</th>
                        <th class="actions-column">Actions</th>
                    </tr>
                    </thead>

                    <tbody id="cleanerTableBody">

                    <% if (cleaners == null || cleaners.isEmpty()) { %>

                        <tr>
                            <td colspan="5">

                                <div class="empty-state">

                                    <div class="empty-icon">
                                        <i class="fas fa-user-group"></i>
                                    </div>

                                    <h3>No cleaners found</h3>

                                    <p>
                                        Add the first cleaner to start building
                                        the campus cleaner roster.
                                    </p>

                                    <% if ("SUPERVISOR".equalsIgnoreCase(userRole)) { %>
                                    <a class="btn btn-primary"
                                       href="<%= request.getContextPath() %>/cleaners?action=add">

                                        <i class="fas fa-user-plus"></i>
                                        Add Cleaner
                                    </a>
                                    <% } %>

                                </div>

                            </td>
                        </tr>

                    <% } else { %>

                        <% for (Cleaner cleaner : cleaners) { %>

                            <tr class="cleaner-row"
                                data-campus="<%= cleaner.getCampusId() %>"
                                data-search="<%= (
                                        cleaner.getName()
                                        + " "
                                        + cleaner.getSurname()
                                        + " "
                                        + cleaner.getEmail()
                                        + " "
                                        + cleaner.getPhone()
                                        + " "
                                        + cleaner.getCampusName()
                                ).toLowerCase() %>">

                                <td>
                                    <span class="id-pill">
                                        #<%= cleaner.getCleanerId() %>
                                    </span>
                                </td>

                                <td>
                                    <div class="person-cell">

                                        <div class="person-avatar">
                                            <%= cleaner.getName() == null
                                                    || cleaner.getName().isBlank()
                                                    ? "?"
                                                    : cleaner.getName()
                                                            .substring(0, 1)
                                                            .toUpperCase() %>
                                        </div>

                                        <div>
                                            <strong>
                                                <%= cleaner.getName() %>
                                                <%= cleaner.getSurname() %>
                                            </strong>

                                            <span>Cleaner</span>
                                        </div>

                                    </div>
                                </td>

                                <td>
                                    <span class="campus-badge">
                                        <i class="fas fa-location-dot"></i>

                                        <%= cleaner.getCampusName() == null
                                                ? "Unassigned campus"
                                                : cleaner.getCampusName() %>
                                    </span>
                                </td>

                                <td>
                                    <div class="contact-details">

                                        <span>
                                            <i class="fas fa-envelope"></i>
                                            <%= cleaner.getEmail() %>
                                        </span>

                                        <span>
                                            <i class="fas fa-phone"></i>
                                            <%= cleaner.getPhone() %>
                                        </span>

                                    </div>
                                </td>

                                <td>

                                    <div class="row-actions">

                                        <a class="icon-btn view-action"
                                           title="View cleaner"
                                           href="<%= request.getContextPath() %>/cleaners?action=view&id=<%= cleaner.getCleanerId() %>">

                                            <i class="fas fa-eye"></i>
                                        </a>

                                         <% if ("SUPERVISOR".equalsIgnoreCase(userRole)) { %>
                                         <a class="icon-btn edit-action"
                                            title="Edit cleaner"
                                            href="<%= request.getContextPath() %>/cleaners?action=edit&id=<%= cleaner.getCleanerId() %>">

                                             <i class="fas fa-pen"></i>
                                         </a>

                                         <form method="post"
                                               action="<%= request.getContextPath() %>/cleaners?action=delete"
                                               onsubmit="return confirm('Are you sure you want to delete this cleaner?');">

                                             <input type="hidden"
                                                    name="cleanerId"
                                                    value="<%= cleaner.getCleanerId() %>">

                                             <button type="submit"
                                                     class="icon-btn delete-action"
                                                     title="Delete cleaner">

                                                 <i class="fas fa-trash"></i>
                                             </button>

                                         </form>
                                         <% } %>

                                    </div>

                                </td>

                            </tr>

                        <% } %>

                    <% } %>

                    <tr id="noFilterResults" class="hidden-row">
                        <td colspan="5">

                            <div class="empty-state compact-empty">

                                <div class="empty-icon">
                                    <i class="fas fa-magnifying-glass"></i>
                                </div>

                                <h3>No matching cleaners</h3>

                                <p>
                                    Change the search text or campus filter.
                                </p>

                            </div>

                        </td>
                    </tr>

                    </tbody>

                </table>

            </div>

        </section>

    </main>

</div>

<script>
    const searchInput = document.getElementById("cleanerSearch");
    const campusFilter = document.getElementById("campusFilter");
    const cleanerRows = Array.from(
        document.querySelectorAll(".cleaner-row")
    );
    const resultCount = document.getElementById("resultCount");
    const noFilterResults =
        document.getElementById("noFilterResults");

    function filterCleaners() {

        const searchText =
            searchInput.value.trim().toLowerCase();

        const selectedCampus =
            campusFilter.value;

        let visibleCount = 0;

        cleanerRows.forEach(function (row) {

            const matchesSearch =
                row.dataset.search.includes(searchText);

            const matchesCampus =
                selectedCampus === ""
                || row.dataset.campus === selectedCampus;

            const visible =
                matchesSearch && matchesCampus;

            row.style.display = visible ? "" : "none";

            if (visible) {
                visibleCount++;
            }
        });

        resultCount.textContent =
            visibleCount
            + (visibleCount === 1
                ? " cleaner shown"
                : " cleaners shown");

        if (cleanerRows.length > 0) {
            noFilterResults.classList.toggle(
                "hidden-row",
                visibleCount !== 0
            );
        }
    }

    searchInput.addEventListener("input", filterCleaners);
    campusFilter.addEventListener("change", filterCleaners);
</script>

</body>
</html>
