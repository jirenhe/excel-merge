package com.example.excel.merge.entity;

import com.example.excel.merge.DefineException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TargetExcelEntity extends ExcelEntity {

    private final List<String> titles;

    private final Map<String, Record> datas = new HashMap<>();

    public TargetExcelEntity(String dir, String fullName, String primaryKey, List<String> titles) {
        super(dir, fullName, primaryKey);
        this.titles = titles;
    }

    public TargetExcelEntity(String fullPath, String primaryKey, List<String> titles) {
        super(fullPath, primaryKey);
        this.titles = titles;
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
                path = dir + File.pathSeparator + fileName + "(" + i + ")" + suffix;
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

    }

    public List<String> getTitles() {
        return new ArrayList<>(titles);
    }

}
