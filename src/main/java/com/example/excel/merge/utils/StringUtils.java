package com.example.excel.merge.utils;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    /**
     * 根据replacementMap 替换字符串
     *
     * @param target
     * @param replacementMap
     * @return
     */
    public static String multipleReplace(String target, final Map<String, ?> replacementMap) {
        return multipleReplace(target, replacementMap, "", "");
    }

    /**
     * 根据replacementMap 替换字符串
     *
     * @param target
     * @param replacementMap
     * @param prefix         替换关键字前缀
     * @param suffix         替换关键字后缀
     * @return
     */
    public static String multipleReplace(String target, final Map<String, ?> replacementMap, String prefix, String suffix) {
        String result = target;
        for (Map.Entry<String, ?> entry : replacementMap.entrySet()) {
            result = result.replaceAll(prefix + entry.getKey() + suffix, entry.getValue().toString());
        }
        return result;
    }


    /**
     * 替换掉HTML标签方法
     */
    public static String replaceHtml(String html) {
        if (isEmpty(html)) {
            return "";
        }
        String regEx = "<.+?>";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(html);
        return m.replaceAll("");
    }

    /**
     * 缩略字符串（不区分中英文字符）
     *
     * @param str    目标字符串
     * @param length 截取长度
     * @return
     */
    public static String abbr(String str, int length) {
        if (str == null) {
            return "";
        }
        if (str.length() <= length) {
            return str;
        } else {
            return str.substring(0, length) + "...";
        }
    }

    /**
     * 转换为Double类型
     */
    public static Double toDouble(Object val) {
        if (val == null) {
            return 0D;
        }
        try {
            return Double.valueOf(val.toString().trim());
        } catch (Exception e) {
            return 0D;
        }
    }

    /**
     * 转换为Float类型
     */
    public static Float toFloat(Object val) {
        return toDouble(val).floatValue();
    }

    /**
     * 转换为Long类型
     */
    public static Long toLong(Object val) {
        return toDouble(val).longValue();
    }

    /**
     * 转换为Integer类型
     */
    public static Integer toInteger(Object val) {
        return toLong(val).intValue();
    }


    public static String string2Unicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }
        return unicode.toString();
    }

    public static String unicode2String(String unicode) {
        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);
            // 追加成string
            string.append((char) data);
        }
        return string.toString();
    }


    /**
     * 下划线转驼峰
     *
     * @param source
     * @return
     */
    public static String underLine2Camel(String source) {
        if (isEmpty(source)) return source;
        if (!source.contains("_")) return source;
        StringBuilder result = new StringBuilder(30);
        char[] sourceCharArray = source.toCharArray();
        char item;
        for (int i = 0; i < sourceCharArray.length; i++) {
            item = sourceCharArray[i];
            if (item == '_') {
                result.append(Character.toUpperCase(sourceCharArray[i + 1]));
                i++;
            } else {
                result.append(item);
            }
        }
        return result.toString();
    }

    /**
     * 驼峰转下划线
     *
     * @param source
     * @return
     */
    public static String camel2UnderLine(String source) {
        if (isEmpty(source)) return source;
        StringBuilder result = new StringBuilder(30);
        char[] sourceCharArray = source.toCharArray();
        char item;
        for (char aSourceCharArray : sourceCharArray) {
            item = aSourceCharArray;
            if (Character.isUpperCase(item)) {
                result.append('_').append(Character.toLowerCase(item));
            } else {
                result.append(item);
            }
        }
        return result.toString();
    }

    public static String specifyTrim(final String trimTarget, final String trimGist) {
        if (isNotEmpty(trimTarget) && isNotEmpty(trimGist)) {
            String result = trimTarget;
            int trimGistLength = trimGist.length();
            if (result.indexOf(trimGist) == 0) {
                result = result.substring(trimGistLength);
            }
            if (result.lastIndexOf(trimGist) == result.length() - trimGistLength) {
                result = result.substring(0, result.length() - trimGistLength);
            }
            return result;
        } else {
            return trimTarget;
        }
    }

    /**
     * 把按逗号分隔的字符串转变为列表，同时会进行去重复，去空处理
     * 如："123,441,3242"转为[123,441,3242]
     *
     * @param target   目标字符串
     * @param function 实现类型转换
     * @param <T>      返回List中的元素类型
     * @return 分隔后的列表
     */
    public static <T> List<T> splitCommaToList(String target, Function<String, T> function) {
        if (function == null) {
            throw new NullPointerException();
        }
        if (StringUtils.isNotEmpty(target)) {
            Set<String> temp = new LinkedHashSet<>((Arrays.asList(target.split(","))));
            List<T> result = new ArrayList<>(temp.size());
            for (String str : temp) {
                if (!"".equals(str)) {
                    result.add(function.apply(str));
                }
            }
            return result;
        } else {
            return Collections.emptyList();
        }
    }

    public static boolean isNotEmpty(String target) {
        return !isEmpty(target);
    }

    /**
     * 去除字符串中的空格和无效段
     * 列：12,,1231,,"","  ",12,
     * 处理后 12,1231,12
     *
     * @param str   字符串
     * @param split 替换符
     * @return String
     */
    public static String strChange(String str, String split) {
        //1、如果分割符为空的话，默认为','
        if (Objects.equals(split.trim(), "")) {
            split = ",";
        }
        //2、将字符串转换成数组
        String[] arr = str.split(split);
        //3、定义中间变量value
        String value = "";
        List<Long> arrData = new ArrayList();
        //4、遍历数组，如果下标索引对应的值为null或者空字符串的，则将其添加到value中
        for (String s : arr) {
            //注意：字符串判断为空的话，一定要加trim()，因为："    ".length != 0
            if (!StringUtils.isEmpty(s)) {
                Long y = Long.valueOf(s);
                arrData.add(y);
            }
        }
        Collections.sort(arrData);
        for (Long s : arrData) {
            if (s <= 0) {
                continue;
            }
            value += s + split;
        }
        //5、如果value为空的话，返回null,不为空的话，则去掉value的最后一个字符然后返回
        return Objects.equals(value, "") ? null : value.substring(0, value.length() - 1);
    }

    public static boolean isEmpty(String s) {
        return s == null || "".equals(s);
    }

    /**
     * 把按逗号分隔的字符串转变为列表，同时会进行去重复，去空处理
     *
     * @param target 目标字符串
     * @return 分隔后的列表
     */
    public static List<String> splitCommaToList(String target) {
        return splitCommaToList(target, s -> s);
    }

    public static <T> String listToCommaSplit(List<T> list, Function<T, String> function) {
        if (function == null) {
            throw new NullPointerException();
        }
        if (CollectionUtils.isNotEmpty(list)) {
            StringBuilder stringBuilder = new StringBuilder();
            list.forEach(t -> stringBuilder.append(function.apply(t)).append(","));
            return stringBuilder.substring(0, stringBuilder.length() - 1);
        }
        return null;
    }

    public static String listToCommaSplit(List<String> list) {
        return listToCommaSplit(list, s -> s);
    }

}
