<aside class="sidebar">

    <!-- Logo -->
    <div class="sidebar-header">

        <div class="logo-icon">
            <i class="fas fa-pump-soap"></i>
        </div>

        <div class="logo-text">
            <h2>Cleaning Inventory</h2>
            <p>Management System</p>
        </div>

    </div>


    <!-- Navigation -->

    <nav>

        <ul class="menu">

            <li class="<%= "dashboard".equals(request.getAttribute("activePage")) ? "active" : "" %>">
                <a href="${pageContext.request.contextPath}/dashboard">
                    <i class="fas fa-house"></i>
                    <span>Dashboard</span>
                </a>
            </li>

            <li class="<%= "materials".equals(request.getAttribute("activePage")) ? "active" : "" %>">
                <a href="${pageContext.request.contextPath}/materials">
                    <i class="fas fa-boxes-stacked"></i>
                    <span>Materials</span>
                </a>
            </li>

            <li class="<%= "suppliers".equals(request.getAttribute("activePage")) ? "active" : "" %>">
                <a href="${pageContext.request.contextPath}/suppliers">
                    <i class="fas fa-truck"></i>
                    <span>Suppliers</span>
                </a>
            </li>

            <li class="<%= "cleaners".equals(request.getAttribute("activePage")) ? "active" : "" %>">
                <a href="${pageContext.request.contextPath}/cleaners">
                    <i class="fas fa-users"></i>
                    <span>Cleaners</span>
                </a>
            </li>

            <li class="<%= "issuance".equals(request.getAttribute("activePage")) ? "active" : "" %>">
                <a href="${pageContext.request.contextPath}/issuance">
                    <i class="fas fa-hand-holding"></i>
                    <span>Stock Issuance</span>
                </a>
            </li>

            <li class="<%= "reports".equals(request.getAttribute("activePage")) ? "active" : "" %>">
                <a href="${pageContext.request.contextPath}/reports">
                    <i class="fas fa-chart-column"></i>
                    <span>Reports</span>
                </a>
            </li>

        </ul>

    </nav>


    <!-- User -->

    <div class="sidebar-footer">

        <small>ACTIVE SESSION</small>

        <div class="user-card">

            <div class="user-icon">

                <i class="fas fa-user"></i>

            </div>

            <div>

                <strong>Anele Nkayi</strong>

                <p>Storekeeper</p>

            </div>

        </div>

        <button class="logout-btn">

            <i class="fas fa-right-from-bracket"></i>

            Logout

        </button>

    </div>

</aside>