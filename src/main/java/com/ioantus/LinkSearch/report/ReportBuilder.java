package com.ioantus.LinkSearch.report;

import com.ioantus.LinkSearch.DTO.ScanResultDTO;
import com.ioantus.LinkSearch.DTO.SinglePageDTO;
import com.ioantus.LinkSearch.config.AppConstants;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class ReportBuilder {

    public static OutputStream createReport(ScanResultDTO resultDTO, OutputStream os) throws IOException {
        final Workbook book = fillReport(resultDTO);
        book.write(os);
        return os;
    }

    private static Workbook fillReport(ScanResultDTO resultDTO){
        final Workbook book = new XSSFWorkbook();

        final Sheet sheet = book.createSheet("Scan result");
        sheet.setColumnWidth(0, 50000);
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 5000);

        final Font titleFont = book.createFont();
        titleFont.setFontName("Arial");
        titleFont.setFontHeightInPoints((short) 14);
        titleFont.setBold(true);

        final Font columnHeaderFont = book.createFont();
        columnHeaderFont.setFontName("Arial");
        columnHeaderFont.setFontHeightInPoints((short) 12);
        columnHeaderFont.setBold(true);

        final Font tableFont = book.createFont();
        tableFont.setFontName("Arial");
        tableFont.setFontHeightInPoints((short) 12);

        final CellStyle titleStyle = book.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.LEFT);
        titleStyle.setFont(titleFont);

        final CellStyle columnHeaderStyle = book.createCellStyle();
        columnHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
        columnHeaderStyle.setFont(columnHeaderFont);
        columnHeaderStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        columnHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        columnHeaderStyle.setBorderBottom(BorderStyle.THICK);
        columnHeaderStyle.setBorderLeft(BorderStyle.THICK);
        columnHeaderStyle.setBorderRight(BorderStyle.THICK);
        columnHeaderStyle.setBorderTop(BorderStyle.THICK);

        final CellStyle tableLeftStyle = book.createCellStyle();
        tableLeftStyle.setAlignment(HorizontalAlignment.LEFT);
        tableLeftStyle.setFont(tableFont);
        tableLeftStyle.setBorderLeft(BorderStyle.MEDIUM);
        tableLeftStyle.setBorderRight(BorderStyle.MEDIUM);

        final CellStyle tableCenterStyle = book.createCellStyle();
        tableCenterStyle.setAlignment(HorizontalAlignment.CENTER);
        tableCenterStyle.setFont(tableFont);
        tableCenterStyle.setBorderLeft(BorderStyle.MEDIUM);
        tableCenterStyle.setBorderRight(BorderStyle.MEDIUM);

        final CellStyle tableBottomStyle = book.createCellStyle();
        tableBottomStyle.setBorderTop(BorderStyle.MEDIUM);

        // Заголовок
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        String title = String.format("Start scanning domain %s at %s, found %s pages with %s external links, finished at %s",
                resultDTO.getDomain(),
                AppConstants.DF.format(resultDTO.getStartTime()),
                resultDTO.getResultSet().size(),
                resultDTO.getExternalPagesCount(),
                AppConstants.DF.format(resultDTO.getEndTime())
        );
        titleCell.setCellValue(title);
        titleCell.setCellStyle(titleStyle);
        // Шапка таблицы
        Row columnHeader = sheet.createRow(1);
        Cell headerUrlCell = columnHeader.createCell(0);
        headerUrlCell.setCellValue("Internal url");
        headerUrlCell.setCellStyle(columnHeaderStyle);
        Cell headerLevelCell = columnHeader.createCell(1);
        headerLevelCell.setCellValue("Level");
        headerLevelCell.setCellStyle(columnHeaderStyle);
        Cell headerExtCountCell = columnHeader.createCell(2);
        headerExtCountCell.setCellValue("Ext.count");
        headerExtCountCell.setCellStyle(columnHeaderStyle);
        // Тело таблицы
        int i = 2;
        for (SinglePageDTO pageDTO:  resultDTO.getResultSet()){
            Row tableRow = sheet.createRow(i);

            Cell urlCell = tableRow.createCell(0);
            urlCell.setCellValue(pageDTO.getInnerUrl().toString());
            urlCell.setCellStyle(tableLeftStyle);

            Cell levelCell = tableRow.createCell(1);
            levelCell.setCellValue(pageDTO.getLevel().toString());
            levelCell.setCellStyle(tableCenterStyle);

            Cell extCountCell = tableRow.createCell(2);
            extCountCell.setCellValue(pageDTO.getOuterLinksCount().toString());
            extCountCell.setCellStyle(tableCenterStyle);

            i++;
        }
        // Хвост таблицы
        Row tableBottomRow = sheet.createRow(i);
        Cell urlBottomCell = tableBottomRow.createCell(0);
        Cell levelBottomCell = tableBottomRow.createCell(1);
        Cell extBottomCountCell = tableBottomRow.createCell(2);
        urlBottomCell.setCellStyle(tableBottomStyle);
        levelBottomCell.setCellStyle(tableBottomStyle);
        extBottomCountCell.setCellStyle(tableBottomStyle);
        return book;
    }

}
