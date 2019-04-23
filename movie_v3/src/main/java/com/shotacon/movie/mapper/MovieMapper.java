package com.shotacon.movie.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MovieMapper {

    @Select(" select DISTINCT                  t.movie_lanages as language,                    "
            + " t.movie_types as type,         t.film_length as duration,                      "
            + " t.intro as description,        t.movie_image as backgroundPicture,             "
            + " t.movie_image as poster,       t.movie_versions as showVersionList,            "
            + " t.release_time as openDay,     t.actors as leadingRole,                        "
            + " t.director as director,        t.movie_name_cn as showName,                    "
            + " t.movie_name_en as showNameEn, t.movie_id as filmId                            "
            + " from                           tihe_moviefan_movie t                           "
            + " left join tihe_moviefan_shows tms on tms.movie_id = t.movie_id "
            + " left join tihe_moviefan_cinema tmc on tmc.cinema_id = tms.cinema_id where tmc.city = #{cityId} ")
    List<Map<String, Object>> getMovieByCityId(@Param("cityId") String cityId);

    @Select(" select                           t.movie_lanages as language,                    "
            + " t.movie_types as type,         t.film_length as duration,                      "
            + " t.intro as description,        t.movie_image as backgroundPicture,             "
            + " t.movie_image as poster,       t.movie_versions as showVersionList,            "
            + " t.release_time as openDay,     t.actors as leadingRole,                        "
            + " t.director as director,        t.movie_name_cn as showName,                    "
            + " t.movie_name_en as showNameEn, t.movie_id as filmId                            "
            + " from                           tihe_moviefan_movie t                           "
            + " where t.movie_id = #{filmId} ")
    List<Map<String, Object>> getFilmById(@Param("filmId") String filmId);

    @Select(" select   DISTINCT                t.movie_lanages as language,                    "
            + " t.movie_types as type,         t.film_length as duration,                      "
            + " t.intro as description,        t.movie_image as backgroundPicture,             "
            + " t.movie_image as poster,       t.movie_versions as showVersionList,            "
            + " t.release_time as openDay,     t.actors as leadingRole,                        "
            + " t.director as director,        t.movie_name_cn as showName,                    "
            + " t.movie_name_en as showNameEn, t.movie_id as filmId                            "
            + " from                           tihe_moviefan_movie t                           "
            + " left join tihe_moviefan_shows tms on tms.movie_id = t.movie_id and tms.cinema_id = #{cinemaId}  "
            + " where tms.ticket_end_time >= #{date} ")
    List<Map<String, Object>> getFilmByCinema(@Param("cinemaId") String cinemaId, @Param("date") String date);

    @Insert("INSERT INTO tihe_movie_want_watch (nick_name, uid, film_id, create_time, film_name, film_remark, "
            + "film_poster, film_time,source) VALUES (#{username}, #{uid}, #{movieId},"
            + "#{createTime},#{movieName},0,#{moviePoster},#{movieOpenDay},'MF' )")
    int markWantWatch(@Param("uid") int uid, @Param("movieId") int movieId, @Param("movieName") String movieName,
            @Param("moviePoster") String moviePoster, @Param("movieOpenDay") String movieOpenDay,
            @Param("username") String username, @Param("createTime") String createTime);

    @Select("SELECT id FROM tihe_movie_want_watch WHERE uid = #{uid} AND film_id = #{movieId} AND source = 'MF'")
    List<Integer> checkReadyMark(@Param("uid") int uid, @Param("movieId") int movieId);

    @Select("SELECT a.nick_name as nickName, a.film_id as filmId, a.create_time, a.film_name as filmName, "
            + "    a.film_poster as filmPoster, a.film_time as filmTime, '' as filmStatus, 0 as filmRemark "
            + "FROM tihe_movie_want_watch a WHERE a.uid = #{uid} AND a.source = 'MF' ORDER BY a.film_time ASC  ")
    List<Map<String, Object>> getWantWatch(@Param("uid") int uid);

    @Select(" select                           t.movie_lanages as language,                    "
            + " t.movie_types as type,         t.film_length as duration,                      "
            + " t.intro as description,        t.movie_image as backgroundPicture,             "
            + " t.movie_image as poster,       t.movie_versions as showVersionList,            "
            + " t.release_time as openDay,     t.actors as leadingRole,                        "
            + " t.director as director,        t.movie_name_cn as showName,                    "
            + " t.movie_name_en as showNameEn, t.movie_id as filmId                            "
            + " from                           tihe_moviefan_movie t                           "
            + " where (POSITION(#{text} in t.movie_name_cn)  or POSITION(#{text} in t.movie_name_en)) ")
    List<Map<String, Object>> search(@Param("text") String text);

    @Select("select t.movie_id as filmId, t.showtime_id as scheduleId, t.cinema_id as cinemaId, "
            + " t.show_time as showDate, t.hall_name as hallName, t.price/100 as price, t.version as showVersion, "
            + " t.ticket_end_time as closeTime from tihe_moviefan_shows t "
            + " where t.cinema_id = #{cinemaId} and t.movie_id = #{filmId} "
            + " and DATE_FORMAT(t.show_time,'%Y-%m-%d') = #{date}"
            + " and t.show_time >= NOW() order by t.show_time asc  ")
    List<Map<String, Object>> getSchedule(@Param("cinemaId") int cinemaId, @Param("filmId") int filmId,
            @Param("date") String date);

    @Select("select t.movie_id as filmId, tmm.movie_name_cn as filmName,t.showtime_id as scheduleId, t.cinema_id as cinemaId,"
            + " DATE_FORMAT(t.ticket_end_time,'%Y-%m-%d') as showDate,t.show_time as showTime , t.hall_name as hallName, t.price/100 as price, "
            + " t.version as showVersion, t.ticket_end_time as closeTime from tihe_moviefan_shows t"
            + " left join tihe_moviefan_movie tmm on tmm.movie_id = t.movie_id "
            + " where t.showtime_id = #{scheduleId} and t.show_time >= NOW() order by t.show_time asc   ")
    List<Map<String, Object>> getScheduleById(@Param("scheduleId") String scheduleId);

    @Select(" select  DISTINCT                  tt.movie_lanages as language,                    "
            + " tt.movie_types as type,         tt.film_length as duration,                      "
            + " tt.intro as description,        tt.movie_image as backgroundPicture,             "
            + " tt.movie_image as poster,       tt.movie_versions as showVersionList,            "
            + " tt.release_time as openDay,     tt.actors as leadingRole,                        "
            + " tt.director as director,        tt.movie_name_cn as showName,                    "
            + " tt.movie_name_en as showNameEn, tt.movie_id as filmId                            "
            + " from                           tihe_movie_ticket_record t                      "
            + " left join tihe_moviefan_movie tt on tt.movie_id = t.film_id "
            + " WHERE t.uid = #{uid} GROUP BY t.film_id ")
    List<Map<String, Object>> getWatched(int uid);

    @Select("SELECT * FROM tihe_movie_ticket_record WHERE uid = #{uid} AND status = #{status} " // 
            + " AND schedule_end_time < #{date} LIMIT #{pageIndex},#{pageNum}")
    List<Map<String, Object>> getTicketList(@Param("uid") int uid, @Param("status") String status,
            @Param("pageIndex") int pageIndex, @Param("pageNum") int pageNum, @Param("date") String date);
}
