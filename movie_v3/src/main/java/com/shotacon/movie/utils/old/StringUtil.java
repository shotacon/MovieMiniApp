package com.shotacon.movie.utils.old;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class StringUtil {
    /**
     * 获得【指定长度的】随机数字符串
     *
     * @param length
     * @return
     */
    public static String getRandomStrByLength(int length) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(random.nextInt(10));
        }
        return stringBuilder.toString();
    }

    /**
     * 字符串前补零直到最大长度
     *
     * @param
     * @return
     */
    public static String addZeroToStr(String str, int totalLength) {
        StringBuilder stringBuilder = new StringBuilder();
        int recycleCount = totalLength - str.length();
        for (int i = 0; i < recycleCount; i++) {
            stringBuilder.append(0);
        }
        return stringBuilder.toString() + str;
    }

    public static boolean isEmpty(String str) {
        if (str != null && str.length() != 0 && !str.equalsIgnoreCase("null"))
            return false;
        return true;
    }

    /**
     * 生成流水号（唯一） 【年月日时分秒】+【uuid(位数不够，随机数字字母补位)】 【至少大于十四位】
     * 
     * @return
     */
    public static String getSerilNumByLength(int length) {
        if (length <= 14)
            return null;
        String serlNum = "";
        serlNum = String.valueOf(Math.abs(UUID.randomUUID().toString().hashCode()));
        // 【14位】
        String nowStr = DateUtil.getTodayStrUseFormat(DateUtil.TOTAL_DATE_TIME_SIMPLE);
        String strTemp = nowStr + serlNum;
        int strTempLength = strTemp.length();
        if (strTempLength >= length)
            serlNum = strTemp.substring(0, length);
        else if (strTempLength < length)
            serlNum = strTemp + getStringRandom(length - strTempLength);
        return serlNum;
    }

    /**
     * 生成随机数字和字母
     * 
     * @param length
     * @return 2016年11月2日 下午4:13:39
     */
    public static String getStringRandom(int length) {
        String val = "";
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            if ("char".equalsIgnoreCase(charOrNum)) {
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (random.nextInt(26) + temp);
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    public static Map<String, String> doXMLParse(String strxml) {
        if (null == strxml || "".equals(strxml)) {
            return null;
        }

        Map<String, String> m = new HashMap<String, String>();
        Document document = null;
        try {
            document = DocumentHelper.parseText(strxml);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        if (document == null) {
            return m;
        }
        Element root = document.getRootElement();
        for (Iterator<?> iterator = root.elementIterator(); iterator.hasNext();) {
            Element e = (Element) iterator.next();
            m.put(e.getName(), e.getText());
        }
        return m;
    }
}
