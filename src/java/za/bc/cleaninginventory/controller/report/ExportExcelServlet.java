package za.bc.cleaninginventory.controller.report;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import za.bc.cleaninginventory.service.report.ExcelExportService;

import java.io.IOException;

@WebServlet("/exportExcel")
public class ExportExcelServlet extends HttpServlet {

    private final ExcelExportService excelService =
            new ExcelExportService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String reportType = request.getParameter("type");

        if(reportType == null || reportType.isBlank()){

            reportType = "products";

        }

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"" + reportType + "_report.xlsx\"");

        excelService.exportReport(
                reportType,
                response.getOutputStream());

    }

}