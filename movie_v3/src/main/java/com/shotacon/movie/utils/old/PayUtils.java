package com.shotacon.movie.utils.old;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PayUtils {
    public static String getNonceStr() {
        String currTime = DateUtil.getNowDateStrByFormatStr(DateUtil.TOTAL_DATE_TIME_SIMPLE);
        String strTime = currTime.substring(8, currTime.length());
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random += 0.1;
        }
        for (int i = 0; i < 4; i++) {
            num *= 10;
        }
        num = (int) (random * num);
        return strTime + num;
    }

    public static String createLinkString(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        StringBuffer sb = new StringBuffer();

        for (String key : keys) {
            String value = params.get(key);
            sb.append(key).append("=").append(value).append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static Map<String, String> paraFilter(Map<String, String> sArray) {
        Map<String, String> result = new HashMap<String, String>();
        if (sArray == null || sArray.size() == 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                    || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }

    public static String sign(String text, String key, String charset) {
        text = text + "&key=" + key;
        log.info(text);
        return DigestUtils.md5Hex(getContentBytes(text, charset));
    }

    @SuppressWarnings("rawtypes")
	public static String createSign(SortedMap<String, String> packageParams, String key) {
        StringBuffer sb = new StringBuffer();
        Set<?> es = packageParams.entrySet();
        Iterator<?> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k).append("=").append(v).append("&");
            }
        }
        sb.append("key=").append(key);
        String sign = DigestUtils.md5Hex(getContentBytes(sb.toString(), "utf-8"));
        return sign;
    }

    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equalsIgnoreCase(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getMoney(String amount) {
        if (amount == null)
            return "";

        String currency = amount.replace("\\$|\\￥|\\,", "");
        int index = currency.indexOf(".");
        int length = currency.length();
        Long amLong = 0l;
        if (index == -1) {
            amLong = Long.valueOf(currency + "00");
        } else if (length - index >= 3) {
            amLong = Long.valueOf((currency.substring(0, index + 3)).replace(".", ""));
        } else if (length - index == 2) {
            amLong = Long.valueOf((currency.substring(0, index + 2)).replace(".", "") + 0);
        } else {
            amLong = Long.valueOf((currency.substring(0, index + 1)).replace(".", "") + "00");
        }
        return amLong.toString();
    }

    @SuppressWarnings("unchecked")
	public static String getPayNo(String result) throws DocumentException, SAXException {
        Map<String, String> map = new HashMap<String, String>();
        InputStream in = new ByteArrayInputStream(result.getBytes());
        SAXReader reader = new SAXReader();
        reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        Document doc = reader.read(in);
        Element root = doc.getRootElement();
        List<Element> list = root.elements();
        for (Element element : list) {
            map.put(element.getName(), element.getText());
        }
        String return_code = map.get("return_code");
        String result_code = map.get("result_code");
        String prepay_id = "";
        if (return_code.equalsIgnoreCase("SUCCESS") && result_code.equals("SUCCESS")) {
            prepay_id = map.get("prepay_id");
        }
        return prepay_id;
    }

    @SuppressWarnings("unchecked")
	public static Map<String, String> getNotifyUrl(String result) throws DocumentException, SAXException {
        Map<String, String> map = new HashMap<String, String>();
        InputStream in = new ByteArrayInputStream(result.getBytes());
        SAXReader read = new SAXReader();
        read.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        read.setFeature("http://xml.org/sax/features/external-general-entities", false);
        read.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        Document doc = read.read(in);
        Element root = doc.getRootElement();
        List<Element> list = root.elements();
        for (Element element : list) {
            map.put(element.getName().toString(), element.getText().toString());
        }
        return map;
    }

    public static boolean verifyWXNotify(Map<String, String> map, String key) {
        String mapStr = createLinkString(map);
        String signOwn = sign(mapStr, key, "utf-8").toUpperCase();
        String signWx = map.get("sign");
        if (signOwn.equals(signWx)) {
            return true;
        } else {
            return false;
        }
    }
}