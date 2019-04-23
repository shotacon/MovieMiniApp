package com.shotacon.movie.api.moviefan.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.shotacon.movie.api.moviefan.constant.MovieFanConstants;
import com.shotacon.movie.api.moviefan.exception.SignCalculateException;

import lombok.extern.slf4j.Slf4j;

/**
 * 计算签名工具类
 * 
 * @author shotacon
 *
 */
@Slf4j
public class SignUtil {

    /**
     * 签名加密
     * 
     * @param params
     * @param channelCode
     * @return
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    public static Map<String, String> sign(Map<String, Object> params)
            throws SignCalculateException, UnsupportedEncodingException {

        Map<String, String> result = new HashMap<>();
        // 指定排序规则
        Map<String, Object> compateMap = new TreeMap<String, Object>(new Comparator<String>() {
            public int compare(String obj1, String obj2) {
                // 升序排序
                return obj1.compareTo(obj2);
            }
        });

        // 排序参数
        compateMap.putAll(params);

        StringBuffer sb = new StringBuffer();
        for (Entry<String, Object> entry : compateMap.entrySet()) {
            List<String> value = new ArrayList<>();
            for (String str : String.valueOf(entry.getValue()).split(",")) {
                String encode = URLEncoder.encode(str, StandardCharsets.UTF_8.name());
                value.add(encode);
            }
            sb.append(entry.getKey()).append("=").append(StringUtils.join(value, ",")).append("&");
        }
        sb = sb.deleteCharAt(sb.length() - 1);
        result.put("param", sb.toString());
        sb.append(MovieFanConstants.channelSecret);

        String signStr = "";
        try {
            signStr = new String(sb.toString().getBytes("GB2312"));
        } catch (UnsupportedEncodingException e) {
            log.error("Code converse error", e);
            throw new SignCalculateException("Code converse error!");
        }

        String sign = DigestUtils.md5Hex(signStr);
        result.put("sign", sign);
        return result;
    }
}
