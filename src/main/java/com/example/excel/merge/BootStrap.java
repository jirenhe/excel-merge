package com.example.excel.merge;

import com.example.excel.merge.entity.config.MergeConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BootStrap {

    public static void main(String[] args) throws IOException {
        File directory = new File(".");
        String f = directory.getCanonicalPath() + File.separator + "merge.txt";
        System.out.println("读取配置文件：" + f);
        File yamlFile = new File(f);
        if (!yamlFile.exists() || !yamlFile.isFile()) {
            System.out.println("配置文件：merge.txt不存在请检查！");
        } else {
            Yaml yaml = new Yaml();
            try {
                MergeConfig mergeConfig = yaml.loadAs(new FileInputStream(yamlFile), MergeConfig.class);
                for (Map.Entry<String, MergeConfig.Source> stringSourceEntry : mergeConfig.getSources().entrySet()) {
                    stringSourceEntry.getValue().setAlias(stringSourceEntry.getKey());
                }
                System.out.println(mergeConfig);
                MergeEngine.merge(mergeConfig);
            } catch (FileNotFoundException e) {
                System.out.println("配置文件无法读取！");
            } catch (DefineException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("程序发生错误，请联系开发人员！");
                e.printStackTrace();
            }
        }
        System.out.println("程序将在5秒钟后退出....");
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
