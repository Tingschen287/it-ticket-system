package com.itticket.service;

import com.itticket.entity.Ticket;
import com.itticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private final TicketRepository ticketRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public byte[] exportTicketsToExcel(List<Ticket.Status> statuses, Ticket.Priority priority,
                                        Ticket.TicketType type, Long departmentId) throws IOException {
        List<Ticket> tickets = filterTickets(statuses, priority, type, departmentId);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Tickets");

            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);

            // Create headers
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "工单编号", "标题", "类型", "优先级", "状态",
                    "报告人", "处理人", "部门", "创建时间", "更新时间", "解决时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            // Create date style
            CellStyle dateStyle = workbook.createCellStyle();
            CreationHelper creationHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm"));

            // Fill data
            int rowNum = 1;
            for (Ticket ticket : tickets) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(ticket.getId());
                row.createCell(1).setCellValue(ticket.getTicketNo());
                row.createCell(2).setCellValue(ticket.getTitle());
                row.createCell(3).setCellValue(ticket.getType() != null ? ticket.getType().name() : "");
                row.createCell(4).setCellValue(ticket.getPriority() != null ? ticket.getPriority().name() : "");
                row.createCell(5).setCellValue(ticket.getStatus() != null ? ticket.getStatus().name() : "");
                row.createCell(6).setCellValue(ticket.getReporter() != null ? ticket.getReporter().getFullName() : "");
                row.createCell(7).setCellValue(ticket.getAssignee() != null ? ticket.getAssignee().getFullName() : "");
                row.createCell(8).setCellValue(ticket.getDepartment() != null ? ticket.getDepartment().getName() : "");

                if (ticket.getCreatedAt() != null) {
                    row.createCell(9).setCellValue(ticket.getCreatedAt().format(DATE_FORMATTER));
                } else {
                    row.createCell(9).setCellValue("");
                }

                if (ticket.getUpdatedAt() != null) {
                    row.createCell(10).setCellValue(ticket.getUpdatedAt().format(DATE_FORMATTER));
                } else {
                    row.createCell(10).setCellValue("");
                }

                if (ticket.getResolvedAt() != null) {
                    row.createCell(11).setCellValue(ticket.getResolvedAt().format(DATE_FORMATTER));
                } else {
                    row.createCell(11).setCellValue("");
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private List<Ticket> filterTickets(List<Ticket.Status> statuses, Ticket.Priority priority,
                                        Ticket.TicketType type, Long departmentId) {
        List<Ticket> allTickets = ticketRepository.findAll();

        return allTickets.stream()
                .filter(t -> statuses == null || statuses.isEmpty() || statuses.contains(t.getStatus()))
                .filter(t -> priority == null || priority == t.getPriority())
                .filter(t -> type == null || type == t.getType())
                .filter(t -> departmentId == null ||
                        (t.getDepartment() != null && t.getDepartment().getId().equals(departmentId)))
                .toList();
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        return style;
    }
}
