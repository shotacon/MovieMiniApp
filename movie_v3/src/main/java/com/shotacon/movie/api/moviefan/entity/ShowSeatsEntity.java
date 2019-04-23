package com.shotacon.movie.api.moviefan.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
//@Table(name = "tihe_moviefan_show_seats")
public class ShowSeatsEntity {

    private long id;

    // (name = "seat_id", nullable = false)
    private String seatID; // 座位ID

    // (name = "row_num", nullable = false)
    private String rowNum; // 行号

    // (name = "col_num", nullable = false)
    private String colNum; // 列号

    // (name = "x_coord", nullable = false)
    private int xCoord; // 横坐标

    // (name = "y_coord", nullable = false)
    private int yCoord; // 纵坐标

    // (name = "type", nullable = false)
    private int type; // 座位类型（1：普通 2, 情侣左座; 3, 情侣右座）

    // (name = "love_code", nullable = false)
    private String loveCode; // 情侣座编码。当座位类型是情侣座时才有该编码，情侣座编码相同的两个座位互相关联。

    // (name = "status", nullable = false)
    private String status; // 可售状态（0：不可售 1：可售，2不是座位）
}
