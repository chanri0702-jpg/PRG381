# 🧹 Cleaning Inventory Management System

A Java-based web application developed to streamline the management of cleaning inventory within an organization. The system centralizes inventory operations, allowing organizations to manage materials, suppliers, cleaners, stock requests, stock issuance, and reporting through an intuitive web interface.

---

## 📖 Overview

The **Cleaning Inventory System** replaces manual inventory tracking with a centralized digital solution. It enables organizations to efficiently manage cleaning supplies, monitor stock movements, and generate reports that support informed decision-making.

The application follows the **Model-View-Controller (MVC)** architecture, ensuring a clean separation between the presentation layer, business logic, and data access layer for improved maintainability and scalability.

---

# ✨ Features

## 🔐 Authentication & Authorization

- Secure user login
- Session management
- Role-based access control
- Protected application pages

---

## 📊 Dashboard

- Real-time inventory overview
- Total materials
- Total suppliers
- Total cleaners
- Pending stock requests
- Quick operational insights

---

## 📦 Materials Management

- Add materials
- Edit material information
- Delete materials
- View all materials
- Search materials

---

## 🚚 Suppliers Management

- Register suppliers
- Update supplier information
- Delete suppliers
- View supplier records

---

## 👷 Cleaners Management

- Register cleaners
- Update cleaner information
- Delete cleaner records
- View all cleaners

---

## 📄 Stock Requests

- Employees submit stock requests
- Track request status
- Request management

---

## 🛒 Orders

- Create purchase orders
- View orders
- Manage order records

---

## 📤 Stock Issuance

- Issue inventory to employees
- Record issued quantities
- Maintain issuance history

---

## 📈 Reports

- View inventory reports
- Export reports to **PDF**
- Export reports to **Excel**

---

# 🛠 Technologies Used

## Backend

- Java
- Java Servlets
- JDBC

## Frontend

- JSP
- HTML5
- CSS3

## Database

- PostgreSQL

## Architecture

- MVC (Model-View-Controller)

## Web Server

- Apache Tomcat / TomEE

## Version Control

- Git
- GitHub

---

# 📂 Project Structure

```
CleaningInventorySystem
│
├── database/
│   └── schema.sql
│
├── src/
│   └── java/
│       └── za/
│           └── bc/
│               └── cleaninginventory/
│                   ├── config/
│                   ├── controller/
│                   ├── database/
│                   ├── filter/
│                   ├── model/
│                   │   ├── dao/
│                   │   ├── dto/
│                   │   └── entity/
│                   ├── service/
│                   └── util/
│
├── web/
│   ├── components/
│   ├── css/
│   ├── dashboard/
│   ├── issuance/
│   ├── reports/
│   └── WEB-INF/
│
└── README.md
```

---

# 🏗 System Architecture

The application follows the **MVC (Model-View-Controller)** architecture.

### Model

- Entity Classes
- DAO Classes
- DTO Classes

### View

- JSP Pages
- HTML
- CSS

### Controller

- Java Servlets

This architecture separates business logic from the user interface, making the application easier to maintain, test, and extend.

---

# 🚀 Getting Started

## Prerequisites

Before running the project, ensure you have the following installed:

- JDK 17 or later
- Apache Tomcat 10+
- PostgreSQL
- NetBeans IDE (recommended)
- Git

---

## Clone the Repository

```bash
git clone https://github.com/<YOUR_USERNAME>/CleaningInventorySystem.git
```

---

## Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE CleaningInventoryDB;
```

Run the SQL script located in:

```
database/schema.sql
```

to create all required tables.

---

## Configure Database Connection

Open:

```
src/java/za/bc/cleaninginventory/config/DatabaseConfig.java
```

Replace the placeholder values with your PostgreSQL database credentials.

```java
package za.bc.cleaninginventory.config;

public class DatabaseConfig {

    private DatabaseConfig() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    public static final String URL =
        "jdbc:postgresql://<HOST>:<PORT>/<DATABASE_NAME>";

    public static final String USER =
        "<DATABASE_USERNAME>";

    public static final String PASS =
        "<DATABASE_PASSWORD>";

}
```

Example:

| Placeholder | Example |
|------------|---------|
| `<HOST>` | localhost |
| `<PORT>` | 5432 |
| `<DATABASE_NAME>` | CleaningInventoryDB |
| `<DATABASE_USERNAME>` | postgres |
| `<DATABASE_PASSWORD>` | your_password |

---

## Configure Email (Optional)

If you would like to enable email notifications, open:

```
src/java/za/bc/cleaninginventory/config/EmailConfig.java
```

Replace the placeholders below:

```java
package za.bc.cleaninginventory.config;

public class EmailConfig {

    public static final String SMTP_HOST = "smtp.gmail.com";

    public static final int SMTP_PORT = 587;

    public static final String USERNAME =
        "<EMAIL_ADDRESS>";

    public static final String PASSWORD =
        "<EMAIL_APP_PASSWORD>";

    public static final String FROM_ADDRESS = USERNAME;

}
```

> **Note:** If using Gmail, create an **App Password** from your Google Account and use it instead of your regular account password.

---

## Run the Application

1. Open the project in NetBeans.
2. Build the project.
3. Deploy the application to Apache Tomcat.
4. Start the server.
5. Open your browser and navigate to:

```
http://localhost:8080/CleaningInventorySystem
```

---

# 👥 Team Members

| Team Member | Responsibility |
|-------------|----------------|
| Anele Nkayi | Dashboard, Reports, Project Integration |
| Vuyo | Authentication & Session Management |
| Nicholas | Materials Management & Search |
| Thabani | Suppliers & Cleaners |
| Chanri | Stock Requests, Orders & Stock Issuance |

---

# 🌟 Project Highlights

- MVC Architecture
- Layered Application Design
- Object-Oriented Programming
- JDBC Database Connectivity
- Role-Based Access Control
- Session Management
- PDF Report Generation
- Excel Report Export
- Responsive User Interface
- Git Collaboration

---

# 🚀 Future Improvements

- Email notifications
- Barcode scanning
- QR code integration
- REST API
- Mobile application
- Cloud deployment
- Analytics dashboard
- Audit logs
- Inventory forecasting

---

# 📚 Learning Outcomes

This project provided practical experience in:

- Java Web Development
- JSP & Servlets
- JDBC
- PostgreSQL
- MVC Architecture
- Git & GitHub Collaboration
- Database Design
- Team-based Software Development
- Software Integration
- Debugging & Testing

---

# 📷 Screenshots

Add screenshots of the following pages:

- Login
- Dashboard
- Materials
- Suppliers
- Cleaners
- Requests
- Orders
- Stock Issuance
- Reports
- PDF Export
- Excel Export

---

# 📄 License

This project was developed as an academic project for **Belgium Campus iTversity** and is intended for educational purposes.

---

## ⭐ Acknowledgements

Special thanks to our project team for their collaboration and contributions throughout the development of this application.
