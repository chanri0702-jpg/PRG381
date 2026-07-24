<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - University Cleaning Inventory & Issuance System</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/cims_logo.png">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg-gradient: #F8FAFC;
            --card-bg: #FFFFFF;
            --card-border: #E2E8F0;
            --primary: #2563EB;
            --primary-hover: #1D4ED8;
            --text-main: #0F172A;
            --text-muted: #64748B;
            --input-bg: #FFFFFF;
            --input-border: #E2E8F0;
            --input-focus: #2563EB;
            --error-bg: #FEE2E2;
            --error-text: #B91C1C;
            --error-border: #FECACA;
            --success-bg: #DCFCE7;
            --success-text: #15803D;
            --success-border: #BBF7D0;
            --shadow: 0 8px 24px rgba(15,23,42,.08);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Poppins', 'Plus Jakarta Sans', sans-serif;
        }

        body {
            min-height: 100vh;
            background: var(--bg-gradient);
            display: flex;
            align-items: center;
            justify-content: center;
            color: var(--text-main);
            overflow-x: hidden;
            position: relative;
        }

        /* Abstract decorative background elements */
        body::before {
            content: '';
            position: absolute;
            width: 400px;
            height: 400px;
            background: radial-gradient(circle, rgba(37, 99, 235, 0.08) 0%, rgba(0,0,0,0) 70%);
            top: -100px;
            left: -100px;
            z-index: 0;
        }

        body::after {
            content: '';
            position: absolute;
            width: 500px;
            height: 500px;
            background: radial-gradient(circle, rgba(59, 130, 246, 0.05) 0%, rgba(0,0,0,0) 70%);
            bottom: -150px;
            right: -100px;
            z-index: 0;
        }

        .login-container {
            width: 100%;
            max-width: 450px;
            padding: 20px;
            z-index: 10;
        }

        .login-card {
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 24px;
            padding: 40px;
            backdrop-filter: blur(16px);
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }

        .login-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.4);
        }

        .header {
            text-align: center;
            margin-bottom: 35px;
        }

        .header h1 {
            font-size: 28px;
            font-weight: 700;
            letter-spacing: -0.5px;
            background: linear-gradient(to right, var(--primary), var(--primary-hover));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            margin-bottom: 8px;
        }

        .header p {
            font-size: 14px;
            color: var(--text-muted);
        }

        .alert {
            padding: 14px 16px;
            border-radius: 12px;
            margin-bottom: 24px;
            font-size: 13px;
            line-height: 1.5;
            display: flex;
            align-items: center;
        }

        .alert-error {
            background: var(--error-bg);
            color: var(--error-text);
            border: 1px solid var(--error-border);
        }

        .alert-success {
            background: var(--success-bg);
            color: var(--success-text);
            border: 1px solid var(--success-border);
        }

        .form-group {
            margin-bottom: 22px;
            position: relative;
        }

        .form-label {
            display: block;
            margin-bottom: 8px;
            font-size: 13px;
            font-weight: 500;
            color: var(--text-muted);
            transition: color 0.2s ease;
        }

        .form-control {
            width: 100%;
            padding: 14px 16px;
            background: var(--input-bg);
            border: 1px solid var(--input-border);
            border-radius: 12px;
            color: var(--text-main);
            font-size: 14px;
            outline: none;
            transition: border-color 0.2s ease, box-shadow 0.2s ease;
        }

        .form-control:focus {
            border-color: var(--input-focus);
            box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.15);
        }

        .btn-submit {
            width: 100%;
            padding: 14px;
            background: var(--primary);
            border: none;
            border-radius: 12px;
            color: white;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            transition: background-color 0.2s ease, transform 0.1s ease;
            margin-top: 10px;
        }

        .btn-submit:hover {
            background: var(--primary-hover);
        }

        .btn-submit:active {
            transform: scale(0.98);
        }

        .footer-text {
            text-align: center;
            margin-top: 30px;
            font-size: 13px;
            color: var(--text-muted);
        }

        .footer-text a {
            color: var(--primary);
            text-decoration: none;
            font-weight: 500;
            transition: color 0.2s ease;
        }

        .footer-text a:hover {
            color: var(--primary-hover);
            text-decoration: underline;
        }
    </style>
</head>
<body>

    <div class="login-container">
        <div class="login-card">
            <div class="header">
                <h1>Cleaning Inventory</h1>
                <p>Sign in to manage stock & system files</p>
            </div>

            <!-- Timeout Alerts -->
            <% if ("true".equals(request.getParameter("timeout"))) { %>
                <div class="alert alert-error">
                    You have been securely logged out due to inactivity.
                </div>
            <% } %>

            <!-- Error Alerts -->
            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <!-- Success Alerts -->
            <% if (request.getAttribute("success") != null) { %>
                <div class="alert alert-success">
                    <%= request.getAttribute("success") %>
                </div>
            <% } %>

            <form action="<%= request.getContextPath() %>/login" method="POST">
                <div class="form-group">
                    <label class="form-label" for="email">Email Address</label>
                    <input type="email" id="email" name="email" class="form-control" placeholder="Enter email address" required autocomplete="email">
                </div>

                <div class="form-group">
                    <label class="form-label" for="password">Password</label>
                    <input type="password" id="password" name="password" class="form-control" placeholder="Enter password" required autocomplete="current-password">
                </div>

                <button type="submit" class="btn-submit">Sign In</button>
            </form>

            <div class="footer-text">
                Don't have an account? <a href="<%= request.getContextPath() %>/register">Register here</a>
            </div>
        </div>
    </div>

</body>
</html>
