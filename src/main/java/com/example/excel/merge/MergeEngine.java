package com.example.excel.merge;

import com.example.excel.merge.entity.Record;
import com.example.excel.merge.entity.SourceExcel;
import com.example.excel.merge.entity.TargetExcel;
import com.example.excel.merge.entity.config.MergeConfig;
import com.example.excel.merge.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.BiFunction;

public class MergeEngine {

    private MergeEngine() {
    }

    public static void merge(MergeConfig mergeConfig) {
        checkMergeConfig(mergeConfig);
        String primaryKey = mergeConfig.getMergeKey();
        Map<String, SourceExcel> sourceExcelMap = initSourceExcel(mergeConfig.getSources(), primaryKey);
        TargetExcel targetExcel = initTargetExcel(mergeConfig.getTarget(), primaryKey);
        LinkedHashSet<String> primaryKeyValues = mergePrimaryKey(sourceExcelMap);

        System.out.println("一共合并" + primaryKeyValues.size() + "条记录，主键集合如下:");
        for (String primaryKeyValue : primaryKeyValues) {
            System.out.println(primaryKeyValue);
        }

        Map<String, BiFunction<Map<String, SourceExcel>, String, String>> logic = compileLogic(mergeConfig.getTarget().getLogic());

        final int logicSize = mergeConfig.getTarget().getLogic().size();
        for (String primaryKeyValue : primaryKeyValues) {
            Record record = new Record(primaryKey, primaryKeyValue, logicSize);
            for (Map.Entry<String, BiFunction<Map<String, SourceExcel>, String, String>> logicEntry : logic.entrySet()) {
                BiFunction<Map<String, SourceExcel>, String, String> function = logicEntry.getValue();
                record.appendColumn(logicEntry.getKey(), function.apply(sourceExcelMap, primaryKeyValue));
            }
            targetExcel.appendRecord(record);
        }
        String s = targetExcel.save();
        System.out.println("合并成功，已经合并到文件" + s + "中");
    }

    private static LinkedHashSet<String> mergePrimaryKey(Map<String, SourceExcel> sourceExcelMap) {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        for (SourceExcel value : sourceExcelMap.values()) {
            for (Record record : value.getDatas().values()) {
                result.add(record.getPrimaryKeyValue());
            }
        }
        return result;
    }

    private static Map<String, BiFunction<Map<String, SourceExcel>, String, String>> compileLogic(Map<String, String> logics) {
        Map<String, BiFunction<Map<String, SourceExcel>, String, String>> result = new HashMap<>();
        for (Map.Entry<String, String> en : logics.entrySet()) {
            String logic = en.getValue();
            String[] splited = logic.split("\\.");
            if (splited.length != 2) {
                throw new DefineException("定义的logic：" + logic + " 有误");
            }
            String from = splited[0];
            String value = splited[1];
            result.put(en.getKey(), (stringSourceExcelMap, primaryKeyValue) -> {
                SourceExcel sourceExcel = stringSourceExcelMap.get(from);
                if (sourceExcel == null) {
                    throw new DefineException("logic变量" + en.getKey() + "未定义");
                }
                Record record = sourceExcel.getDatas().get(primaryKeyValue);
                if (record != null) {
                    return record.getValues().getOrDefault(value, "");
                } else {
                    return "";
                }
            });
        }
        return result;
    }

    private static TargetExcel initTargetExcel(MergeConfig.Target target, String primaryKey) {
        return new TargetExcel(target.getPath(), target.getName(), primaryKey, new ArrayList<>(target.getLogic().keySet()));
    }

    private static Map<String, SourceExcel> initSourceExcel(Map<String, MergeConfig.Source> sources, String primaryKey) {
        Map<String, SourceExcel> sourceExcelMap = new HashMap<>();
        for (Map.Entry<String, MergeConfig.Source> stringSourceEntry : sources.entrySet()) {
            SourceExcel sourceExcel = new SourceExcel(stringSourceEntry.getValue().getPath(), primaryKey);
            sourceExcel.read();
            sourceExcelMap.put(stringSourceEntry.getKey(), sourceExcel);
        }
        return sourceExcelMap;
    }

    private static void checkMergeConfig(MergeConfig mergeConfig) {
        if (StringUtils.isEmpty(mergeConfig.getMergeKey())) {
            throw new DefineException("缺少mergeKey，请指定mergeKey");
        }
        if (mergeConfig.getSources() == null || mergeConfig.getSources().size() == 0) {
            throw new DefineException("缺少sources，请至少指定一个source");
        }
        for (MergeConfig.Source source : mergeConfig.getSources().values()) {
            if (source.getPath() == null || "".equals(source.getPath())) {
                throw new DefineException("source:" + source.getAlias() + "，缺少文件路径");
            }
        }
        MergeConfig.Target target = mergeConfig.getTarget();
        if (target == null) {
            throw new DefineException("缺少target，请指定一个target");
        }
        if (StringUtils.isEmpty(target.getName())) {
            throw new DefineException("target缺少文件名name，请指定目标文件名");
        }
        if (StringUtils.isEmpty(target.getPath())) {
            throw new DefineException("target缺少文件路径path，请指定目标文件保存路径");
        }
        if (target.getLogic() == null || target.getLogic().size() == 0) {
            throw new DefineException("target缺少聚合逻辑logic，请指定具体聚合逻辑");
        }
    }
}
