package com.hcms.payroll.service;

import com.hcms.payroll.dto.PayrollResponse;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

@Service
@Slf4j
public class PdfService {

    public byte[] generatePayslip(PayrollResponse payroll) {
        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("PAYSLIP", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Company Info
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Paragraph companyInfo = new Paragraph("Human Capital Management System", headerFont);
            companyInfo.setAlignment(Element.ALIGN_CENTER);
            companyInfo.setSpacingAfter(10);
            document.add(companyInfo);

            // Pay Period
            Paragraph period = new Paragraph(
                    String.format("Pay Period: %02d/%d", payroll.getPayPeriodMonth(), payroll.getPayPeriodYear()),
                    FontFactory.getFont(FontFactory.HELVETICA, 10));
            period.setAlignment(Element.ALIGN_CENTER);
            period.setSpacingAfter(20);
            document.add(period);

            // Employee Info Table
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1, 2});

            addCell(infoTable, "Employee ID:", payroll.getEmployeeId().toString());
            addCell(infoTable, "Payment Date:", payroll.getPaymentDate().toString());
            addCell(infoTable, "Status:", payroll.getStatus().toString());

            document.add(infoTable);
            document.add(Chunk.NEWLINE);

            // Earnings Table
            PdfPTable earningsTable = new PdfPTable(2);
            earningsTable.setWidthPercentage(100);
            earningsTable.setWidths(new float[]{3, 1});

            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            PdfPCell headerCell = new PdfPCell(new Phrase("EARNINGS", tableHeaderFont));
            headerCell.setColspan(2);
            headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            headerCell.setPadding(5);
            earningsTable.addCell(headerCell);

            addRow(earningsTable, "Base Salary", payroll.getBaseSalary());
            addRow(earningsTable, "Allowances", payroll.getAllowances());
            addRow(earningsTable, "Bonus", payroll.getBonus());
            addRow(earningsTable, "Overtime", payroll.getOvertime());

            PdfPCell grossCell = new PdfPCell(new Phrase("Gross Salary", tableHeaderFont));
            grossCell.setPadding(5);
            earningsTable.addCell(grossCell);
            PdfPCell grossValue = new PdfPCell(new Phrase(formatCurrency(payroll.getGrossSalary()), tableHeaderFont));
            grossValue.setPadding(5);
            earningsTable.addCell(grossValue);

            document.add(earningsTable);
            document.add(Chunk.NEWLINE);

            // Deductions Table
            PdfPTable deductionsTable = new PdfPTable(2);
            deductionsTable.setWidthPercentage(100);
            deductionsTable.setWidths(new float[]{3, 1});

            PdfPCell dedHeaderCell = new PdfPCell(new Phrase("DEDUCTIONS", tableHeaderFont));
            dedHeaderCell.setColspan(2);
            dedHeaderCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            dedHeaderCell.setPadding(5);
            deductionsTable.addCell(dedHeaderCell);

            addRow(deductionsTable, "Tax Deduction", payroll.getTaxDeduction());
            addRow(deductionsTable, "Provident Fund", payroll.getProvidentFund());
            addRow(deductionsTable, "Other Deductions", payroll.getOtherDeductions());

            PdfPCell totalDedCell = new PdfPCell(new Phrase("Total Deductions", tableHeaderFont));
            totalDedCell.setPadding(5);
            deductionsTable.addCell(totalDedCell);
            PdfPCell totalDedValue = new PdfPCell(new Phrase(formatCurrency(payroll.getTotalDeductions()), tableHeaderFont));
            totalDedValue.setPadding(5);
            deductionsTable.addCell(totalDedValue);

            document.add(deductionsTable);
            document.add(Chunk.NEWLINE);

            // Net Salary
            PdfPTable netTable = new PdfPTable(2);
            netTable.setWidthPercentage(100);
            netTable.setWidths(new float[]{3, 1});

            Font netFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            PdfPCell netLabel = new PdfPCell(new Phrase("NET SALARY", netFont));
            netLabel.setPadding(5);
            netTable.addCell(netLabel);
            PdfPCell netValue = new PdfPCell(new Phrase(formatCurrency(payroll.getNetSalary()), netFont));
            netValue.setPadding(5);
            netTable.addCell(netValue);

            document.add(netTable);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF: ", e);
            throw new RuntimeException("Failed to generate payslip PDF", e);
        }
    }

    private void addCell(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        labelCell.setPadding(5);
        table.addCell(labelCell);
        PdfPCell valueCell = new PdfPCell(new Phrase(value, FontFactory.getFont(FontFactory.HELVETICA, 10)));
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    private void addRow(PdfPTable table, String label, BigDecimal amount) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, FontFactory.getFont(FontFactory.HELVETICA, 10)));
        labelCell.setPadding(5);
        table.addCell(labelCell);
        PdfPCell amountCell = new PdfPCell(new Phrase(formatCurrency(amount), FontFactory.getFont(FontFactory.HELVETICA, 10)));
        amountCell.setPadding(5);
        table.addCell(amountCell);
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "$0.00";
        }
        return "$" + amount.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }
}

