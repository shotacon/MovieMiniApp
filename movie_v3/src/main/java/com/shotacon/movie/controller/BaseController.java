package com.shotacon.movie.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.shotacon.movie.config.WXPayConstants;
import com.shotacon.movie.exception.OperateLimitException;
import com.shotacon.movie.exception.ValidateException;
import com.shotacon.movie.utils.newapi.RedisUtil;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.exceptions.JedisException;

@Slf4j
public class BaseController {

    @Autowired
    protected RedisUtil redisUtil;

    protected String validateString(String str, String name) throws ValidateException {
        if (str == null)
            throw new ValidateException(name + "不允许为空");
        try {
            return URLDecoder.decode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new ValidateException(name + "解码失败");
        }
    }

    protected int validateInteger(String str, String name) throws ValidateException {
        validateString(str, name);
        try {
            int result = Integer.parseInt(str);
            return result;
        } catch (Exception e) {
            throw new ValidateException(name + "必须为正整数");
        }
    }

    protected int validateIntegerNullable(String str, String name, int defaults) throws ValidateException {
        if (str == null)
            return defaults;
        try {
            int result = Integer.parseInt(str);
            return result;
        } catch (Exception e) {
            throw new ValidateException(name + "必须为正整数");
        }
    }

    protected int validateOrder(String str, String name) throws ValidateException {
        int order = validateIntegerNullable(str, name, 0);
        if (order != 0 && order != 1) {
            throw new ValidateException(name + "必须为0(升序)或者1(降序)");
        }
        return order;
    }

    protected double validateDouble(String str, String name) throws ValidateException {
        validateString(str, name);
        try {
            double result = Double.parseDouble(str);
            return result;
        } catch (Exception e) {
            log.error(str + "\t无法转化为double");
            throw new ValidateException(name + "必须为Double数字型");
        }
    }

    protected long validateLong(String str, String name) throws ValidateException {
        validateString(str, name);
        try {
            long result = Long.parseLong(str);
            return result;
        } catch (Exception e) {
            throw new ValidateException(name + "必须为Long数字型");
        }
    }

    protected void validateDate(String str, String name, String formatStr) throws ValidateException {
        validateString(str, name);
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatStr);
            format.setLenient(false);
            format.parse(str);
        } catch (Exception e) {
            throw new ValidateException(name + "格式必须为" + formatStr);
        }
    }

    protected String forbidOrderKey(String orderNo) {
        StringBuffer sb = new StringBuffer("CREATE_ORDER_LIMIT_");
        sb.append(orderNo);
        String key = sb.toString();
        return key;
    }

    protected void validateIds(String ids, String name) throws ValidateException {
        validateString(ids, name);
        ids = ids.replace("#", "");
        String[] idArray = ids.split("\\|");
        int seatNum = idArray.length;
        Arrays.sort(idArray);
        if (seatNum > 1) {
            for (int i = 0; i < (seatNum - 1); i++) {
                long result = Long.parseLong(numberIntercept(idArray[i]))
                        - Long.parseLong(numberIntercept(idArray[i + 1]));
                if (Math.abs(result) == 2L) {
                    throw new ValidateException("选座时，请尽量选择连在一起的座位，不要留下单个的空闲座位");
                }
            }
        }
    }

    protected boolean validateOpenToken(String timestamp, String token) {
        String validToken = DigestUtils
                .sha1Hex(DigestUtils.md5Hex(timestamp + WXPayConstants.OPEN_API_KEY) + WXPayConstants.OPEN_API_SECRET);
        if (validToken.equals(token)) {
            return true;
        } else {
            return false;
        }
    }

    protected String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1)
                return ip.substring(0, index);
            else
                return ip;
        }
        ip = request.getHeader("X-Real-IP");
        if (!StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip))
            return ip;
        return request.getRemoteAddr();
    }

    private String numberIntercept(String number) {
        return Pattern.compile("[^0-9]").matcher(number).replaceAll("");
    }

    protected String forbidReLock(String token, String scheduleId) throws OperateLimitException {
        StringBuffer sb = new StringBuffer("LOKESEAT_LIMIT_");
        sb.append(token).append("_").append(scheduleId);
        String key = sb.toString();
        if (redisUtil.hasKey(key)) {
            throw new OperateLimitException(3);
        }
        return key;
    }

    protected String forbidUseCardKey(String token, String cardNo) {
        StringBuffer sb = new StringBuffer("USE_EXCHANGE_CARD_LIMIT_");
        sb.append(token).append("_").append(cardNo);
        return sb.toString();
    }

    protected String forbidUseCard(String token, String cardNo) throws OperateLimitException, JedisException {
        StringBuffer sb = new StringBuffer("USE_EXCHANGE_CARD_LIMIT_");
        sb.append(token).append("_").append(cardNo);
        String key = sb.toString();
        if (redisUtil.hasKey(key)) {
            throw new OperateLimitException(6);
        }
        return key;
    }

    protected String forbidOrder(String orderNo) throws JedisException, OperateLimitException {
        StringBuffer sb = new StringBuffer("CREATE_ORDER_LIMIT_");
        sb.append(orderNo);
        String key = sb.toString();
        if (redisUtil.hasKey(key)) {
            throw new OperateLimitException(3);
        }
        return key;
    }
}
