package za.bc.cleaninginventory.service.report;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import za.bc.cleaninginventory.model.dao.report.ReportDAO;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import za.bc.cleaninginventory.model.dto.ReportDTO;

import jakarta.servlet.ServletContext;
import za.bc.cleaninginventory.model.entity.Employee;

public class PdfExportService {

    private final ReportDAO reportDAO = new ReportDAO();

    public void exportReport(String reportType,
            OutputStream outputStream,
            ServletContext context,
            Employee currentUser) throws Exception {

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.setMargins(36, 36, 36, 36);

        FontProvider fontProvider = new FontProvider();
        fontProvider.addFont(
                getClass()
                        .getClassLoader()
                        .getResource("za/bc/cleaninginventory/fonts/GoogleSans-Regular.ttf")
                        .toExternalForm());

        fontProvider.addFont(
                getClass()
                        .getClassLoader()
                        .getResource("za/bc/cleaninginventory/fonts/GoogleSans-Bold.ttf")
                        .toExternalForm());

        ConverterProperties properties = new ConverterProperties();
        properties.setFontProvider(fontProvider);

        addHeader(document, context);

        addTitle(document, properties, reportType, currentUser.getName(), currentUser.getSurname());

        String tableHtml = buildTableHtml(reportType);

        List<IElement> table
                = HtmlConverter.convertToElements(tableHtml, properties);

        for (IElement element : table) {

            document.add((IBlockElement) element);

        }

        addSignature(document, currentUser.getName(), currentUser.getSurname(), currentUser.getRole());

        document.close();

    }

    private void addHeader(Document document,
            ServletContext context) throws Exception {

        String logoPath = getClass()
                .getClassLoader()
                .getResource("za/bc/cleaninginventory/assets/cims_logo_head.png")
                .toExternalForm();

        Image logo = new Image(
                ImageDataFactory.create(logoPath))
                .setWidth(120);

        document.add(logo);

    }

    private void addTitle(Document document,
            ConverterProperties properties,
            String reportType,
            String name, String surname) throws Exception {

        String title
                = reportType.substring(0, 1).toUpperCase()
                + reportType.substring(1)
                + " Report";

        String today
                = LocalDate.now().format(
                        DateTimeFormatter.ofPattern("dd MMMM yyyy"));

        String html
                = "<h1 style='font-family:\"Google Sans\",sans-serif;"
                + "font-size:22pt;font-weight:bold;margin-top:20px;'>"
                + title
                + "</h1>"
                + "<p style='font-family:\"Google Sans\",sans-serif;"
                + "font-size:10pt;'>"
                + "Prepared by: " + name + " " + surname
                + "<br>Date: "
                + today
                + "</p>";

        List<IElement> elements
                = HtmlConverter.convertToElements(html, properties);

        for (IElement element : elements) {

            document.add((IBlockElement) element);

        }

    }

    private String buildTableHtml(String reportType) {

        switch (reportType) {

            case "requests":
                return buildRequestsTable();

            case "orders":
                return buildOrdersTable();

            case "stock":
                return buildStockTable();

            case "suppliers":
                return buildSuppliersTable();

            default:
                return buildProductsTable();

        }

    }

    private void addSignature(Document document,
            String name, String surname, String role) throws Exception {

        Table table
                = new Table(UnitValue.createPercentArray(new float[]{100}))
                        .setWidth(100)
                        .setMarginTop(50)
                        .setMarginLeft(5);

        String signaturePath = getClass()
                .getClassLoader()
                .getResource("za/bc/cleaninginventory/assets/signature_1.png")
                .toExternalForm();

        Image signature = new Image(
                ImageDataFactory.create(signaturePath))
                .setWidth(80);

        DeviceRgb grey = new DeviceRgb(220, 220, 220);

        SolidLine line = new SolidLine(0.7f);

        line.setColor(grey);

        LineSeparator separator
                = new LineSeparator(line)
                        .setMarginTop(-8)
                        .setMarginBottom(8);

        Paragraph text
                = new Paragraph(name + " " + surname + "\n" + role)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(8.5f);

        Cell cell
                = new Cell()
                        .add(signature)
                        .add(separator)
                        .add(text);

        cell.setBorder(null);

        table.addCell(cell);

        document.add(table);

    }

