package com.shotacon.movie.api.moviefan.entity;

import java.math.BigDecimal;

import lombok.Data;
import lombok.ToString;

@Data
//@Table(name = "tihe_moviefan_cinema")
@ToString
public class CinemaEntity {

    private long id;

    // (name = "cinema_id", nullable = false, unique = true)
    private int cinemaID; // 影院ID

    // (name = "cinema_name", nullable = false)
    private String cinemaName; // 影院名称

    // (name = "cinema_address", nullable = false)
    private String cinemaAddress; // 影院地址

    // (name = "province", nullable = false)
    private int province; // 影院所在省份ID

    // (name = "city", nullable = false)
    private int city; // 影院所在城市ID

    // (name = "county", nullable = false)
    private int county; // 影院所在区ID

    // (name = "baiduLatitude", nullable = false)
    private BigDecimal baiduLatitude; // 百度纬度

    // (name = "baiduLongitude", nullable = false)
    private BigDecimal baiduLongitude; // 百度经度

    // (name = "cinema_tatus", nullable = false)
    private int cinemaStatus; // 影院状态（1表示正常4表示没有场次）

    // (name = "phone", nullable = false)
    private String phone; // 影院电话

    // (name = "description", nullable = false)
    private String desc; // 影院描述

}
