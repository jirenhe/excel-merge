package com.example.excel.merge.entity;

import com.example.excel.merge.DefineException;

import java.io.File;
import java.util.regex.Matcher;

public abstract class AbstractExcel {

    protected final String fileName;

    protected final String dir;

    protected final String suffix;

    protected final String primaryKey;

    public AbstractExcel(String dir, String fullName, String primaryKey) {
        this(dir + File.separator + fullName, primaryKey);
    }

    public AbstractExcel(String fullPath, String primaryKey) {
        this.primaryKey = primaryKey;
        while (fullPath.contains(File.separator + File.separator)) {
            fullPath = fullPath.replaceAll(Matcher.quoteReplacement(File.separator + File.separator), Matcher.quoteReplacement(File.separator));
        }
        File f = new File(fullPath);
        if (f.exists() && !f.isFile()) {
            throw new DefineException("路径:" + fullPath + "路径有误或不是一个文件，请检查");
        }
        int lastIdx = fullPath.lastIndexOf(File.separator);
        if (lastIdx < 0) {
            throw new IllegalArgumentException();
        }
        String fullName = fullPath.substring(lastIdx + 1);
        dir = fullPath.substring(0, lastIdx);
        String[] tmp = fullName.split("\\.");
        if (tmp.length == 1) {
            throw new DefineException(this.getFullPath() + " 请带上文件后缀名");
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
        return dir + File.separator + fileName + "." + suffix;
    }
}
