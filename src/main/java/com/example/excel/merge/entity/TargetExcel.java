package com.example.excel.merge.entity;

import com.example.excel.merge.DefineException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class TargetExcel extends AbstractExcel {

    private final List<String> titles = new ArrayList<>();

    private final Map<String, Record> datas = new LinkedHashMap<>();

    public TargetExcel(String dir, String fullName, String primaryKey, List<String> titles) {
        super(dir, fullName, primaryKey);
        if (!titles.contains(primaryKey)) {
            this.titles.add(primaryKey);
        }
        this.titles.addAll(titles);
    }

    public TargetExcel(String fullPath, String primaryKey, List<String> titles) {
        super(fullPath, primaryKey);
        if (!titles.contains(primaryKey)) {
            this.titles.add(primaryKey);
        }
        this.titles.addAll(titles);
    }

    public Map<String, Record> getDatas() {
        return datas;
    }

    public void appendRecord(Record record) {
        if (!record.getPrimaryKey().equals(primaryKey)) {
            throw new IllegalStateException("this record primary key is no matching, can not append!");
        }
        this.datas.put(record.getPrimaryKeyValue(), record);
    }

    public boolean exist() {
        return exist(getFullPath());
    }

    public boolean exist(String path) {
        return new File(path).exists();
    }

    public String save() {
        String path = getFullPath();
        if (exist(path)) {
            int i = 0;
            do {
                path = dir + File.separator + fileName + "(" + i + ")" + "." + suffix;
                i++;
            } while (exist(path));
        }
        File file = new File(path);
        try {
            if (!file.createNewFile()) {
                throw new DefineException("文件" + path + "无法创建！");
            }
        } catch (IOException e) {
            throw new DefineException("文件" + path + "无法创建！");
        }
        writeData(file);
        return path;
    }

    private void writeData(File file) {
        Workbook workbook;
        if (suffix.equals("xls")) {
            workbook = new HSSFWorkbook();
        } else if (suffix.equals("xlsx")) {
            workbook = new XSSFWorkbook();
        } else {
            throw new DefineException("source文件不是一个excel文件：" + getFullPath() + ", 请检查文件是否正确");
        }
        Sheet sheet = workbook.createSheet();
        writeTitle(sheet);
        writeDatas(sheet);
        try {
            workbook.write(new FileOutputStream(file));
        } catch (IOException e) {
            throw new DefineException("目标文件写入失败！");
        }
    }

    private void writeDatas(Sheet sheet) {
        int i = 1;
        for (Map.Entry<String, Record> enrty : this.datas.entrySet()) {
            Row row = sheet.createRow(i);
            Record record = enrty.getValue();
            for (int j = 0; j < this.titles.size(); j++) {
                String title = this.titles.get(j);
                row.createCell(j).setCellValue(record.getValues().get(title));
            }
            i++;
        }
    }

    private void writeTitle(Sheet sheet) {
        Row row = sheet.createRow(0);
        for (int i = 0; i < this.titles.size(); i++) {
            row.createCell(i).setCellValue(this.titles.get(i));
        }
    }

    public List<String> getTitles() {
        return new ArrayList<>(titles);
    }

}
