<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
    request.setAttribute("pageTitle", "Materials Management");
    request.setAttribute("activePage","materials");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Materials | Cleaning Inventory & Issuance System</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/cims_logo.png">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/materials.css">
    
    <style>
        .alert {
            padding: 12px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .alert-success {
            background: #DCFCE7;
            color: #15803D;
        }
        .alert-danger {
            background: #FEE2E2;
            color: #B91C1C;
        }
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 20px;
            margin-bottom: 30px;
        }
        .search-grid {
            display: grid;
            grid-template-columns: 1fr 1fr 1fr auto;
            gap: 15px;
            align-items: end;
        }
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            font-weight: 500;
            margin-bottom: 5px;
            color: #0F172A;
        }
        .form-group input,
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 10px 14px;
            border: 1px solid #E2E8F0;
            border-radius: 12px;
            font-size: 14px;
            transition: 0.3s;
        }
        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #2563EB;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
        }
        .action-buttons {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;
        }
        .action-buttons .btn {
            padding: 5px 12px;
            font-size: 13px;
        }
        .view-details {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }
        .view-item {
            padding: 15px;
            background: #F8FAFC;
            border-radius: 12px;
        }
        .view-item label {
            display: block;
            font-size: 12px;
            color: #94A3B8;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 5px;
        }
        .view-item p {
            margin: 0;
            font-size: 16px;
            color: #0F172A;
            font-weight: 500;
        }
        .status-badge {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 50px;
            font-size: 13px;
            font-weight: 600;
        }
        .status-badge.in-stock {
            background: #DCFCE7;
            color: #15803D;
        }
        .status-badge.low-stock {
            background: #FEE2E2;
            color: #B91C1C;
        }
        .page-section {
            display: none;
        }
        .page-section.active {
            display: block;
        }
        
        .main-content {
            flex: 1;
            display: flex;
            flex-direction: column;
            overflow-y: auto !important;
            overflow-x: hidden;
            padding: 35px 70px;
            height: 100vh;
        }
        
        .parent-container {
            display: flex;
            height: 100vh;
            overflow: hidden;
            background: var(--background-color);
        }
        
        .main-content > * {
            flex-shrink: 0;
        }
        
        .main-content .table-card {
            flex-shrink: 0;
        }
        
        .table-card {
            overflow-x: auto;
        }
        
        .table-card table {
            min-width: 700px;
        }
        
        @media (max-width: 768px) {
            .stats-grid {
                grid-template-columns: 1fr 1fr !important;
            }
            .search-grid {
                grid-template-columns: 1fr;
            }
            .form-row {
                grid-template-columns: 1fr;
            }
            .view-details {
                grid-template-columns: 1fr;
            }
            .main-content {
                padding: 20px;
            }
        }
        @media (max-width: 992px) {
            .search-grid {
                grid-template-columns: 1fr 1fr;
            }
        }
        
        .page-header {
            flex-shrink: 0;
        }
        
        /* Remove duplicate header from topbar */
        .topbar h1, .topbar p {
            display: none;
        }
    </style>
