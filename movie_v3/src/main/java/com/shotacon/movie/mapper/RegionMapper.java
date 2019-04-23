package com.shotacon.movie.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RegionMapper {

    @Select("SELECT t.name_cn AS regionName, t.name_pinyin AS pinYin, t.parent_id AS parentId, "
            + "t.name_code AS cityCode, t.location_id AS cityId  FROM tihe_moviefan_location t"
            + " where t.location_type = 2")
    List<Map<String, Object>> getAllRegion();

    @Select("SELECT t.name_cn AS regionName, t.name_pinyin AS pinYin, t.parent_id AS parentId, "
            + "t.name_code AS cityCode, t.location_id AS cityId  FROM tihe_moviefan_location t "
            + "where t.name_pinyin like CONCAT(#{trip},'%') and t.location_type = 2")
    List<Map<String, Object>> getRegionByPinYin(String trip);

    @Select("SELECT t.name_cn AS regionName, t.name_pinyin AS pinYin, t.parent_id AS parentId, "
            + "t.name_code AS cityCode, t.location_id AS cityId  FROM tihe_moviefan_location t "
            + "where t.name_cn like CONCAT(#{trip},'%') and t.location_type = 2")
    List<Map<String, Object>> getRegionByNameLike(String trip);

    @Select("SELECT name_cn FROM tihe_moviefan_location WHERE parent_id IN (SELECT name_code FROM tihe_moviefan_location WHERE name_cn = #{cityName})")
    List<String> getRegionArea(String cityName);

    @Select("SELECT t.name_cn AS regionName, t.name_pinyin AS pinYin, t.parent_id AS parentId, "
            + "t.name_code AS cityCode, t.location_id AS cityId  FROM tihe_moviefan_location t "
            + "where t.name_cn = #{cityName} and t.location_type = 2")
    List<Map<String, Object>> getRegionByCityName(String cityName);

    @Select("SELECT t.name_cn AS regionName, t.name_pinyin AS pinYin, t.parent_id AS parentId, "
            + "t.name_code AS cityCode, t.location_id AS cityId  FROM tihe_moviefan_location t "
            + "where t.location_id = #{cityId}")
    List<Map<String, Object>> getRegionByCityId(String cityId);

    @Select("SELECT location_id FROM tihe_moviefan_location WHERE parent_id = #{cityId} and name_cn = #{area}")
    List<String> getRegionAreaByCityId(@Param("area") String area, @Param("cityId") String cityId);

}
