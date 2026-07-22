package za.bc.cleaninginventory.service.report;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import za.bc.cleaninginventory.model.dao.report.ReportDAO;
import za.bc.cleaninginventory.model.dto.ReportDTO;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ExcelExportService {

    private final ReportDAO reportDAO = new ReportDAO();

    public void exportReport(String reportType,
            OutputStream outputStream) throws IOException {

        Workbook workbook = new XSSFWorkbook();

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);

        switch (reportType.toLowerCase()) {

            case "products":
                createProductsSheet(workbook, headerStyle, currencyStyle);
                break;

            case "requests":
                createRequestsSheet(workbook, headerStyle);
                break;

            case "orders":
                createOrdersSheet(workbook, headerStyle, currencyStyle);
                break;

            case "stock":
                createStockSheet(workbook, headerStyle);
                break;

            case "suppliers":
                createSuppliersSheet(workbook, headerStyle);
                break;

            default:
                createProductsSheet(workbook, headerStyle, currencyStyle);

        }

        workbook.write(outputStream);
        workbook.close();
    }

    private CellStyle createHeaderStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();

        Font font = workbook.createFont();

        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());

        style.setFont(font);

        style.setFillForegroundColor(
                IndexedColors.DARK_BLUE.getIndex());

        style.setFillPattern(
                FillPatternType.SOLID_FOREGROUND);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();

        DataFormat format = workbook.createDataFormat();

        style.setDataFormat(format.getFormat("R#,##0.00"));

        return style;
    }

    private void createProductsSheet(
            Workbook workbook,
            CellStyle headerStyle,
            CellStyle currencyStyle) {

        Sheet sheet = workbook.createSheet("Products");

        String[] headers = {
            "ID",
            "Product",
            "Supplier",
            "Price",
            "Stock",
            "Status"
        };

        Row header = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {

            Cell cell = header.createCell(i);

            cell.setCellValue(headers[i]);

            cell.setCellStyle(headerStyle);

        }

        List<ReportDTO> products
                = reportDAO.getProductsReport();

        int rowNum = 1;

        for (ReportDTO p : products) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(
                    p.getProductId());

            row.createCell(1).setCellValue(
                    p.getProductName());

            row.createCell(2).setCellValue(
                    p.getSupplier());

            Cell price
                    = row.createCell(3);

            price.setCellValue(
                    p.getPrice());

            price.setCellStyle(currencyStyle);

            row.createCell(4).setCellValue(
                    p.getStock());

            String status;

            if (p.getStock() <= 2) {
                status = "Critical";
            } else if (p.getStock() <= 10) {
                status = "Low Stock";
            } else {
                status = "Available";
            }

            row.createCell(5).setCellValue(status);

        }

        for (int i = 0; i < headers.length; i++) {

            sheet.autoSizeColumn(i);

        }

    }

    private void createRequestsSheet(
            Workbook workbook,
            CellStyle headerStyle) {

        Sheet sheet = workbook.createSheet("Requests");

        String[] headers = {
            "Employee",
            "Product",
            "Quantity",
            "Status",
            "Priority",
            "Date"
        };

        Row header = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {

            Cell cell = header.createCell(i);

            cell.setCellValue(headers[i]);

            cell.setCellStyle(headerStyle);

        }

        List<ReportDTO> requests
                = reportDAO.getRequestsReport();

        int rowNum = 1;

        for (ReportDTO r : requests) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(
                    r.getEmployee());

            row.createCell(1).setCellValue(
                    r.getProductName());

            row.createCell(2).setCellValue(
                    r.getQuantity());

            row.createCell(3).setCellValue(
                    r.getStatus());

            row.createCell(4).setCellValue(
                    r.getPriority());

            row.createCell(5).setCellValue(
                    r.getRequestDate());

        }

        for (int i = 0; i < headers.length; i++) {

            sheet.autoSizeColumn(i);

        }

    }

    private void createOrdersSheet(
            Workbook workbook,
            CellStyle headerStyle,
            CellStyle currencyStyle) {

        Sheet sheet = workbook.createSheet("Orders");

        String[] headers = {
            "Order ID",
            "Date",
            "Product",
            "Quantity",
            "Total"
        };

        Row header = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {

            Cell cell = header.createCell(i);

            cell.setCellValue(headers[i]);

            cell.setCellStyle(headerStyle);

        }

        List<ReportDTO> orders = reportDAO.getOrdersReport();

        int rowNum = 1;

        for (ReportDTO order : orders) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(order.getOrderId());

            row.createCell(1).setCellValue(order.getRequestDate());

            row.createCell(2).setCellValue(order.getProductName());

            row.createCell(3).setCellValue(order.getQuantity());

            Cell totalCell = row.createCell(4);

            totalCell.setCellValue(order.getTotal());

            totalCell.setCellStyle(currencyStyle);

        }

        for (int i = 0; i < headers.length; i++) {

            sheet.autoSizeColumn(i);

        }

    }

    private void createStockSheet(
            Workbook workbook,
            CellStyle headerStyle) {

        Sheet sheet = workbook.createSheet("Stock");

        String[] headers = {
            "Product",
            "Campus",
            "Stock",
            "Status"
        };

        Row header = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {

            Cell cell = header.createCell(i);

            cell.setCellValue(headers[i]);

            cell.setCellStyle(headerStyle);

        }

        List<ReportDTO> stock = reportDAO.getStockReport();

        int rowNum = 1;

        for (ReportDTO item : stock) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(item.getProductName());

            row.createCell(1).setCellValue(item.getCampus());

            row.createCell(2).setCellValue(item.getStock());

            String status;

            if (item.getStock() <= 2) {
                status = "Critical";
            } else if (item.getStock() <= 10) {
                status = "Low Stock";
            } else {
                status = "Available";
            }

            row.createCell(3).setCellValue(status);

        }

        for (int i = 0; i < headers.length; i++) {

            sheet.autoSizeColumn(i);

        }

    }

    private void createSuppliersSheet(
            Workbook workbook,
            CellStyle headerStyle) {

        Sheet sheet = workbook.createSheet("Suppliers");

        String[] headers = {
            "Supplier",
            "Description",
            "City",
            "Province"
        };

        Row header = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {

            Cell cell = header.createCell(i);

            cell.setCellValue(headers[i]);

            cell.setCellStyle(headerStyle);

        }

        List<ReportDTO> suppliers = reportDAO.getSuppliersReport();

        int rowNum = 1;

        for (ReportDTO supplier : suppliers) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(supplier.getSupplier());

            row.createCell(1).setCellValue(supplier.getSupplierDescription());

            row.createCell(2).setCellValue(supplier.getCity());

            row.createCell(3).setCellValue(supplier.getProvince());

        }

        for (int i = 0; i < headers.length; i++) {

            sheet.autoSizeColumn(i);

        }

    }
}
