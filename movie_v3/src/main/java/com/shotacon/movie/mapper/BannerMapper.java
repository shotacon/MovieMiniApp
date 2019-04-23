package com.shotacon.movie.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BannerMapper {

    @Select("SELECT * FROM tihe_movie_film_promotion WHERE type = 'Banner' ORDER BY show_order DESC")
    List<Map<String, Object>> getFilmBanner();

    @Select("SELECT * FROM tihe_movie_film_promotion WHERE type = 'CinemaTop' ORDER BY show_order DESC")
    List<Map<String, Object>> getCinemaBanner();
}
