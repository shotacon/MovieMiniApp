package com.shotacon.movie.utils.old;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Joker on 14-6-5.
 */

/**
 * Json生成解析工具类
 *
 * @author Joker
 * @version 1.0
 */
public class JsonUtils<T> {
    private static Gson gson = null;

    static {
        if (gson == null) {
            gson = new GsonBuilder().disableHtmlEscaping().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        }
    }

    public JsonUtils() {
    }

    /**
     * 将Java对象转化为Json对象
     *
     * @param obj Java对象
     * @return Json字符串
     */
    public static String objectToJson(Object obj) {
        String jsonStr = null;
        if (gson != null) {
            jsonStr = gson.toJson(obj);
        }
        return jsonStr;
    }

    /**
     * 将Json对象转化为Map对象
     *
     * @param jsonStr Json字符串
     * @return Map对象
     */
    public static Map<?, ?> jsonToMap(String jsonStr) {
        Map<?, ?> objMap = null;
        if (gson != null) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<?, ?>>() {
            }.getType();
            objMap = gson.fromJson(jsonStr, type);
        }
        return objMap;
    }

    /**
     * 将Json对象转化为Java Bean对象
     *
     * @param jsonStr Json字符串
     * @param cl      Java Bean类名
     * @return Java Bean对象，该对象以Object类型返回
     */
    public static Object jsonToBean(String jsonStr, Class<?> cl) {
        Object obj = null;
        if (gson != null) {
            obj = gson.fromJson(jsonStr, cl);
        }
        return obj;
    }

    /**
     * 将Json对象转化为List对象
     *
     * @param jsonStr Json字符串
     * @return List对象
     */
    public List<T> jsonToList(String jsonStr) {
        List<T> objList = null;
        if (gson != null) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<T>>() {
            }.getType();
            objList = gson.fromJson(jsonStr, type);
        }
        return objList;
    }

    public ArrayList<T> jsonToList(String json, Class<T> cls) {
        ArrayList<T> mList = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for (final JsonElement elem : array) {
            mList.add(gson.fromJson(elem, cls));
        }
        return mList;
    }
}
