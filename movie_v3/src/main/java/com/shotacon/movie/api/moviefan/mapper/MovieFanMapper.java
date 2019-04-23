package com.shotacon.movie.api.moviefan.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.shotacon.movie.api.moviefan.entity.CinemaEntity;
import com.shotacon.movie.api.moviefan.entity.HallEntity;
import com.shotacon.movie.api.moviefan.entity.LocationEntity;
import com.shotacon.movie.api.moviefan.entity.MovieEntity;
import com.shotacon.movie.api.moviefan.entity.OrderEntity;
import com.shotacon.movie.api.moviefan.entity.ShowsEntity;

@Mapper
public interface MovieFanMapper {

    public int updateShows(ShowsEntity result);
    // ===================insert=====================

    // tihe_moviefan_location
    public int insertLocationBatch(List<LocationEntity> list);

    // tihe_moviefan_cinema
    public int insertCinemaBatch(List<CinemaEntity> result);

    // tihe_moviefan_movie
    public int insertMovieBatch(List<MovieEntity> result);

    // tihe_moviefan_hall
    public int insertHallBatch(List<HallEntity> result);

    // tihe_moviefan_shows
    public int insertShowBatch(List<ShowsEntity> result);

    // =======================delete=========================
    @Delete("delete from tihe_moviefan_location")
    public int deleteLocationBatch();

    @Delete("delete from tihe_moviefan_cinema")
    public int deleteCinemaBatch();

    @Delete("delete from tihe_moviefan_movie")
    public int deleteMovieBatch();

    @Delete("delete from tihe_moviefan_hall")
    public int deleteHallBatch();

    @Delete("delete from tihe_moviefan_hall where cinema_id = #{cinemaID}")
    public int deleteHallByCinemaID(@Param("cinemaID") int cinemaID);

    @Delete("delete from tihe_moviefan_shows")
    public int deleteShowBatch();

    @Delete("delete from tihe_moviefan_shows where cinema_id = #{cinemaID} and movie_id = #{movieID}")
    public int deleteShowSpecified(@Param("cinemaID") int cinemaID, @Param("movieID") int movieID);

    public int insertOrder(OrderEntity result);

    @Select("select * from tihe_moviefan_order where orderexternal_id = #{orderExternalID}")
    public List<OrderEntity> queryOrder(@Param("orderExternalID") String orderExternalID);

    public int updateOrder(OrderEntity orderEntity);

}
