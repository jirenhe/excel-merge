package com.example.excel.merge;

import com.example.excel.merge.entity.Record;
import com.example.excel.merge.entity.SourceExcel;
import com.example.excel.merge.entity.TargetExcel;
import com.example.excel.merge.entity.config.MergeConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MergeEngine {

    private MergeEngine() {
    }

    public static void merge(MergeConfig mergeConfig) {
        checkMergeConfig(mergeConfig);
        String primaryKey = mergeConfig.getMergeKey();
        Map<String, SourceExcel> sourceExcelMap = initSourceExcel(mergeConfig.getSources(), primaryKey);
        TargetExcel targetExcel = initTargetExcel(mergeConfig.getTarget(), primaryKey);
        SourceExcel maxLengthExcel = chooseMaxLengthExcel(sourceExcelMap.values());
        Map<String, Function<Map<String, SourceExcel>, String>> logic = compileLogic(mergeConfig.getTarget().getLogic());
        int logicSize = mergeConfig.getTarget().getLogic().size();
        for (Map.Entry<String, Record> recordEntry : maxLengthExcel.getDatas().entrySet()) {
            String primaryKeyValue = recordEntry.getValue().getPrimaryKeyValue();
            Record record = new Record(primaryKey, primaryKeyValue, logicSize);
            for (Map.Entry<String, Function<Map<String, SourceExcel>, String>> logicEntry : logic.entrySet()) {
                Function<Map<String, SourceExcel>, String> function = logicEntry.getValue();
                record.appendColumn(logicEntry.getKey(), function.apply(sourceExcelMap));
            }
            targetExcel.appendRecord(record);
        }
        targetExcel.save();
    }

    private static Map<String, Function<Map<String, SourceExcel>, String>> compileLogic(Map<String, String> logic) {
        return null;
    }

    private static SourceExcel chooseMaxLengthExcel(Collection<SourceExcel> values) {
        int maxLen = 0;
        SourceExcel maxLenExcel = null;
        for (SourceExcel value : values) {
            int len = value.getDatas().size();
            if (len > maxLen) {
                maxLen = len;
                maxLenExcel = value;
            }
        }
        return maxLenExcel;
    }

    private static TargetExcel initTargetExcel(MergeConfig.Target target, String primaryKey) {
        return new TargetExcel(target.getPath(), target.getName(), new ArrayList<>(target.getLogic().keySet()));
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

    }
}
