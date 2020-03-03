package com.example.excel.merge;

import com.example.excel.merge.entity.MergeConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class BootStrap {

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("args wrong!");
        }
        File yamlFile = new File(args[0]);
        if (!yamlFile.exists() || !yamlFile.isFile()) {
            throw new IllegalArgumentException("yaml file can not open!");
        }
        Yaml yaml = new Yaml();
        try {
            MergeConfig mergeConfig = yaml.loadAs(new FileInputStream(yamlFile), MergeConfig.class);
            System.out.println(mergeConfig);
        } catch (FileNotFoundException e) {
            System.err.print("配置文件无法读取！");
        } catch (Exception e) {
            System.err.print("配置文件格式错误，请检查！");
        }
    }
}
