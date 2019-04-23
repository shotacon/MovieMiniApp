package com.shotacon.movie.utils.newapi;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.shotacon.movie.config.RestSSLClient;
import com.shotacon.movie.config.WXPayConstants;
import com.shotacon.movie.exception.MapRequestException;
import com.shotacon.movie.exception.ValidateCodeException;

public class WXUtil {

    private static final String baseSendUrl = "https://zxgp.tihe-china.com/tihe/third/";

    private static final String baseMapUrl = "http://api.map.baidu.com/geocoder/";

    public static boolean sendCode(String phone, String code) throws ValidateCodeException {
        String url = baseSendUrl + "toSendCode";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", phone);
        params.put("code", code);

        String body = RestSSLClient.getForEntity(url, params, String.class).getBody();

        JSONObject result = JSONObject.parseObject(body);

        if (result.containsKey("msgcode") || "OK".equals(result.getString("msgcode"))) {
            return true;
        } else {
            throw new ValidateCodeException(result.getString("msg"));
        }

    }

    public static JSONObject mapInfo(String lat, String lng) throws MapRequestException {
        String url = baseMapUrl + "v2/";
        String location = lat + "," + lng;

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("location", location);
        params.put("output", "json");
        params.put("coordtype", "wgs84ll");
        params.put("ak", WXPayConstants.BAIDU_MAP_AK);

        String body = RestSSLClient.getForEntity(url, params, String.class).getBody();
        JSONObject result = JSONObject.parseObject(body);

        if (result.containsKey("status") || "0".equals(result.getString("status"))) {
            return result.getJSONObject("result").getJSONObject("addressComponent");
        } else {
            throw new MapRequestException(result.getString("message"));
        }

    }

    public static JSONObject jscode2Session(String code) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appid", WXPayConstants.WX_APP_ID);
        params.put("secret", WXPayConstants.WX_APP_SECRET);
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");

        String body = RestSSLClient.getForEntity(WXPayConstants.JS2CODE_URL, params, String.class).getBody();
        return JSONObject.parseObject(body);
    }
}
