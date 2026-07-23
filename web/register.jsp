<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - University Cleaning Inventory & Issuance System</title>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg-gradient: linear-gradient(135deg, #0f172a 0%, #1e1b4b 100%);
            --card-bg: rgba(30, 41, 59, 0.7);
            --card-border: rgba(255, 255, 255, 0.08);
            --primary: #6366f1;
            --primary-hover: #4f46e5;
            --text-main: #f8fafc;
            --text-muted: #94a3b8;
            --input-bg: rgba(15, 23, 42, 0.6);
            --input-border: rgba(255, 255, 255, 0.1);
            --input-focus: #818cf8;
            --error-bg: rgba(239, 68, 68, 0.15);
            --error-text: #fca5a5;
            --error-border: rgba(239, 68, 68, 0.3);
            --success-bg: rgba(34, 197, 94, 0.15);
            --success-text: #86efac;
            --success-border: rgba(34, 197, 94, 0.3);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Plus Jakarta Sans', sans-serif;
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
            padding: 40px 20px;
        }

        /* Abstract decorative background elements */
        body::before {
            content: '';
            position: absolute;
            width: 400px;
            height: 400px;
            background: radial-gradient(circle, rgba(99, 102, 241, 0.15) 0%, rgba(0,0,0,0) 70%);
            top: -100px;
            left: -100px;
            z-index: 0;
        }

        body::after {
            content: '';
            position: absolute;
            width: 500px;
            height: 500px;
            background: radial-gradient(circle, rgba(129, 140, 248, 0.1) 0%, rgba(0,0,0,0) 70%);
            bottom: -150px;
            right: -100px;
            z-index: 0;
        }

        .register-container {
            width: 100%;
            max-width: 500px;
            z-index: 10;
        }

        .register-card {
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 24px;
            padding: 40px;
            backdrop-filter: blur(16px);
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }

        .register-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.4);
        }

        .header {
            text-align: center;
            margin-bottom: 30px;
        }

        .header h1 {
            font-size: 28px;
            font-weight: 700;
            letter-spacing: -0.5px;
            background: linear-gradient(to right, #e2e8f0, #94a3b8);
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

        .form-group {
            margin-bottom: 20px;
            position: relative;
        }

        .form-label {
            display: block;
            margin-bottom: 8px;
            font-size: 13px;
            font-weight: 500;
            color: var(--text-muted);
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

        /* Styling select dropdown options for dark theme */
        select.form-control option {
            background-color: #1e293b;
            color: var(--text-main);
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

    <%
        // Pre-populate input values on failure
        String enteredName = request.getAttribute("enteredName") != null ? (String) request.getAttribute("enteredName") : "";
        String enteredSurname = request.getAttribute("enteredSurname") != null ? (String) request.getAttribute("enteredSurname") : "";
        String enteredEmail = request.getAttribute("enteredEmail") != null ? (String) request.getAttribute("enteredEmail") : "";
        String enteredRole = request.getAttribute("enteredRole") != null ? (String) request.getAttribute("enteredRole") : "";
    %>

    <div class="register-container">
        <div class="register-card">
            <div class="header">
                <h1>Create Account</h1>
                <p>Register a new system user profile</p>
            </div>

            <!-- Error Alerts -->
            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <form action="<%= request.getContextPath() %>/register" method="POST">
                <div class="form-group">
                    <label class="form-label" for="name">First Name</label>
                    <input type="text" id="name" name="name" class="form-control" placeholder="Enter your first name" required value="<%= enteredName %>" autocomplete="given-name">
                </div>

                <div class="form-group">
                    <label class="form-label" for="surname">Surname</label>
                    <input type="text" id="surname" name="surname" class="form-control" placeholder="Enter your surname" required value="<%= enteredSurname %>" autocomplete="family-name">
                </div>

                <div class="form-group">
                    <label class="form-label" for="email">Email Address</label>
                    <input type="email" id="email" name="email" class="form-control" placeholder="e.g. name@university.edu" required value="<%= enteredEmail %>" autocomplete="email">
                </div>

                <div class="form-group">
                    <label class="form-label" for="role">Assign User Role</label>
                    <select id="role" name="role" class="form-control" required>
                        <option value="" disabled <%= enteredRole.isEmpty() ? "selected" : "" %>>Select User Role</option>
                        <option value="STOREKEEPER" <%= "STOREKEEPER".equals(enteredRole) ? "selected" : "" %>>Storekeeper (Inventory Manager)</option>
                        <option value="SUPERVISOR" <%= "SUPERVISOR".equals(enteredRole) ? "selected" : "" %>>Supervisor (Reports & Approvals)</option>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label" for="password">Password</label>
                    <input type="password" id="password" name="password" class="form-control" placeholder="Minimum 6 characters" required autocomplete="new-password">
                </div>

                <div class="form-group">
                    <label class="form-label" for="confirmPassword">Confirm Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" placeholder="Retype password" required autocomplete="new-password">
                </div>

                <button type="submit" class="btn-submit">Register Profile</button>
            </form>

            <div class="footer-text">
                Already have an account? <a href="<%= request.getContextPath() %>/login">Sign in here</a>
            </div>
        </div>
    </div>

</body>
</html>
