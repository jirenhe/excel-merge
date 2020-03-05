package com.example.excel.merge;

import com.example.excel.merge.entity.Record;
import com.example.excel.merge.entity.SourceExcel;
import com.example.excel.merge.entity.TargetExcel;
import com.example.excel.merge.entity.config.MergeConfig;
import com.example.excel.merge.utils.StringUtils;

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
