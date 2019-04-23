package com.shotacon.movie.api.moviefan.constant;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 常量们
 * 
 * @author shotacon
 *
 */
@Component
public class MovieFanConstants {

    @Value("${movieFan.url}")
    private String urlForAuto;
    @Value("${movieFan.code}")
    private String channelCodeForAuto;
    @Value("${movieFan.secret}")
    private String channelSecretForAuto;
    @Value("${movieFan.insertSize}")
    private String insertSizeForAuto;

    /**
     * 电影票接口地址
     */
    public static String movieUrl;

    /**
     * 电影票接口渠道号
     */
    public static String channelCode;

    /**
     * 电影票接口渠道秘钥
     */
    public static String channelSecret;

    /**
     * 电影票接口插入时的量
     */
    public static int insertSize;

    /**
     * 电影类型
     */
    public static Map<Integer, String> movieTypesMap = new HashMap<Integer, String>();
    /**
     * 电影版本
     */
    public static Map<Integer, String> movieVersionMap = new HashMap<Integer, String>();
    /**
     * 通用返回码
     */
    public static Map<Integer, String> returnCodeMap = new HashMap<Integer, String>();

    @PostConstruct
    public void init() {
        if (!urlForAuto.endsWith("/")) {
            urlForAuto += "/";
        }
        MovieFanConstants.movieUrl = urlForAuto;
        MovieFanConstants.channelCode = channelCodeForAuto;
        MovieFanConstants.channelSecret = channelSecretForAuto;
        MovieFanConstants.insertSize = Integer.parseInt(insertSizeForAuto);

        // 初始化电影类型
        movieTypesMap.put(1, "恐怖片");
        movieTypesMap.put(2, "喜剧片");
        movieTypesMap.put(3, "动作片");
        movieTypesMap.put(4, "动画片");
        movieTypesMap.put(5, "战争片");
        movieTypesMap.put(6, "爱情片");
        movieTypesMap.put(7, "青春片");
        movieTypesMap.put(8, "科幻片");
        movieTypesMap.put(9, "悬疑片");
        movieTypesMap.put(10, "伦理片");
        movieTypesMap.put(11, "灾难片");
        movieTypesMap.put(12, "警匪片");
        movieTypesMap.put(13, "剧情片");
        movieTypesMap.put(14, "冒险片");
        movieTypesMap.put(15, "纪录片");
        movieTypesMap.put(16, "惊悚片");
        movieTypesMap.put(17, "家庭片");
        movieTypesMap.put(18, "历史片");
        movieTypesMap.put(19, "运动片");
        movieTypesMap.put(20, "传记片");
        movieTypesMap.put(21, "犯罪片");
        movieTypesMap.put(22, "奇幻片");
        movieTypesMap.put(23, "武侠片");
        movieTypesMap.put(24, "励志片");
        movieTypesMap.put(25, "情色片");
        movieTypesMap.put(26, "文艺片");
        movieTypesMap.put(27, "暴力片");
        movieTypesMap.put(28, "音乐片");
        movieTypesMap.put(29, "黑色幽默片");

        // 初始化电影版本
        movieVersionMap.put(1, "2D");
        movieVersionMap.put(2, "3D");
        movieVersionMap.put(3, "IMAX");
        movieVersionMap.put(4, "IMAX3D");
        movieVersionMap.put(5, "4D");
        movieVersionMap.put(6, "中国巨幕");
        movieVersionMap.put(7, "中国巨幕3D");

        // 初始化通用返回码
        returnCodeMap.put(1, "操作成功");
        returnCodeMap.put(100, "系统错误");
        returnCodeMap.put(101, "签名错误");
        returnCodeMap.put(200, "缺少参数");
        returnCodeMap.put(201, "渠道场次失效");
        returnCodeMap.put(300, "订单不存在");
        returnCodeMap.put(301, "锁座失败");
        returnCodeMap.put(302, "订单状态不正确");
        returnCodeMap.put(303, "确认订单信息不正确");
    }

    // API常量========================================================

    /**
     * 查询开展了业务或是提供影讯信息的所有地区信息。对应的页面地址
     */
    public static final String QUERY_LOCATIONS = "queryLocations.aspx";

    /**
     * 分页查询所有影院列表(每页100条)
     */
    public static final String QUERY_CINEMAS = "queryCinemas.aspx";

    /**
     * 查询某个影院信息
     */
    public static final String QUERY_CINEMA = "queryCinema.aspx";

    /**
     * 查询所有影片信息
     */
    public static final String QUERY_MOVIES = "queryMovies.aspx";

    /**
     * 查询某个影片信息
     */
    public static final String QUERY_MOVIE = "queryMovie.aspx";

    /**
     * 查询影院影厅列表
     */
    public static final String QUERY_HALLS = "queryHalls.aspx";

    /**
     * 查询指定影院、指定影片的放映场次列表。
     */
    public static final String QUERY_SHOWS = "queryShows.aspx";

    /**
     * 查询指定场次的所有状态或指定状态的座位列表。
     */
    public static final String QUERY_SHOW_SEATS = "queryShowSeats.aspx";

    /**
     * 锁定指定场次的座位。锁定座位后将产生订单，用于支付后确认订单。每次最多允许锁定4个座位。
     */
    public static final String LOCK_SEATS = "lockSeats.aspx";

    /**
     * 确认订单成功不代表出票成功,需要轮询查询订单接口明确返回出票成功或失败才能确定订单的最终状态;
     * 若确认订单返回失败或超时,建议多调用一次查询订单接口,若查询订单接口支付状态返回已支付,可继续轮询查询接口;
     * 
     */
    public static final String SUBMIT_ORDER = "submitOrder.aspx";

    /**
     * 释放已锁定的座位。如果锁定座位后放弃支付，可以释放已锁定的座位，方便其他用户购买这些座位。释放座位后，订单也随之被取消。
     */
    public static final String RELEASE_SEATS = "releaseSeats.aspx";

    /**
     * 查询指定的订单。通过查询订单接口可以查询到订单的状态及订单信息
     */
    public static final String QUERY_ORDER = "queryOrder.aspx";

    /**
     * 查询指定的座位。通过接口可以校验选择的座位是否会产生孤座。
     */
    public static final String VALIDSEAT_POSTION = "validseatPostion.aspx";
}
