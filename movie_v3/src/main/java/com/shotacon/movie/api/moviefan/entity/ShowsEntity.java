package com.shotacon.movie.api.moviefan.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
//@Table(name = "tihe_moviefan_shows")
public class ShowsEntity {

    private long id;

    // (name = "showtime_id", nullable = false)
    private int showtimeID; // 场次ID

    // (name = "movie_id", nullable = false)
    private int movieID; // 影片ID

    // (name = "cinema_id", nullable = false)
    private int cinemaID; // 影院ID

    // (name = "version", nullable = false)
    private String version; // 版本

    // (name = "language", nullable = false)
    private String language; // 语言

    // (name = "hall_id", nullable = false)
    private int hallID; // 影厅ID

    // (name = "ticket_start_time", nullable = false)
    private String ticketStartTime; // 场次开始时间

    // (name = "price", nullable = false)
    private int price; // 价格

    // (name = "show_time", nullable = false)
    private String showTime; // 放映时间（格式: yyyy-MM-dd HH:mm:ss）

    // (name = "ticket_end_time", nullable = false)
    private String ticketEndTime; // 停售时间（格式: yyyy-MM-dd HH:mm:ss），停止售票时间。到达停售时间后，该场次将不再允许锁座下单。

    // (name = "status", nullable = false)
    private int status; // 状态(0停用，1启用)
    
    private int filmLength; //  int 放映时长
    
    private int retailPrice; // 原价
    
    private String hallName; // 厅名
}