    private String buildProductsTable() {

        List<ReportDTO> products = reportDAO.getProductsReport();

        StringBuilder html = new StringBuilder();

        html.append("""
        <h3 style="
            font-family:'Google Sans',sans-serif;
            margin-top:15px;
            margin-bottom:10px;
            font-size:14pt;
            font-weight:bold;">
            Products
        </h3>
        """);

        html.append("""
        <table style="
            width:100%;
            border-collapse:collapse;
            font-family:'Google Sans',sans-serif;
            font-size:10pt;">
        """);

        html.append("""
        <tr style="
            background-color:#F8FAFC;
            color:#64748B;
            font-weight:bold;
            text-align:left;">
            <th style="padding:8px;border:1px solid #ddd;">ID</th>
            <th style="padding:8px;border:1px solid #ddd;">Product</th>
            <th style="padding:8px;border:1px solid #ddd;">Supplier</th>
            <th style="padding:8px;border:1px solid #ddd;">Price</th>
            <th style="padding:8px;border:1px solid #ddd;">Stock</th>
            <th style="padding:8px;border:1px solid #ddd;">Status</th>
        </tr>
        """);

        for (ReportDTO product : products) {

            String status;

            if (product.getStock() <= 2) {
                status = "Critical";
            } else if (product.getStock() <= 10) {
                status = "Low Stock";
            } else {
                status = "Available";
            }

            html.append("<tr>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(product.getProductId())
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(product.getProductName())
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(product.getSupplier())
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>R ")
                    .append(String.format("%.2f", product.getPrice()))
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(product.getStock())
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(status)
                    .append("</td>");

            html.append("</tr>");

        }

        html.append("</table>");

        return html.toString();

    }

    private String buildRequestsTable() {

        List<ReportDTO> requests = reportDAO.getRequestsReport();

        StringBuilder html = new StringBuilder();

        html.append("""
        <h3 style="
            font-family:'Google Sans',sans-serif;
            margin-top:15px;
            margin-bottom:10px;
            font-size:14pt;
            font-weight:bold;">
            Requests
        </h3>
        """);

        html.append("""
        <table style="
            width:100%;
            border-collapse:collapse;
            font-family:'Google Sans',sans-serif;
            font-size:10pt;">
        """);

        html.append("""
        <tr style="
            background-color:#F8FAFC;
            color:#64748B;
            font-weight:bold;
            text-align:left;">
            <th style="padding:8px;border:1px solid #ddd;">Employee</th>
            <th style="padding:8px;border:1px solid #ddd;">Product</th>
            <th style="padding:8px;border:1px solid #ddd;">Quantity</th>
            <th style="padding:8px;border:1px solid #ddd;">Priority</th>
            <th style="padding:8px;border:1px solid #ddd;">Status</th>
            <th style="padding:8px;border:1px solid #ddd;">Date</th>
        </tr>
        """);

        for (ReportDTO request : requests) {

            html.append("<tr>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(request.getEmployee())
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(request.getProductName())
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(request.getQuantity())
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(request.getPriority())
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(request.getStatus())
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(request.getRequestDate())
                    .append("</td>");

            html.append("</tr>");

        }

        html.append("</table>");

        return html.toString();

    }

    private String buildOrdersTable() {

        List<ReportDTO> orders = reportDAO.getOrdersReport();

        StringBuilder html = new StringBuilder();

        html.append("""
        <h3 style="
            font-family:'Google Sans',sans-serif;
            margin-top:15px;
            margin-bottom:10px;
            font-size:14pt;
            font-weight:bold;">
            Orders
        </h3>
        """);

        html.append("""
        <table style="
            width:100%;
            border-collapse:collapse;
            font-family:'Google Sans',sans-serif;
            font-size:10pt;">
        """);

        html.append("""
        <tr style="
            background-color:#F8FAFC;
            color:#64748B;
            font-weight:bold;
            text-align:left;">
            <th style="padding:8px;border:1px solid #ddd;">Order ID</th>
            <th style="padding:8px;border:1px solid #ddd;">Date</th>
            <th style="padding:8px;border:1px solid #ddd;">Product</th>
            <th style="padding:8px;border:1px solid #ddd;">Quantity</th>
            <th style="padding:8px;border:1px solid #ddd;">Total</th>
        </tr>
        """);

        for (ReportDTO order : orders) {

            html.append("<tr>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(order.getOrderId())
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(order.getRequestDate())
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(order.getProductName())
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(order.getQuantity())
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>R ")
                    .append(String.format("%.2f", order.getTotal()))
                    .append("</td>");

            html.append("</tr>");

        }

        html.append("</table>");

        return html.toString();

    }

    private String buildStockTable() {

        List<ReportDTO> stock = reportDAO.getStockReport();

        StringBuilder html = new StringBuilder();

        html.append("""
        <h3 style="
            font-family:'Google Sans',sans-serif;
            margin-top:15px;
            margin-bottom:10px;
            font-size:14pt;
            font-weight:bold;">
            Stock
        </h3>
        """);

        html.append("""
        <table style="
            width:100%;
            border-collapse:collapse;
            font-family:'Google Sans',sans-serif;
            font-size:10pt;">
        """);

        html.append("""
        <tr style="
            background-color:#F8FAFC;
            color:#64748B;
            font-weight:bold;
            text-align:left;">
            <th style="padding:8px;border:1px solid #ddd;">Product</th>
            <th style="padding:8px;border:1px solid #ddd;">Campus</th>
            <th style="padding:8px;border:1px solid #ddd;">Stock</th>
            <th style="padding:8px;border:1px solid #ddd;">Status</th>
        </tr>
        """);

        for (ReportDTO item : stock) {

            String status;

            if (item.getStock() <= 2) {
                status = "Critical";
            } else if (item.getStock() <= 10) {
                status = "Low Stock";
            } else {
                status = "Available";
            }

            html.append("<tr>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(item.getProductName())
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(item.getCampus())
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(item.getStock())
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(status)
                    .append("</td>");

            html.append("</tr>");

        }

        html.append("</table>");

        return html.toString();

    }

    private String buildSuppliersTable() {

        List<ReportDTO> suppliers = reportDAO.getSuppliersReport();

        StringBuilder html = new StringBuilder();

        html.append("""
        <h3 style="
            font-family:'Google Sans',sans-serif;
            margin-top:15px;
            margin-bottom:10px;
            font-size:14pt;
            font-weight:bold;">
            Suppliers
        </h3>
        """);

        html.append("""
        <table style="
            width:100%;
            border-collapse:collapse;
            font-family:'Google Sans',sans-serif;
            font-size:10pt;">
        """);

        html.append("""
        <tr style="
            background-color:#F8FAFC;
            color:#64748B;
            font-weight:bold;
            text-align:left;">
            <th style="padding:8px;border:1px solid #ddd;">Supplier</th>
            <th style="padding:8px;border:1px solid #ddd;">Description</th>
            <th style="padding:8px;border:1px solid #ddd;">City</th>
            <th style="padding:8px;border:1px solid #ddd;">Province</th>
        </tr>
        """);

        for (ReportDTO supplier : suppliers) {

            html.append("<tr>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(supplier.getSupplier())
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(supplier.getSupplierDescription())
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(supplier.getCity())
                    .append("</td>");

            html.append("<td style='padding:8px;border:1px solid #ddd;'>")
                    .append(supplier.getProvince())
                    .append("</td>");

            html.append("</tr>");

        }

        html.append("</table>");

        return html.toString();

    }

}
