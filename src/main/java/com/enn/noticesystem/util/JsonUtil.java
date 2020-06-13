package com.enn.noticesystem.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Project: NoticeSystem
 * Create by liyanfei on 20/05/20 16:28
 * Version: 1.0
 */
public class JsonUtil {

    /**
     * @param
     * @return
     * @todo 对象转为JSON字符串
     * @date 20/05/20 16:29
     */
    public static String getString(Object obj) {
        return JSON.toJSONString(obj);

    }

    /**
     * @author liyanfei
     * @date 20/05/20 16:34
     * @description Json字符串转对象
     */
    public static Object getObj(String jsonStr) {
        if (checkJsonStr(jsonStr)) {
            return JSON.parseObject(jsonStr);
        } else {
            return null;
        }
    }


    public static Map<String, Object> getMap(String jsonStr) {
        if (checkJsonStr(jsonStr)) {
            return (Map<String, Object>) JSONObject.parse(jsonStr);
        } else {
            return null;
        }
    }

    /**
     * @author liyanfei
     * @date 20/05/20 17:16
     * @description 校验json 字符串合法性
     */
    private static boolean checkJsonStr(String jsonStr) {
        return true;
    }

    /**
     * @param
     * @return
     * @todo 排序list中的map对象，通过map的id
     * @date 20/05/28 18:29
     */
    public static void sortListJsonByKey(List<Map<String, Object>> lm,String sortKey) {
        Collections.sort(lm, new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Integer id1 = Integer.valueOf(o1.get(sortKey).toString());
                Integer id2 = Integer.valueOf(o2.get(sortKey).toString());
                return id1.compareTo(id2);
            }
        });
    }
}
