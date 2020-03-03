package com.example.excel.merge.entity;

import com.example.excel.merge.DefineException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceExcelEntity extends ExcelEntity {

    private List<String> titles;

    private final Map<String, Record> datas = new HashMap<>();

    public SourceExcelEntity(String dir, String fullName, String primaryKey) {
        super(dir, fullName, primaryKey);
    }

    public SourceExcelEntity(String fullPath, String primaryKey) {
        super(fullPath, primaryKey);
    }

    public Map<String, Record> getDatas() {
        return datas;
    }

    public void read() {
        if (!exist()) {
            throw new DefineException("文件不存在无法读取：" + getFullPath());
        }
    }

    public void readTitle() {

    }

    public List<String> getTitles() {
        return new ArrayList<>(titles);
    }
}
