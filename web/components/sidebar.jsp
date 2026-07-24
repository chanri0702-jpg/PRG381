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

            <li class="<%= ("issuance".equals(request.getAttribute("activePage")) || "orders".equals(request.getAttribute("activePage")) || "request".equals(request.getAttribute("activePage"))) ? "active" : "" %>">
                <a href="${pageContext.request.contextPath}/issuance">
                    <i class="fas fa-hand-holding"></i>
                    <span>Stock</span>
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

                <strong>${currentUser.name} ${currentUser.surname}</strong>

                <p>${currentUser.role}</p>

            </div>

        </div>

        <form action="${pageContext.request.contextPath}/logout" method="POST" style="width: 100%;">
            <button type="submit" class="logout-btn">
                <i class="fas fa-right-from-bracket"></i>
                Logout
            </button>
        </form>

    </div>

</aside>

<!-- Session Timeout Modal -->
<div id="session-timeout-modal" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(15, 23, 42, 0.3); z-index: 9999; align-items: center; justify-content: center; backdrop-filter: blur(4px);">
    <div style="background: #ffffff; padding: 30px; border-radius: 16px; border: 1px solid #e2e8f0; text-align: center; color: #0f172a; max-width: 380px; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);">
        <h3 style="margin-bottom: 12px; font-size: 20px; color: #0f172a; font-weight: 600;">Session Timeout</h3>
        <p style="margin-bottom: 24px; color: #64748b; font-size: 14px; line-height: 1.5;">Your session will expire in <strong id="timeout-countdown" style="color: #ef4444; font-size: 16px;">30</strong> seconds due to inactivity.</p>
        <button id="stay-logged-in-btn" style="background: #2563eb; color: white; border: none; padding: 12px 24px; border-radius: 12px; cursor: pointer; font-weight: 600; width: 100%; transition: background 0.2s; font-family: 'Poppins', sans-serif;">Stay Logged In</button>
    </div>
</div>

<script>
    // Client-side inactivity timeout (2 minutes total)
    (function() {
        let warningTimeout;
        let logoutTimeout;
        let countdownInterval;
        let secondsLeft = 30;
        
        const modal = document.getElementById('session-timeout-modal');
        const countdownEl = document.getElementById('timeout-countdown');
        const stayBtn = document.getElementById('stay-logged-in-btn');

        function resetTimer() {
            clearTimeout(warningTimeout);
            clearTimeout(logoutTimeout);
            clearInterval(countdownInterval);
            
            modal.style.display = 'none';
            secondsLeft = 30;
            
            // 90 seconds (1.5 minutes) until the 30-second warning popup
            warningTimeout = setTimeout(showWarning, 90000);
        }
        
        function showWarning() {
            modal.style.display = 'flex';
            countdownEl.innerText = secondsLeft;
            
            // Start the 30 second countdown on the UI
            countdownInterval = setInterval(function() {
                secondsLeft--;
                countdownEl.innerText = secondsLeft;
                if (secondsLeft <= 0) {
                    clearInterval(countdownInterval);
                }
            }, 1000);
            
            // Actually logout after 30 seconds
            logoutTimeout = setTimeout(logout, 30000);
        }
        
        function logout() {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/logout?timeout=true';
            document.body.appendChild(form);
            form.submit();
        }
        
        // Clicking the stay logged in button resets everything
        stayBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            resetTimer();
        });
        
        // Reset timer on any user interaction, ONLY if the warning modal is NOT showing
        const resetIfNoModal = function() {
            if (modal.style.display !== 'flex') {
                resetTimer();
            }
        };

        window.onload = resetTimer;
        document.onmousemove = resetIfNoModal;
        document.onkeypress = resetIfNoModal;
        document.onclick = resetIfNoModal;
        document.onscroll = resetIfNoModal;
        
        // Add hover effect to the button via JS to keep it self-contained
        stayBtn.addEventListener('mouseover', function() { this.style.background = '#1d4ed8'; });
        stayBtn.addEventListener('mouseout', function() { this.style.background = '#2563eb'; });
    })();
</script>