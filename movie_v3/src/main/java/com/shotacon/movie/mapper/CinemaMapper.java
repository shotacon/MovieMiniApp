package com.shotacon.movie.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface CinemaMapper {

    @Select("select DISTINCT t.cinema_address as address, 0 as scheduleCloseTime, t.city as cityId,"
            + " t.baidu_latitude as latitude, tl.name_cn as cityName, t.cinema_name as cinema_name, "
            + " t.baidu_longitude as longitude, t.phone as phone, t.cinema_id as cinemaId, 0 as recommend, "
            + " t.county as regionName, 0 as distance from tihe_moviefan_cinema t "
            + " left join tihe_moviefan_location tl on tl.location_id = t.city  where t.cinema_id = #{cinemaId} ")
    List<Map<String, Object>> getCinemaByCinemaId(String cinemaId);

    @SelectProvider(type = SqlProvider.class, method = "searchCinema1")
    List<Map<String, Object>> searchCinema1(@Param("latitude") String latitude, @Param("longitude") String longitude,
            @Param("cityId") String cityId, @Param("cinemaName") String cinemaName);

    @SelectProvider(type = SqlProvider.class, method = "getCinemaByFilmParam")
    List<Map<String, Object>> getCinemaByFilm(@Param("latitude") String latitude, @Param("longitude") String longitude,
            @Param("cityId") String cityId, @Param("area") String area, @Param("filmId") String filmId,
            @Param("date") String date);

    @SelectProvider(type = SqlProvider.class, method = "getCinemaParam")
    List<Map<String, Object>> getCinema(@Param("latitude") String latitude, @Param("longitude") String longitude,
            @Param("area") String area, @Param("cityId") String cityId, String orderBy);

    class SqlProvider {

        public String commonSql() {

            return " select t.cinema_id as cinema_id, t.city as city_id, 0 as schedule_close_time,"
                    + " t.baidu_latitude as latitude, t.baidu_longitude as longitude, t.county as region_name,"
                    + " t.cinema_name as cinema_name, t.cinema_address as address, t.phone as phone, 0 as recommend,"
                    + " IFNULL(ROUND(6371393 * ACOS(COS(RADIANS(#{latitude})) * COS(RADIANS(t.baidu_latitude)) *"
                    + " COS(RADIANS(#{longitude} - t.baidu_longitude)) + SIN(RADIANS(#{latitude})) * "
                    + " SIN(RADIANS(t.baidu_latitude)))),0) as distance from tihe_moviefan_cinema t ";
        }

        public String searchCinema1(@Param("latitude") String latitude, @Param("longitude") String longitude,
                @Param("cityId") String cityId, @Param("cinemaName") String cinemaName) {

            String commonSql = commonSql();

            commonSql += " where t.city = #{cityId} and POSITION(#{cinemaName} in t.cinema_name) ";
            commonSql += " ORDER BY distance asc ";
            return commonSql;
        }

        public String getCinemaByFilmParam(@Param("latitude") String latitude, @Param("longitude") String longitude,
                @Param("cityId") String cityId, @Param("area") String area, @Param("filmId") String filmId,
                @Param("date") String date) {

            String commonSql = commonSql();
            commonSql += " where t.city = #{cityId} and t.cinema_id IN ( SELECT DISTINCT ( ts.cinema_id ) FROM tihe_moviefan_shows ts "
                    + " LEFT JOIN tihe_moviefan_cinema tc ON tc.cinema_id = ts.cinema_id "
                    + " WHERE DATE_FORMAT( ts.show_time, '%Y-%m-%d' ) = #{date} AND movie_id = #{filmId} AND tc.city = #{cityId})";

            if (area != null && !area.equals("")) {
                commonSql += "and t.county = #{area} ";
            }
            commonSql += " ORDER BY distance asc";
            return commonSql;
        }

        public String getCinemaParam(@Param("latitude") String latitude, @Param("longitude") String longitude,
                @Param("area") String area, @Param("cityId") String cityId, String orderBy) {
            String commonSql = commonSql();
            commonSql += " where t.city = #{cityId} ";

            if (area != null && !area.equals("")) {
                commonSql += "and t.county = #{area} ";
            }
            
            if ("1".equals(orderBy)) {
                commonSql += " ORDER BY distance DESC";
            } else {
                commonSql += " ORDER BY distance ASC";
            }
            return commonSql;
        }
    }

}
