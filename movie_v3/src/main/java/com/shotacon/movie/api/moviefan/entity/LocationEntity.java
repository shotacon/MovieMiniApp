package com.shotacon.movie.api.moviefan.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
//@Table(name = "tihe_moviefan_location")
public class LocationEntity {

    private long id;

    // (name = "location_id", nullable = false, unique = true)
    private int locationID;// 地区ID

    // (name = "parent_id", nullable = false)
    private int parentID;// 父ID

    // (name = "location_type", nullable = false)
    private int locationType;// 地区类型（省、市、县）

    // (name = "name_cn", nullable = false)
    private String nameCN;// 中文名

    // (name = "name_en", nullable = false)
    private String nameEN;// 英文名

    // (name = "name_pinyin", nullable = false)
    private String namePinyin;// 拼音

    // (name = "name_pinyin_short", nullable = false)
    private String namePinyinShort;// 拼音缩写

    // (name = "name_code", nullable = false)
    private String nameCode;// 地区编码

    // (name = "is_hot", nullable = false)
    private boolean isHot;// 是否热门城市

    // (name = "latitude", nullable = false)
    private String latitude;// 纬度

    // (name = "longitude", nullable = false)
    private String longitude;// 经度

}
