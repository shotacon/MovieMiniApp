package com.shotacon.movie.api.moviefan.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.client.RestClientException;

import com.alibaba.fastjson.JSONObject;
import com.shotacon.movie.api.moviefan.constant.MovieFanConstants;
import com.shotacon.movie.api.moviefan.entity.CinemaEntity;
import com.shotacon.movie.api.moviefan.entity.HallEntity;
import com.shotacon.movie.api.moviefan.entity.LocationEntity;
import com.shotacon.movie.api.moviefan.entity.MovieEntity;
import com.shotacon.movie.api.moviefan.entity.OrderEntity;
import com.shotacon.movie.api.moviefan.entity.ShowSeatsEntity;
import com.shotacon.movie.api.moviefan.entity.ShowsEntity;
import com.shotacon.movie.api.moviefan.exception.BodyHandlerException;
import com.shotacon.movie.api.moviefan.exception.SignCalculateException;
import com.shotacon.movie.config.RestSSLClient;

import lombok.extern.slf4j.Slf4j;

/**
 * 电影票友工具类
 * 
 * @author shotacon
 *
 */
@Slf4j
public class MovieFanUtil {

    /**
     * 替换类型或者版本
     * 
     * @param type
     * @param isType true为类型, false为版本
     * @return
     */
    public static String replaceType(String type, boolean isType) {
        List<String> result = new ArrayList<>();
        String trim = type.trim();
        for (String singleType : trim.split(",")) {
            if (isType) {
                result.add(MovieFanConstants.movieTypesMap.get(Integer.parseInt(singleType)));
            } else {
                result.add(MovieFanConstants.movieVersionMap.get(Integer.parseInt(singleType)));
            }
        }
        if (result.size() <= 0) {
            return StringUtils.EMPTY;
        }
        return StringUtils.join(result, ",");
    }

    /**
     * 查询指定的订单。通过查询订单接口可以查询到订单的状态及订单信息<br>
     * 订单的状态分为8种：<br>
     * <ul>
     * <li>0:新建,创建订单
     * <li>2:删除,
     * <li>10:锁座成功,锁座成功
     * <li>20:锁座失败,锁座失败
     * <li>30:成功,出票成功
     * <li>40:失败,出票失败
     * <li>100:已取消,
     * <li>110:释放座位，
     * 
     * @param orderExternalID 平台订单号
     * @return
     * @throws RestClientException
     * @throws BodyHandlerException
     */
    public static OrderEntity queryOrder(String orderExternalID) throws RestClientException, BodyHandlerException {
        Map<String, Object> params = new HashMap<>();
        params.put("orderExternalID", orderExternalID);
        JSONObject body = bodyHandler(MovieFanConstants.movieUrl + MovieFanConstants.QUERY_ORDER, params);
        return body.getJSONObject("orderdetail").toJavaObject(OrderEntity.class);
    }

    /**
     * 锁定指定场次的座位。锁定座位后将产生订单，用于支付后确认订单。每次最多允许锁定4个座位。
     * 
     * @param showtimeID 场次ID
     * @param seatIDs    座位ID（多个用逗号分隔）
     * @param seatNames  座位名称（多个用逗号分隔）
     * @param mobile     手机号
     * @return
     * @throws RestClientException
     * @throws BodyHandlerException
     */
    public static String lockSeats(String showtimeID, String seatIDs, String seatNames, String mobile)
            throws RestClientException, BodyHandlerException {
        Map<String, Object> params = new HashMap<>();
        params.put("showtimeID", showtimeID);
        params.put("seatIDs", seatIDs);
        params.put("seatNames", seatNames);
        params.put("mobile", mobile);
        JSONObject body = bodyHandler(MovieFanConstants.movieUrl + MovieFanConstants.LOCK_SEATS, params);
        return body.getJSONObject("order").getString("orderExternalID");
    }

