package com.example.excel.merge.entity;

import com.example.excel.merge.DefineException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SourceExcel extends AbstractExcel {

    private List<String> titles = new ArrayList<>();

    private final Map<String, Record> datas = new LinkedHashMap<>();

    public SourceExcel(String dir, String fullName, String primaryKey) {
        super(dir, fullName, primaryKey);
    }

    public SourceExcel(String fullPath, String primaryKey) {
        super(fullPath, primaryKey);
    }

    public Map<String, Record> getDatas() {
        return datas;
    }

    public void read() {
        if (!exist()) {
            throw new DefineException("source文件不存在无法读取：" + getFullPath() + ", 请检查路径是否正确");
        }
        try {
            Sheet sheet = getSheet();
            initTitles(sheet);
            if (!titles.contains(primaryKey)) {
                throw new DefineException("source文件不包含聚合列:" + primaryKey + "路径：" + getFullPath() + ", 请检查路径是否正确");
            }
            readData(sheet);
        } catch (IOException e) {
            throw new DefineException("source文件读取失败：" + getFullPath() + ", 请检查文件是否正确");
        }
    }

    private void readData(Sheet sheet) {
        int primaryKeyIndex = titles.indexOf(primaryKey);
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            Cell primaryCell = row.getCell(primaryKeyIndex);
            if (primaryCell == null) {
                continue;
            }
            primaryCell.setCellType(CellType.STRING);
            String primaryKeyValue = primaryCell.getStringCellValue();
            Record record = new Record(primaryKey, primaryKeyValue);
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                if (cell == null) continue;
                cell.setCellType(CellType.STRING);
                record.appendColumn(titles.get(j), cell.getStringCellValue());
            }
            this.datas.put(primaryKeyValue, record);
        }
    }

    private void initTitles(Sheet sheet) {
        Row row = sheet.getRow(0);
        if (row == null) {
            throw new DefineException("source文件是个空表：" + getFullPath() + ", 请检查文件是否正确");
        }
        int startIndex = row.getFirstCellNum();
        int lastIndex = row.getLastCellNum();
        if (startIndex == lastIndex) {
            throw new DefineException("source文件第一行必须是标题行，不能为空行：" + getFullPath() + ", 请检查文件是否正确");
        }
        System.out.println("读取文件：" + this.getFullPath() + " 标题：");
        for (int i = startIndex; i < lastIndex; i++) {
            Cell cell = row.getCell(i);
            if (cell == null) {
                throw new DefineException("source文件：" + getFullPath() + ", 标题行存在空栏，请去掉这一列再试");
            }
            cell.setCellType(CellType.STRING);
            String title = cell.getStringCellValue();
            System.out.print(title + " | ");
            titles.add(title);
        }

    }

    private Sheet getSheet() throws IOException {
        Workbook workbook;
        if (suffix.equals("xls")) {
            workbook = new HSSFWorkbook(new FileInputStream(new File(getFullPath())));
        } else if (suffix.equals("xlsx")) {
            workbook = new XSSFWorkbook(new FileInputStream(new File(getFullPath())));
        } else {
            throw new DefineException("source文件不是一个excel文件：" + getFullPath() + ", 请检查文件是否正确");
        }
        return workbook.getSheetAt(0);
    }

    public List<String> getTitles() {
        return new ArrayList<>(titles);
    }
}
