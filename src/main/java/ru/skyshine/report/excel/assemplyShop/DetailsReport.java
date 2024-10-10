package ru.skyshine.report.excel.assemplyShop;

import org.apache.poi.ss.usermodel.*;
import ru.skyshine.report.excel.ConfigReport;

import java.util.List;

public class DetailsReport extends ConfigReport {
    private final List<Object[]> data;

    public DetailsReport(String nameSheet, List<Object[]> data) {
        super(nameSheet != null ? nameSheet : "Детали", "Details.xls");
        this.data = data;
    }

    public boolean createReport() {
        try {
            setHeaderReport();
            Object[] curRow;
            for (int i = 0; i < data.size(); i++) {
                curRow = data.get(i);
                Row rowDataCW = sheet.createRow(i + 1);
                for (int j = 0; j < curRow.length; j++) {
                    Cell cellData = rowDataCW.createCell(j);
                    if (curRow[j] != null)
                        cellData.setCellValue(curRow[j].toString());
                    else
                        cellData.setBlank();
                }
            }
            for (int i = 0; i < 8; i++)
                sheet.autoSizeColumn(i);
            return true;
        } catch (Exception e) {
            System.out.println("Ошибка в создании отчета 'Детали'");
            return false;
        }
    }

    private void setHeaderReport() {
        Row header = sheet.createRow(0);
        header.setHeight((short) 500);

        Cell staticName1 = header.createCell(0);
        staticName1.setCellValue("Код детали");
        staticName1.setCellStyle(headerStyle());

        Cell staticName2 = header.createCell(1);
        staticName2.setCellValue("Наименование");
        staticName2.setCellStyle(headerStyle());

        Cell staticName3 = header.createCell(2);
        staticName3.setCellValue("Материал");
        staticName3.setCellStyle(headerStyle());

        Cell staticName4 = header.createCell(3);
        staticName4.setCellValue("Вес");
        staticName4.setCellStyle(headerStyle());

        Cell staticName5 = header.createCell(4);
        staticName5.setCellValue("Цвет");
        staticName5.setCellStyle(headerStyle());

        Cell staticName6 = header.createCell(5);
        staticName6.setCellValue("Цена");
        staticName6.setCellStyle(headerStyle());

        Cell staticName7 = header.createCell(6);
        staticName7.setCellValue("Описание");
        staticName7.setCellStyle(headerStyle());

        Cell staticName8 = header.createCell(7);
        staticName8.setCellValue("Общее количество");
        staticName8.setCellStyle(headerStyle());
    }

    private CellStyle headerStyle() {
        CellStyle cellStyle = this.wb.createCellStyle();
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }
}
