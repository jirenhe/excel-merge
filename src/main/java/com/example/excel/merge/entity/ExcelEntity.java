package com.example.excel.merge.entity;

import com.example.excel.merge.DefineException;

import java.io.File;

public abstract class ExcelEntity {

    protected final String fileName;

    protected final String dir;

    protected final String suffix;

    protected final String primaryKey;

    public ExcelEntity(String dir, String fullName, String primaryKey) {
        this(dir + File.pathSeparator + fullName, primaryKey);
    }

    public ExcelEntity(String fullPath, String primaryKey) {
        this.primaryKey = primaryKey;
        while (fullPath.contains(File.pathSeparator + File.pathSeparator)) {
            fullPath = fullPath.replaceAll(File.pathSeparator + File.pathSeparator, File.pathSeparator);
        }
        File f = new File(fullPath);
        if (f.isFile()) {
            throw new DefineException("文件路径不正确:" + fullPath);
        }
        int lastIdx = fullPath.lastIndexOf(File.pathSeparator);
        if (lastIdx < 0) {
            throw new IllegalArgumentException();
        }
        String fullName = fullPath.substring(lastIdx);
        dir = fullPath.substring(0, lastIdx);
        String[] tmp = fullName.split("\\.");
        if (tmp.length == 1) {
            this.fileName = fullName;
            this.suffix = "xlsx";
        } else {
            this.fileName = tmp[0];
            this.suffix = tmp[1];
        }
    }

    public boolean exist() {
        return exist(getFullPath());
    }

    public boolean exist(String path) {
        return new File(path).exists();
    }

    public String getFullPath() {
        return dir + File.pathSeparator + fileName + suffix;
    }
}
