package com.shotacon.movie.api.moviefan.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
//@Table(name = "tihe_moviefan_hall")
public class HallEntity {

    private long id;

    // (name = "hall_id", nullable = false)
    private int hallID; // 影厅ID

    // (name = "cinema_id", nullable = false)
    private int cinemaID; // 影院ID

    // (name = "hall_name", nullable = false)
    private String hallName; // 影厅名称

    // (name = "hall_status", nullable = false)
    private int hallStatus; // 影厅状态

    // (name = "alias", nullable = false)
    private String alias; // 别名

    // (name = "intro", nullable = false)
    private String intro; // 影厅描述

    // (name = "seatCount", nullable = false)
    private int seatCount; // 座位总数

    // (name = "hallType", nullable = false)
    private int hallType; // 影厅类型

}
