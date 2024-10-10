package ru.skyshine.report.excel.assemplyShop;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import ru.skyshine.db.model.assemplyShop.MonthlyPlan;
import ru.skyshine.report.excel.ConfigReport;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MonthlyPlanReport extends ConfigReport {
    private final List<MonthlyPlan> data;
    private final int curMonth;

    public MonthlyPlanReport(String nameSheet, List<MonthlyPlan> data) {
        super(nameSheet != null ? nameSheet : "Месячные планы", "MonthlyPlan.xls");
        this.data = data;
        this.curMonth = LocalDate.now().getMonthValue();
    }

    public boolean createReport() {
        try {
            if (data.isEmpty())
                return true;
            Integer month = -2;//записываются строки, которые надо закарсить в серый цвет
            Integer availability, need;
            MonthlyPlan curRow;
            double overallCompletePerc = 0.0;
            int indexRow = 0,
                    countDetailsInMonth = 0,
                    inappropriateMonths = 0; //серый месяц, тк уже прошел
            for (int i = 0; i < data.size(); i++) {
                curRow = data.get(i);
                if (!month.equals(curRow.getMonth())) {
                    month = curRow.getMonth();
                    countDetailsInMonth = 0;
                    overallCompletePerc = 0.0;

                    if (curMonth == month)
                        inappropriateMonths = indexRow;

                    Row rowMonth = sheet.createRow(indexRow);
                    sheet.addMergedRegion(new CellRangeAddress(indexRow, indexRow, 0, 5));
                    Cell cellMonth = rowMonth.createCell(0);
                    cellMonth.setCellValue(Month.of(month).getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("ru")) + " (" + month + ")");
                    cellMonth.setCellStyle(headerStyle());

                    indexRow++;
                }
                Row rowDetails = sheet.createRow(indexRow++);

                Cell cellCodeDetail = rowDetails.createCell(0);
                cellCodeDetail.setCellValue(curRow.getCodeProduct().getCode());

                Cell cellNameDetail = rowDetails.createCell(1);
                cellNameDetail.setCellValue(curRow.getCodeProduct().getName());

                Cell cellAvailability = rowDetails.createCell(2);
                cellAvailability.setCellValue(curRow.getAvailability());
                availability = curRow.getAvailability();

                Cell cellNeed = rowDetails.createCell(3);
                cellNeed.setCellValue(curRow.getNeed());
                need = curRow.getNeed();

                Cell cellLeft = rowDetails.createCell(4);
                cellLeft.setCellValue(need - availability);

                Cell cellCompletePerc = rowDetails.createCell(5);
                cellCompletePerc.setCellValue(new DecimalFormat("#.###").format((double) availability / need * 100) + "%");
                overallCompletePerc += (double) availability / need * 100;

                countDetailsInMonth++;
                if (i + 1 == data.size() || !Objects.equals(month, data.get(i + 1).getMonth())) {
                    Row result = sheet.createRow(indexRow);
                    sheet.addMergedRegion(new CellRangeAddress(indexRow, indexRow, 0, 1));
                    sheet.addMergedRegion(new CellRangeAddress(indexRow, indexRow, 2, 5));

                    Cell staticTotal = result.createCell(0);
                    staticTotal.setCellValue("Выполнение ");

                    Cell totalPerc = result.createCell(2);
                    totalPerc.setCellValue(new DecimalFormat("#.###").format(overallCompletePerc / countDetailsInMonth) + "%");

                    indexRow++;
                }
            }

            Iterator<Cell> cellIterator;
            for (int row = 0; row < (inappropriateMonths != -2 ? inappropriateMonths : indexRow - 1); row++) {
                cellIterator = sheet.getRow(row).cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    cell.setCellStyle(previousMonthsStyle(cell));
                }
            }

            return true;
        } catch (Exception e) {
            System.out.println("Ошибка в создании отчета 'Месячные планы'");
            return false;
        }
    }

    private CellStyle previousMonthsStyle(Cell cell) {
        CellStyle cellStyle = this.wb.createCellStyle();
        cellStyle.cloneStyleFrom(cell.getCellStyle());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        return cellStyle;
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