    /**
     * 释放已锁定的座位。如果锁定座位后放弃支付，可以释放已锁定的座位，方便其他用户购买这些座位。释放座位后，订单也随之被取消。
     * 
     * @param orderExternalID 平台订单号
     * @return
     * @throws RestClientException
     * @throws BodyHandlerException
     */
    public static JSONObject releaseSeats(String orderExternalID) throws RestClientException, BodyHandlerException {
        Map<String, Object> params = new HashMap<>();
        params.put("orderExternalID", orderExternalID);
        return bodyHandler(MovieFanConstants.movieUrl + MovieFanConstants.RELEASE_SEATS, params);
    }

    /**
     * 确认订单
     * 
     * @param i          结算总价(分)
     * @param orderExternalID 平台订单号
     * @param mobile          手机号
     * @return
     * @throws RestClientException
     * @throws BodyHandlerException
     */
    public static String submitOrder(int amount, String orderExternalID, String mobile)
            throws RestClientException, BodyHandlerException {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("mobile", mobile);
        params.put("orderExternalID", orderExternalID);
        JSONObject body = bodyHandler(MovieFanConstants.movieUrl + MovieFanConstants.SUBMIT_ORDER, params);
        return body.toJSONString();
    }

    /**
     * 查询影院影厅列表
     * 
     * @param cinemaID 影院id
     * @return
     * @throws Exception
     * @throws RestClientException
     */
    public static List<HallEntity> queryHalls(int cinemaID) throws RestClientException, BodyHandlerException {
        Map<String, Object> params = new HashMap<>();
        params.put("cinemaID", cinemaID);
        JSONObject body = bodyHandler(MovieFanConstants.movieUrl + MovieFanConstants.QUERY_HALLS, params);
        return body.getJSONArray("halls").toJavaList(HallEntity.class);
    }

    /**
     * 查询开展了业务或是提供影讯信息的所有地区信息
     * 
     * @return
     * @throws Exception
     * @throws RestClientException
     */
    public static List<LocationEntity> queryLocations() throws RestClientException, BodyHandlerException {
        JSONObject body = bodyHandler(MovieFanConstants.movieUrl + MovieFanConstants.QUERY_LOCATIONS, null);
        return body.getJSONArray("locations").toJavaList(LocationEntity.class);
    }

    /**
     * 查询指定影院、指定影片的放映场次列表
     * 
     * @param showtimeID 场次ID
     * @param status     非必填, 座位状态（0：不可售 1：可售）。不传默认查询所有状态的座位列表。
     * @return
     * @throws Exception
     * @throws RestClientException
     */
    public static List<ShowSeatsEntity> queryShowSeats(String showtimeID, String status)
            throws RestClientException, BodyHandlerException {
        Map<String, Object> params = new HashMap<>();
        params.put("showtimeID", showtimeID);
        if (StringUtils.isNotBlank(status)) {
            params.put("status", status);
        }
        JSONObject body = bodyHandler(MovieFanConstants.movieUrl + MovieFanConstants.QUERY_SHOW_SEATS, params);
//        log.info("queryShowSeats: {}", body.toJSONString());
        return body.getJSONArray("seats").toJavaList(ShowSeatsEntity.class);
    }

    /**
     * 查询指定影院、指定影片的放映场次列表
     * 
     * @param cinemaID
     * @param movieID
     * @return
     * @throws Exception
     * @throws RestClientException
     */
    public static List<ShowsEntity> queryShows(int cinemaID, int movieID)
            throws RestClientException, BodyHandlerException {
        Map<String, Object> params = new HashMap<>();
        params.put("cinemaID", cinemaID);
        if (movieID > 0) {
            params.put("movieID", movieID);
        }
        JSONObject body = bodyHandler(MovieFanConstants.movieUrl + MovieFanConstants.QUERY_SHOWS, params);
        return body.getJSONArray("shows").toJavaList(ShowsEntity.class);
    }

    /**
     * 查询所有影片信息
     * 
     * @return
     * @throws Exception
     * @throws RestClientException
     */
    public static List<MovieEntity> queryMovies() throws RestClientException, BodyHandlerException {
        JSONObject body = bodyHandler(MovieFanConstants.movieUrl + MovieFanConstants.QUERY_MOVIES, null);
        return body.getJSONArray("movies").toJavaList(MovieEntity.class);
    }

