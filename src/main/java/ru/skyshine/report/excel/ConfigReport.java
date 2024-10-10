package ru.skyshine.report.excel;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.FileOutputStream;
import java.io.IOException;

public class ConfigReport {

    @Getter
    protected Workbook wb;
    protected Sheet sheet;
    @Setter
    protected String nameSheet;
    protected final String startPath = "src\\main\\resources\\reports\\";
    @Getter
    protected String nameFile;

    public ConfigReport(String nameSheet, String nameFile) {
        this.wb = new HSSFWorkbook();
        this.nameSheet = nameSheet;
        this.sheet = wb.createSheet(this.nameSheet);
        this.nameFile = nameFile;
    }

    public boolean export(HttpServletResponse response, String nameFile) {
        this.nameFile = nameFile;
        return export(response);
    }

    public boolean export(HttpServletResponse response) {
        setServiceFields(response);
        try {
            if (sheet.getPhysicalNumberOfRows() <= 0) {
                System.out.println("Отчет " + nameFile + " пустой");
            }
            ServletOutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            wb.close();
            outputStream.close();
            return true;
        } catch (IOException e) {
            System.out.println("Ошибка передача отчета " + nameFile + " сервером");
            return false;
        }
    }

    public boolean saveInDir() {
        try {
            FileOutputStream resultFile = new FileOutputStream(startPath + nameFile);
            wb.write(resultFile);
            return true;
        } catch (IOException exception) {
            System.out.println("Ошибка сохранения отчета " + nameFile + " на диск");
            return false;
        }
    }

    private void setServiceFields(HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nameFile);
    }
}