</head>
<body>
    <div class="parent-container">
        <%@ include file="../components/sidebar.jsp" %>
        
        <main class="main-content">
            <%@ include file="../components/topbar.jsp" %>
            
            <!-- Determine which view to show -->
            <c:set var="viewMode" value="${viewMode}" />
            <c:if test="${empty viewMode}">
                <c:set var="viewMode" value="list" />
            </c:if>
            
            <!-- Display Messages -->
            <c:if test="${not empty param.success}">
                <div class="alert alert-success">
                    <span><i class="fas fa-check-circle"></i> ${param.success}</span>
                </div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="alert alert-danger">
                    <span><i class="fas fa-exclamation-circle"></i> ${param.error}</span>
                </div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">
                    <span><i class="fas fa-exclamation-circle"></i> ${errorMessage}</span>
                </div>
            </c:if>
            
            <!-- ============================================ -->
            <!-- LIST VIEW -->
            <!-- ============================================ -->
            <c:if test="${viewMode == 'list'}">
                <div class="page-section active">
                    <div class="page-header">
                        <div class="page-header-text">
                            <h1>Materials Management</h1>
                            <p>Manage various items including cleaning stock, categories, reorder levels, search, filtering, and low-stock status.</p>
                        </div>
                        <c:if test="${currentUser.role == 'STOREKEEPER'}">
                        <div class="page-actions">
                            <a href="${pageContext.request.contextPath}/materials?action=add" class="btn btn-primary">
                                <i class="fas fa-plus"></i> Add New Material
                            </a>
                        </div>
                        </c:if>
                    </div>
                    
                    <!-- Statistics Cards -->
                    <div class="stats-grid">
                        <div class="card" style="padding: 20px;">
                            <div style="display: flex; align-items: center; gap: 15px;">
                                <div style="width: 48px; height: 48px; background: #DBEAFE; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #2563EB;">
                                    <i class="fas fa-boxes" style="font-size: 20px;"></i>
                                </div>
                                <div>
                                    <small style="color: #94A3B8;">Total Materials</small>
                                    <h3 style="margin: 0;">${totalMaterials}</h3>
                                </div>
                            </div>
                        </div>
                        
                        <div class="card" style="padding: 20px;">
                            <div style="display: flex; align-items: center; gap: 15px;">
                                <div style="width: 48px; height: 48px; background: #FEF3C7; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #D97706;">
                                    <i class="fas fa-exclamation-triangle" style="font-size: 20px;"></i>
                                </div>
                                <div>
                                    <small style="color: #94A3B8;">Low Stock Items</small>
                                    <h3 style="margin: 0; color: #D97706;">${lowStockCount}</h3>
                                </div>
                            </div>
                        </div>
                        
                        <div class="card" style="padding: 20px;">
                            <div style="display: flex; align-items: center; gap: 15px;">
                                <div style="width: 48px; height: 48px; background: #DCFCE7; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #16A34A;">
                                    <i class="fas fa-tags" style="font-size: 20px;"></i>
                                </div>
                                <div>
                                    <small style="color: #94A3B8;">Suppliers</small>
                                    <h3 style="margin: 0;">${suppliers.size()}</h3>
                                </div>
                            </div>
                        </div>
                        
                        <div class="card" style="padding: 20px;">
                            <div style="display: flex; align-items: center; gap: 15px;">
                                <div style="width: 48px; height: 48px; background: #FCE4EC; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #DC2626;">
                                    <i class="fas fa-university" style="font-size: 20px;"></i>
                                </div>
                                <div>
                                    <small style="color: #94A3B8;">Campuses</small>
                                    <h3 style="margin: 0;">${campuses.size()}</h3>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Search and Filter -->
                    <div class="card" style="padding: 20px; margin-bottom: 30px;">
                        <form action="${pageContext.request.contextPath}/materials" method="get" class="search-grid">
                            <input type="hidden" name="action" value="search">
                            <div>
                                <label for="searchTerm" style="display: block; font-weight: 500; margin-bottom: 5px;">Search</label>
                                <input type="text" id="searchTerm" name="searchTerm" placeholder="Search by name or description" 
                                       value="${searchTerm}" style="width: 100%;">
                            </div>
                            
                            <div>
                                <label for="supplierId" style="display: block; font-weight: 500; margin-bottom: 5px;">Supplier</label>
                                <select id="supplierId" name="supplierId" style="width: 100%; padding: 10px; border: 1px solid #E2E8F0; border-radius: 16px;">
                                    <option value="All">All Suppliers</option>
                                    <c:forEach items="${suppliers}" var="supplier">
                                        <c:choose>
                                            <c:when test="${selectedSupplier != null && selectedSupplier == supplier.busId.toString()}">
                                                <option value="${supplier.busId}" selected>${supplier.name}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${supplier.busId}">${supplier.name}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select>
                            </div>
                            
                            <div>
                                <label for="campusId" style="display: block; font-weight: 500; margin-bottom: 5px;">Campus</label>
                                <select id="campusId" name="campusId" style="width: 100%; padding: 10px; border: 1px solid #E2E8F0; border-radius: 16px;">
                                    <option value="All">All Campuses</option>
                                    <c:forEach items="${campuses}" var="campus">
                                        <c:choose>
                                            <c:when test="${selectedCampus != null && selectedCampus == campus.campId.toString()}">
                                                <option value="${campus.campId}" selected>${campus.name}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${campus.campId}">${campus.name}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select>
                            </div>
                            
                            <div>
                                <button type="submit" class="btn btn-primary" style="width: 100%;">
                                    <i class="fas fa-search"></i> Search
                                </button>
                            </div>
                        </form>
                    </div>
                    
                    <!-- Materials Table -->
                    <div class="table-card">
                        <h2><i class="fas fa-list"></i> Material Inventory</h2>
                        <div style="overflow-x: auto;">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Name</th>
                                        <th>Supplier</th>
                                        <th>Price</th>
                                        <th>Stock</th>
                                        <th>Campus</th>
                                        <th>Status</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty materials}">
                                            <c:forEach items="${materials}" var="material">
                                                <tr>
                                                    <td><strong>${material.name}</strong>
                                                        <br><small style="color: #94A3B8;">${material.description}</small>
                                                    </td>
                                                    <td>${material.supplierName}</td>
                                                    <td>
                                                        <fmt:formatNumber value="${material.price}" type="currency" currencySymbol="R" maxFractionDigits="2" minFractionDigits="2"/>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${material.stockQuantity <= 10}">
                                                                <span style="color: #DC2626; font-weight: bold;">${material.stockQuantity}</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                ${material.stockQuantity}
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>${material.campusName}</td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${material.stockQuantity <= 10}">
                                                                <span class="badge badge-danger">Low Stock</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge badge-success">In Stock</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <div class="action-buttons">
                                                            <a href="${pageContext.request.contextPath}/materials?action=view&id=${material.prodId}" 
                                                               class="btn btn-primary" style="padding: 5px 12px; font-size: 13px;">
                                                                <i class="fas fa-eye"></i>
                                                            </a>
                                                            <c:if test="${currentUser.role == 'STOREKEEPER'}">
                                                                <a href="${pageContext.request.contextPath}/materials?action=edit&id=${material.prodId}" 
                                                                   class="btn btn-success" style="padding: 5px 12px; font-size: 13px;">
                                                                    <i class="fas fa-edit"></i>
                                                                </a>
                                                                <form action="${pageContext.request.contextPath}/materials" method="post" style="display: inline;">
                                                                    <input type="hidden" name="action" value="delete">
                                                                    <input type="hidden" name="prodId" value="${material.prodId}">
                                                                    <button type="submit" class="btn btn-danger" style="padding: 5px 12px; font-size: 13px;" 
                                                                            onclick="return confirm('Are you sure you want to delete ${material.name}?')">
                                                                        <i class="fas fa-trash"></i>
                                                                    </button>
                                                                </form>
                                                            </c:if>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr>
                                                <td colspan="7" style="text-align: center; padding: 40px;">
                                                    <i class="fas fa-box-open" style="font-size: 48px; color: #94A3B8;"></i>
                                                    <p style="margin-top: 15px; color: #94A3B8;">No materials found</p>
                                                </td>
                                            </tr>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </c:if>
            
            <!-- ============================================ -->
            <!-- ADD VIEW -->
            <!-- ============================================ -->
            <c:if test="${viewMode == 'add'}">
                <div class="page-section active">
                    <div class="page-header">
                        <div class="page-header-text">
                            <h1>Add New Material</h1>
                            <p>Add a new cleaning material to inventory</p>
                        </div>
                        <div class="page-actions">
                            <a href="${pageContext.request.contextPath}/materials" class="btn" style="background: #E2E8F0; color: #475569;">
                                <i class="fas fa-arrow-left"></i> Back to List
                            </a>
                        </div>
                    </div>
                    
                    <div class="card" style="padding: 30px; max-width: 800px;">
                        <form action="${pageContext.request.contextPath}/materials" method="post">
                            <input type="hidden" name="action" value="create">
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="name">Material Name <span style="color: #DC2626;">*</span></label>
                                    <input type="text" id="name" name="name" value="${material.name}" required 
                                           placeholder="Enter material name">
                                </div>
                                
                                <div class="form-group">
                                    <label for="busId">Supplier <span style="color: #DC2626;">*</span></label>
                                    <select id="busId" name="busId" required>
                                        <option value="">Select Supplier</option>
                                        <c:forEach items="${suppliers}" var="supplier">
                                            <option value="${supplier.busId}" ${material.busId == supplier.busId ? 'selected' : ''}>
                                                ${supplier.name}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                
                                <div class="form-group">
                                    <label for="price">Price (ZAR) <span style="color: #DC2626;">*</span></label>
                                    <input type="number" step="0.01" id="price" name="price" value="${material.price}" required 
                                           placeholder="0.00" min="0">
                                </div>
                                
                                <div class="form-group">
                                    <label for="stockQuantity">Stock Quantity <span style="color: #DC2626;">*</span></label>
                                    <input type="number" id="stockQuantity" name="stockQuantity" value="${material.stockQuantity}" required 
                                           min="0" placeholder="0">
                                </div>
                                
                                <div class="form-group">
                                    <label for="campId">Campus <span style="color: #DC2626;">*</span></label>
                                    <select id="campId" name="campId" required>
                                        <option value="">Select Campus</option>
                                        <c:forEach items="${campuses}" var="campus">
                                            <option value="${campus.campId}" ${material.campId == campus.campId ? 'selected' : ''}>
                                                ${campus.name}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <label for="description">Description</label>
                                <textarea id="description" name="description" rows="3" 
                                          placeholder="Enter description (optional)">${material.description}</textarea>
                            </div>
                            
                            <div style="display: flex; gap: 15px; margin-top: 30px;">
                                <button type="submit" class="btn btn-primary" style="flex: 1;">
                                    <i class="fas fa-save"></i> Add Material
                                </button>
                                <a href="${pageContext.request.contextPath}/materials" class="btn" style="background: #E2E8F0; color: #475569; flex: 1; text-align: center; padding: 10px 20px;">
                                    Cancel
                                </a>
                            </div>
                        </form>
                    </div>
                </div>
            </c:if>
            
            <!-- ============================================ -->
            <!-- EDIT VIEW -->
            <!-- ============================================ -->
            <c:if test="${viewMode == 'edit'}">
                <div class="page-section active">
                    <div class="page-header">
                        <div class="page-header-text">
                            <h1>Edit Material</h1>
                            <p>Update material details</p>
                        </div>
                        <div class="page-actions">
                            <a href="${pageContext.request.contextPath}/materials" class="btn" style="background: #E2E8F0; color: #475569;">
                                <i class="fas fa-arrow-left"></i> Back to List
                            </a>
                        </div>
                    </div>
                    
                    <div class="card" style="padding: 30px; max-width: 800px;">
                        <form action="${pageContext.request.contextPath}/materials" method="post">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="prodId" value="${material.prodId}">
                            
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="name">Material Name <span style="color: #DC2626;">*</span></label>
                                    <input type="text" id="name" name="name" value="${material.name}" required 
                                           placeholder="Enter material name">
                                </div>
                                
                                <div class="form-group">
                                    <label for="busId">Supplier <span style="color: #DC2626;">*</span></label>
                                    <select id="busId" name="busId" required>
                                        <option value="">Select Supplier</option>
                                        <c:forEach items="${suppliers}" var="supplier">
                                            <option value="${supplier.busId}" ${material.busId == supplier.busId ? 'selected' : ''}>
                                                ${supplier.name}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                
                                <div class="form-group">
                                    <label for="price">Price (ZAR) <span style="color: #DC2626;">*</span></label>
                                    <input type="number" step="0.01" id="price" name="price" value="${material.price}" required 
                                           placeholder="0.00" min="0">
                                </div>
                                
                                <div class="form-group">
                                    <label for="stockQuantity">Stock Quantity <span style="color: #DC2626;">*</span></label>
                                    <input type="number" id="stockQuantity" name="stockQuantity" value="${material.stockQuantity}" required 
                                           min="0" placeholder="0">
                                </div>
                                
                                <div class="form-group">
                                    <label for="campId">Campus <span style="color: #DC2626;">*</span></label>
                                    <select id="campId" name="campId" required>
                                        <option value="">Select Campus</option>
                                        <c:forEach items="${campuses}" var="campus">
                                            <option value="${campus.campId}" ${material.campId == campus.campId ? 'selected' : ''}>
                                                ${campus.name}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <label for="description">Description</label>
                                <textarea id="description" name="description" rows="3" 
                                          placeholder="Enter description (optional)">${material.description}</textarea>
                            </div>
                            
                            <div style="display: flex; gap: 15px; margin-top: 30px;">
                                <button type="submit" class="btn btn-primary" style="flex: 1;">
                                    <i class="fas fa-save"></i> Update Material
                                </button>
                                <a href="${pageContext.request.contextPath}/materials" class="btn" style="background: #E2E8F0; color: #475569; flex: 1; text-align: center; padding: 10px 20px;">
                                    Cancel
                                </a>
                            </div>
                        </form>
                    </div>
                </div>
            </c:if>
            
            <!-- ============================================ -->
            <!-- VIEW DETAILS -->
            <!-- ============================================ -->
            <c:if test="${viewMode == 'view'}">
                <div class="page-section active">
                    <div class="page-header">
                        <div class="page-header-text">
                            <h1>Material Details</h1>
                            <p>View complete information for this material</p>
                        </div>
                        <div class="page-actions">
                            <a href="${pageContext.request.contextPath}/materials" class="btn" style="background: #E2E8F0; color: #475569;">
                                <i class="fas fa-arrow-left"></i> Back to List
                            </a>
                        </div>
                    </div>
                    
                    <div class="card" style="padding: 30px;">
                        <div class="view-details">
                            <div class="view-item">
                                <label>Material Name</label>
                                <p>${material.name}</p>
                            </div>
                            <div class="view-item">
                                <label>Status</label>
                                <p>
                                    <c:choose>
                                        <c:when test="${material.stockQuantity <= 10}">
                                            <span class="status-badge low-stock">Low Stock</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-badge in-stock">In Stock</span>
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                            <div class="view-item">
                                <label>Supplier</label>
                                <p>${material.supplierName}</p>
                            </div>
                            <div class="view-item">
                                <label>Price</label>
                                <p>
                                    <fmt:formatNumber value="${material.price}" type="currency" currencySymbol="R" maxFractionDigits="2" minFractionDigits="2"/>
                                </p>
                            </div>
                            <div class="view-item">
                                <label>Stock Quantity</label>
                                <p style="color: ${material.stockQuantity <= 10 ? '#DC2626' : '#0F172A'}">${material.stockQuantity}</p>
                            </div>
                            <div class="view-item">
                                <label>Campus</label>
                                <p>${material.campusName}</p>
                            </div>
                            <div class="view-item" style="grid-column: span 2;">
                                <label>Description</label>
                                <p>${material.description != null ? material.description : 'No description provided'}</p>
                            </div>
                        </div>
                        
                        <div style="display: flex; gap: 15px; margin-top: 30px; padding-top: 30px; border-top: 1px solid #E2E8F0;">
                            <a href="${pageContext.request.contextPath}/materials?action=edit&id=${material.prodId}" class="btn btn-primary">
                                <i class="fas fa-edit"></i> Edit Material
                            </a>
                            <form action="${pageContext.request.contextPath}/materials" method="post" style="display: inline;">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="prodId" value="${material.prodId}">
                                <button type="submit" class="btn btn-danger" onclick="return confirm('Are you sure you want to delete ${material.name}?')">
                                    <i class="fas fa-trash"></i> Delete Material
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </c:if>
            
        </main>
    </div>
</body>
</html>