    /**
     * 查询某个影片信息
     * 
     * @param movieID
     * @return
     * @throws RestClientException
     * @throws BodyHandlerException
     */
    public static MovieEntity queryMovie(int movieID) throws RestClientException, BodyHandlerException {
        Map<String, Object> params = new HashMap<>();
        params.put("movieID", movieID);
        JSONObject body = bodyHandler(MovieFanConstants.movieUrl + MovieFanConstants.QUERY_MOVIE, params);
        return body.getJSONObject("movie").toJavaObject(MovieEntity.class);
    }

    /**
     * 分页查询所有影院列表（每页100条）
     * 
     * @return
     * @throws Exception
     * @throws RestClientException
     */
    public static List<CinemaEntity> queryCinemas() throws RestClientException, BodyHandlerException {

        JSONObject body = bodyHandler(MovieFanConstants.movieUrl + MovieFanConstants.QUERY_CINEMAS, null);

        List<CinemaEntity> resultList = new ArrayList<>();
        // 通过总条数计算分页
        Integer totalCount = body.getInteger("totalCount");
        log.info("Cinemas count: {}", totalCount);
        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i <= totalCount / 100; i++) {
            if (i > 0) {
                params.clear();
                params.put("pageIndex", i + 1);
                body = bodyHandler(MovieFanConstants.movieUrl + MovieFanConstants.QUERY_CINEMAS, params);
            }
            log.debug("Cinemas pageIndex: {}", i + 1);
            List<CinemaEntity> jsonToList = body.getJSONArray("cinemas").toJavaList(CinemaEntity.class);
            resultList.addAll(jsonToList);
        }
        return resultList;
    }

    /**
     * 查询某个影院信息
     * 
     * @param cinemaID
     * @return
     * @throws Exception
     * @throws RestClientException
     */
    public static CinemaEntity queryCinema(int cinemaID) throws RestClientException, BodyHandlerException {
        Map<String, Object> params = new HashMap<>();
        params.put("cinemaID", cinemaID);
        JSONObject body = bodyHandler(MovieFanConstants.movieUrl + MovieFanConstants.QUERY_CINEMA, params);
        return body.getJSONObject("cinema").toJavaObject(CinemaEntity.class);
    }

    /**
     * 对返回的body 做处理<br>
     * 返回code非1的做异常抛出
     * 
     * @param params
     * 
     * @param bodyStr
     * @return
     * @throws BodyHandlerException
     */
    private static JSONObject bodyHandler(String url, Map<String, Object> params) throws BodyHandlerException {
        String bodyStr = "";
        try {
            if (null == params || params.size() <= 0) {
                params = new HashMap<String, Object>();
            }
            // 加入渠道编码和签名
            params.put("channelCode", MovieFanConstants.channelCode);
            Map<String, String> signMap = SignUtil.sign(params);
            url += "?sign=" + signMap.get("sign") + "&" + signMap.get("param");
        } catch (UnsupportedEncodingException | SignCalculateException e) {
            throw new BodyHandlerException("BodyHandler Error: ", e);
        }
//        log.info("bodyHandler url: {}, param: {}", url, params);
        // 解析返回值, 失败重试三次
        for (int i = 0; i < 3; i++) {
            try {
                bodyStr = RestSSLClient.httpRestTemplate.getForEntity(url, String.class).getBody();
            } catch (Exception e) {
                try {
                    log.info("请求资源失败, 第{}次重试", i + 1);
                    Thread.sleep(200);
                } catch (InterruptedException e1) {
                    log.error("", e);
                }
                continue;
            }
            break;
        }
        JSONObject parseObject = JSONObject.parseObject(bodyStr);
        Integer code = parseObject.getInteger("code");
//        log.info("bodyHandler result: code: {}", code);
        String errorMsg = "";
        if (code == 1) {
            return parseObject;
        } else {
            log.info("request result : {}", parseObject.toJSONString());
            if (MovieFanConstants.returnCodeMap.containsKey(code)) {
                errorMsg = "Request Error: " + MovieFanConstants.returnCodeMap.get(code);
            } else {
                errorMsg = "Request Error: Unknow";
            }
            throw new BodyHandlerException(errorMsg);
        }
    }
}
