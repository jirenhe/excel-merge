package com.example.excel.merge.entity;

import java.util.HashMap;
import java.util.Map;

public class Record {

    private final String primaryKey;

    private final String primaryKeyValue;

    private final Map<String, String> values;

    public Record(String primaryKey, String primaryKeyValue) {
        this.primaryKey = primaryKey;
        this.primaryKeyValue = primaryKeyValue;
        this.values = new HashMap<>();
        this.values.put(primaryKey, primaryKeyValue);
    }

    public Record(String primaryKey, String primaryKeyValue, int size) {
        this.primaryKey = primaryKey;
        this.primaryKeyValue = primaryKeyValue;
        this.values = new HashMap<>((int) (size / 0.75));
        this.values.put(primaryKey, primaryKeyValue);
    }

    public String appendValue(String key, String value) {
        return values.put(key, value);
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public String getPrimaryKeyValue() {
        return primaryKeyValue;
    }

    public Map<String, String> getValues() {
        return values;
    }
}
