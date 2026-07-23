<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="za.bc.cleaninginventory.model.entity.Supplier" %>

<%
    request.setAttribute("pageTitle", "Add Supplier");
    request.setAttribute("activePage", "suppliers");

    Supplier supplier =
            (Supplier) request.getAttribute("supplier");

    String errorMessage =
            (String) request.getAttribute("errorMessage");

    if (supplier == null) {
        supplier = new Supplier();
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Add Supplier | Cleaning Inventory & Issuance System</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/cims_logo.png">

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
                    Capture the supplier business, office address and primary
                    contact-person details.
                </p>
            </div>

            <div class="page-actions">
                <a class="btn btn-outline"
                   href="<%= request.getContextPath() %>/suppliers">

                    <i class="fas fa-arrow-left"></i>
                    Back to Suppliers
                </a>
            </div>

        </div>

        <% if (errorMessage != null && !errorMessage.isBlank()) { %>
            <div class="alert alert-danger">
                <i class="fas fa-circle-exclamation"></i>
                <span><%= errorMessage %></span>
            </div>
        <% } %>

        <form class="supplier-form"
              method="post"
              action="<%= request.getContextPath() %>/suppliers?action=create">

            <section class="form-card">

                <div class="form-section-header">

                    <div class="section-icon">
                        <i class="fas fa-building"></i>
                    </div>

                    <div>
                        <h2>Business Information</h2>
                        <p>Basic details about the supplier business.</p>
                    </div>

                </div>

                <div class="form-grid">

                    <div class="form-field full-width">
                        <label for="businessName">
                            Business name
                            <span class="required">*</span>
                        </label>

                        <input type="text"
                               id="businessName"
                               name="businessName"
                               placeholder="e.g. CleanPro Supplies"
                               value="<%= supplier.getBusinessName() == null
                                       ? ""
                                       : supplier.getBusinessName() %>"
                               required>
                    </div>

                    <div class="form-field full-width">
                        <label for="description">Description</label>

                        <textarea id="description"
                                  name="description"
                                  placeholder="Describe the products or services supplied"><%= supplier.getDescription() == null
                                          ? ""
                                          : supplier.getDescription() %></textarea>
                    </div>

                </div>

            </section>

            <section class="form-card">

                <div class="form-section-header">

                    <div class="section-icon">
                        <i class="fas fa-location-dot"></i>
                    </div>

                    <div>
                        <h2>Office Address</h2>
                        <p>Location of the supplier's primary office.</p>
                    </div>

                </div>

                <div class="form-grid">

                    <div class="form-field full-width">
                        <label for="address">
                            Street address
                            <span class="required">*</span>
                        </label>

                        <input type="text"
                               id="address"
                               name="address"
                               placeholder="e.g. 5 Industrial Avenue"
                               value="<%= supplier.getAddress() == null
                                       ? ""
                                       : supplier.getAddress() %>"
                               required>
                    </div>

                    <div class="form-field">
                        <label for="area">Area</label>

                        <input type="text"
                               id="area"
                               name="area"
                               placeholder="e.g. Silverton"
                               value="<%= supplier.getArea() == null
                                       ? ""
                                       : supplier.getArea() %>">
                    </div>

                    <div class="form-field">
                        <label for="city">
                            City
                            <span class="required">*</span>
                        </label>

                        <input type="text"
                               id="city"
                               name="city"
                               placeholder="e.g. Pretoria"
                               value="<%= supplier.getCity() == null
                                       ? ""
                                       : supplier.getCity() %>"
                               required>
                    </div>

                    <div class="form-field">
                        <label for="province">
                            Province
                            <span class="required">*</span>
                        </label>

                        <input type="text"
                               id="province"
                               name="province"
                               placeholder="e.g. Gauteng"
                               value="<%= supplier.getProvince() == null
                                       ? ""
                                       : supplier.getProvince() %>"
                               required>
                    </div>

                    <div class="form-field">
                        <label for="postalCode">
                            Postal code
                            <span class="required">*</span>
                        </label>

                        <input type="text"
                               id="postalCode"
                               name="postalCode"
                               maxlength="4"
                               pattern="[0-9]{4}"
                               placeholder="e.g. 0184"
                               value="<%= supplier.getPostalCode() == null
                                       ? ""
                                       : supplier.getPostalCode() %>"
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
                        <h2>Primary Contact</h2>
                        <p>Person responsible for communication with the supplier.</p>
                    </div>

                </div>

                <div class="form-grid">

                    <div class="form-field">
                        <label for="contactName">
                            Name
                            <span class="required">*</span>
                        </label>

                        <input type="text"
                               id="contactName"
                               name="contactName"
                               placeholder="First name"
                               value="<%= supplier.getContactName() == null
                                       ? ""
                                       : supplier.getContactName() %>"
                               required>
                    </div>

                    <div class="form-field">
                        <label for="contactSurname">
                            Surname
                            <span class="required">*</span>
                        </label>

                        <input type="text"
                               id="contactSurname"
                               name="contactSurname"
                               placeholder="Surname"
                               value="<%= supplier.getContactSurname() == null
                                       ? ""
                                       : supplier.getContactSurname() %>"
                               required>
                    </div>

                    <div class="form-field">
                        <label for="contactEmail">
                            Email
                            <span class="required">*</span>
                        </label>

                        <input type="email"
                               id="contactEmail"
                               name="contactEmail"
                               placeholder="contact@supplier.co.za"
                               value="<%= supplier.getContactEmail() == null
                                       ? ""
                                       : supplier.getContactEmail() %>"
                               required>
                    </div>

                    <div class="form-field">
                        <label for="contactPhone">
                            Phone
                            <span class="required">*</span>
                        </label>

                        <input type="tel"
                               id="contactPhone"
                               name="contactPhone"
                               maxlength="10"
                               pattern="[0-9]{10}"
                               placeholder="0712345678"
                               value="<%= supplier.getContactPhone() == null
                                       ? ""
                                       : supplier.getContactPhone() %>"
                               required>
                    </div>

                </div>

            </section>

            <div class="form-actions">

                <a class="btn btn-outline"
                   href="<%= request.getContextPath() %>/suppliers">
                    Cancel
                </a>

                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-floppy-disk"></i>
                    Save Supplier
                </button>

            </div>

        </form>

    </main>

</div>

</body>
</html>
