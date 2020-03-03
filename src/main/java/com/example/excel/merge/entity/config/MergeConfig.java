package com.example.excel.merge.entity.config;

import java.util.List;
import java.util.Map;

public class MergeConfig {

    private Map<String, Source> sources;

    private Target target;

    private String mergeKey;

    public static class Target {

        private String name;

        private String path;

        private Map<String, String> logic;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Map<String, String> getLogic() {
            return logic;
        }

        public void setLogic(Map<String, String> logic) {
            this.logic = logic;
        }

        @Override
        public String toString() {
            return "Target{" +
                    "name='" + name + '\'' +
                    ", path='" + path + '\'' +
                    ", logic=" + logic +
                    '}';
        }
    }

    public static class Source {

        private String alias;

        private String path;

        private List<String> filter;

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public List<String> getFilter() {
            return filter;
        }

        public void setFilter(List<String> filter) {
            this.filter = filter;
        }

        @Override
        public String toString() {
            return "Source{" +
                    "alias='" + alias + '\'' +
                    ", path='" + path + '\'' +
                    ", filter=" + filter +
                    '}';
        }
    }

    public Map<String, Source> getSources() {
        return sources;
    }

    public void setSources(Map<String, Source> sources) {
        this.sources = sources;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public String getMergeKey() {
        return mergeKey;
    }

    public void setMergeKey(String mergeKey) {
        this.mergeKey = mergeKey;
    }

    @Override
    public String toString() {
        return "MergeConfig{" +
                "sources=" + sources +
                ", target=" + target +
                ", mergeKey='" + mergeKey + '\'' +
                '}';
    }
}
