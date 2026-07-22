package za.bc.cleaninginventory.controller.report;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import za.bc.cleaninginventory.service.report.PdfExportService;

import java.io.IOException;

@WebServlet("/exportPdf")
public class ExportPdfServlet extends HttpServlet {

    private final PdfExportService pdfService
            = new PdfExportService();

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String reportType = request.getParameter("type");

        if (reportType == null || reportType.isBlank()) {
            reportType = "products";
        }

        response.setContentType("application/pdf");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"" + reportType + "_report.pdf\"");

        try {

            pdfService.exportReport(
                    reportType,
                    response.getOutputStream(),
                    getServletContext());

        } catch (Exception ex) {

            throw new ServletException(ex);

        }

    }

}
