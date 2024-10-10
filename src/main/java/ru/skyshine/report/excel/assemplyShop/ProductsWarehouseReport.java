package ru.skyshine.report.excel.assemplyShop;

import org.apache.poi.ss.usermodel.*;
import ru.skyshine.db.model.assemplyShop.ProductsWarehouse;
import ru.skyshine.report.excel.ConfigReport;

import java.text.DecimalFormat;
import java.util.List;

public class ProductsWarehouseReport extends ConfigReport {
    private final List<ProductsWarehouse> data;

    public ProductsWarehouseReport(String nameSheet, List<ProductsWarehouse> data) {
        super(nameSheet != null ? nameSheet : "Cклад изделий", "ProductsWarehouse.xls");
        this.data = data;
    }


    public boolean createReport() {
        try {
            setHeaderReport();
            ProductsWarehouse curRow;
            Double occupancyPerc;
            for (int i = 0; i < data.size(); i++) {
                curRow = data.get(i);
                occupancyPerc = null;
                Row rowDataCW = sheet.createRow(i + 1);

                Cell codeCell = rowDataCW.createCell(0);
                codeCell.setCellValue(curRow.getCode());

                Cell codeDetail = rowDataCW.createCell(1);
                if (curRow.getCodeProduct() != null) {
                    codeDetail.setCellValue(curRow.getCodeProduct().getName());
                } else
                    codeDetail.setBlank();

                Cell availabilityAmount = rowDataCW.createCell(2);
                if (curRow.getAvailability() != null) {
                    availabilityAmount.setCellValue(curRow.getAvailability());
                    occupancyPerc = Double.valueOf(curRow.getAvailability());
                } else
                    availabilityAmount.setBlank();

                Cell availabilityMax = rowDataCW.createCell(3);
                if (curRow.getMaximum() != null) {
                    availabilityMax.setCellValue(curRow.getMaximum());
                    Double d = occupancyPerc / Double.valueOf(curRow.getMaximum());
                    occupancyPerc /= (occupancyPerc != null) ? Double.valueOf(curRow.getMaximum()) : null;
                } else
                    availabilityMax.setBlank();

                Cell occupancy = rowDataCW.createCell(4);
                if (occupancyPerc != null) {
                    occupancyPerc *= 100;
                    occupancy.setCellValue(new DecimalFormat("#.###").format(occupancyPerc));
                    if (0 <= occupancyPerc && occupancyPerc <= 50)
                        occupancy.setCellStyle(freeCellStyle());
                    else if (51 <= occupancyPerc && occupancyPerc <= 80)
                        occupancy.setCellStyle(halfCellStyle());
                    else
                        occupancy.setCellStyle(fullCellStyle());
                } else
                    occupancy.setCellValue("???");

            }
            for (int i = 0; i < 5; i++)
                sheet.autoSizeColumn(i);
            return true;
        } catch (Exception e) {
            System.out.println("Ошибка в создании отчета 'Склад изделий'");
            return false;
        }
    }

    private void setHeaderReport() {
        Row header = sheet.createRow(0);
        header.setHeight((short) 500);

        Cell staticName1 = header.createCell(0);
        staticName1.setCellValue("Код ячейки");
        staticName1.setCellStyle(headerStyle());

        Cell staticName2 = header.createCell(1);
        staticName2.setCellValue("Наименование изделия");
        staticName2.setCellStyle(headerStyle());

        Cell staticName3 = header.createCell(2);
        staticName3.setCellValue("Текущее количество");
        staticName3.setCellStyle(headerStyle());

        Cell staticName4 = header.createCell(3);
        staticName4.setCellValue("Максимальное наличие");
        staticName4.setCellStyle(headerStyle());

        Cell staticName5 = header.createCell(4);
        staticName5.setCellValue("Заполненность, %");
        staticName5.setCellStyle(headerStyle());
    }

    private CellStyle headerStyle() {
        CellStyle cellStyle = this.wb.createCellStyle();
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }

    private CellStyle freeCellStyle() {
        CellStyle cellStyle = this.wb.createCellStyle();
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        return cellStyle;
    }

    private CellStyle halfCellStyle() {
        CellStyle cellStyle = this.wb.createCellStyle();
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        return cellStyle;
    }

    private CellStyle fullCellStyle() {
        CellStyle cellStyle = this.wb.createCellStyle();
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        return cellStyle;
    }
}
