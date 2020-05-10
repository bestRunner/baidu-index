package com.baidu.index.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GsonUtil {
    private static Gson gson = null;

    static {
        if (gson == null) {
            gson = new Gson();
//            gson= new GsonBuilder().create();
        }
    }

    private GsonUtil() {
    }

    /**
     * 将对象转成json格式
     *
     * @param object
     * @return String
     */
    public static String gsonString(Object object) {
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(object);
        }
        return gsonString;
    }

    /**
     * 将json转成特定的cls的对象
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> T gsonToBean(String gsonString, Class<T> cls) {
        T t = null;
        if (gson != null) {
            t = gson.fromJson(gsonString, cls);
        }
        return t;
    }

    /**
     * json字符串转成list
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> List<T> gsonToList(String gsonString, Class<T> cls) {
        List<T> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString, new TypeToken<List<T>>() {
            }.getType());
        }
        return list;
    }

    /**
     * json字符串转成list中有map的
     *
     * @param gsonString
     * @return
     */
    public static <T> List<Map<String, T>> gsonToListMaps(String gsonString) {
        List<Map<String, T>> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString,
                    new TypeToken<List<Map<String, T>>>() {
                    }.getType());
        }
        return list;
    }

    /**
     * json字符串转成map的
     *
     * @param gsonString
     * @return
     */
    public static <T> Map<String, T> gsonToMaps(String gsonString) {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }

    public static Picker picker(JsonObject src) {
        return new Picker(src);
    }

    public static class Picker {
        private String prefix = null;
        private String sep = "_";
        private JsonObject src;
        private int levelNameKeepType = 0; //-1:no level,0:full level,>0:last n level
        private Map<String, String> translator = new HashMap<>();
        ;

        public Picker() {
        }

        public Picker(JsonObject jsonObject) {
            this.src = jsonObject;
        }

        public Picker separator(String sep) {
            this.sep = sep;
            return this;
        }

        public Picker prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Picker ignoreFullLevelName() {
            this.levelNameKeepType = -1;
            return this;
        }

        public Picker selectAs(String key, String rename) {
            translator.put(key, rename);
            return this;
        }

        public Picker selectAll() {
            src.entrySet().forEach(entry -> translator.put(entry.getKey(), entry.getKey()));
            return this;
        }

        public Picker select(String keyFirst, String... keys) {
            translator.put(keyFirst, keyFirst);
            for (String key : keys) {
                translator.put(key, key);
            }
            return this;
        }

    }
}