package com.shotacon.movie.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shotacon.movie.api.moviefan.util.MovieFanUtil;
import com.shotacon.movie.config.ErrorCode;
import com.shotacon.movie.mapper.CinemaMapper;
import com.shotacon.movie.mapper.MovieMapper;
import com.shotacon.movie.mapper.RegionMapper;
import com.shotacon.movie.mapper.UserMapper;
import com.shotacon.movie.model.ResMsg;
import com.shotacon.movie.utils.old.DateUtil;

@Service
public class MovieService {

    @Autowired
    private RegionMapper regionMapper;
    @Autowired
    private MovieMapper movieMapper;
    @Autowired
    private CinemaMapper cinemaMapper;
    @Autowired
    UserMapper userMapper;

    public ResMsg getFilm(String cityName, int pageIndex, int pageNum, String sign) {
        List<Map<String, Object>> regionByCityName = regionMapper.getRegionByCityName(cityName);
        if (null == regionByCityName || regionByCityName.size() <= 0) {
            return ResMsg.fail(ErrorCode.NO_CITY_NAME, "城市不存在");
        }

        String cityId = String.valueOf(regionByCityName.get(0).get("cityId"));
        PageHelper.startPage(pageIndex, pageNum);
        List<Map<String, Object>> movieList = movieMapper.getMovieByCityId(cityId);
        // 处理电影类型和电影版本
        for (Map<String, Object> map : movieList) {
            map.put("type", MovieFanUtil.replaceType(String.valueOf(map.get("type")), true));
            map.put("showVersionList", MovieFanUtil.replaceType(String.valueOf(map.get("showVersionList")), false));
        }
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(movieList);
        return ResMsg.succWithData(pageInfo);
    }

    public ResMsg getFilm(String filmId) {
        List<Map<String, Object>> filmById = movieMapper.getFilmById(filmId);
        // 处理电影类型和电影版本
        for (Map<String, Object> map : filmById) {
            map.put("type", MovieFanUtil.replaceType(String.valueOf(map.get("type")), true));
            map.put("showVersionList", MovieFanUtil.replaceType(String.valueOf(map.get("showVersionList")), false));
        }
        return ResMsg.succWithData(filmById);
    }

    public ResMsg getFilmByCinema(String cinemaId, String date) {
        List<Map<String, Object>> cinemaList = cinemaMapper.getCinemaByCinemaId(cinemaId);
        if (null == cinemaList || cinemaList.size() <= 0) {
            return ResMsg.fail(ErrorCode.NO_CINEMA_ID, "无此影院");
        }

        List<Map<String, Object>> filmByCinema = movieMapper.getFilmByCinema(cinemaId, date);

        // 处理电影类型和电影版本
        for (Map<String, Object> map : filmByCinema) {
            map.put("type", MovieFanUtil.replaceType(String.valueOf(map.get("type")), true));
            map.put("showVersionList", MovieFanUtil.replaceType(String.valueOf(map.get("showVersionList")), false));
        }
        return ResMsg.succWithData(filmByCinema);
    }

    public ResMsg markWantWatch(String token, String filmId) {
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }

        List<Map<String, Object>> filmList = movieMapper.getFilmById(filmId);
        if (null == filmList || filmList.size() <= 0) {
            return ResMsg.fail(ErrorCode.NO_FILM_ID, "无此电影");
        }

        Map<String, Object> user = userList.get(0);
        Map<String, Object> film = filmList.get(0);
        int uid = Integer.valueOf(String.valueOf(user.get("uid")));
        int movieId = Integer.valueOf(String.valueOf(film.get("filmId")));

        if (movieMapper.checkReadyMark(uid, movieId).size() > 0) {
            return ResMsg.fail(ErrorCode.WAS_MARKED, "已标记为想看,请不要重复点击");
        }

        String movieName = String.valueOf(film.get("showName"));
        String moviePoster = String.valueOf(film.get("poster"));
        String movieOpenDay = String.valueOf(film.get("openDay"));
        String username = String.valueOf(user.get("username"));
        String createTime = DateUtil.getNowDateStrByFormatStr(DateUtil.TOTAL_DATE_TIME);
        movieMapper.markWantWatch(uid, movieId, movieName, moviePoster, movieOpenDay, username, createTime);
        return ResMsg.succ();
    }

    public ResMsg getWantWatch(String token, int pageIndex, int pageNum) {
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        int uid = Integer.valueOf(String.valueOf(userList.get(0).get("uid")));
        PageHelper.startPage(pageIndex, pageNum);
        List<Map<String, Object>> wantWatch = movieMapper.getWantWatch(uid);
        for (Map<String, Object> map : wantWatch) {
            map.put("nickName", new String(Base64Utils.decodeFromString(String.valueOf(map.get("nickName")))));
        }
        return ResMsg.succWithData(new PageInfo<>(wantWatch));
    }

    public ResMsg isWantWatch(String token, int filmId) {
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        int uid = Integer.valueOf(String.valueOf(userList.get(0).get("uid")));
        return ResMsg.succWithData(movieMapper.checkReadyMark(uid, filmId).size() > 0 ? true : false);
    }

    public ResMsg search(String text) {
        List<Map<String, Object>> search = movieMapper.search(text);
        for (Map<String, Object> map : search) {
            map.put("type", MovieFanUtil.replaceType(String.valueOf(map.get("type")), true));
            map.put("showVersionList", MovieFanUtil.replaceType(String.valueOf(map.get("showVersionList")), false));
        }
        return ResMsg.succWithData(search);
    }

    public ResMsg getSchedules(int cinemaId, int filmId, String date, int pageIndex, int pageNum) {
        PageHelper.startPage(pageIndex, pageNum);
        List<Map<String, Object>> schedule = movieMapper.getSchedule(cinemaId, filmId, date);
        return ResMsg.succWithData(new PageInfo<>(schedule));
    }

    public ResMsg getWatched(String token, int pageIndex, int pageNum) {
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        int uid = Integer.valueOf(String.valueOf(userList.get(0).get("uid")));

        List<Map<String, Object>> ticketList = movieMapper.getTicketList(uid, TicketService.STATUS_TICKETING_SUCCESS,
                (pageIndex - 1) * pageNum, pageNum, DateUtil.getNowByAddedMinute(60, DateUtil.TOTAL_DATE_TIME));

        List<Map<String, Object>> result = new ArrayList<>();
        Set<String> movieIdSet = new HashSet<>();
        for (Map<String, Object> map : ticketList) {
            movieIdSet.add(String.valueOf(map.get("film_id")));
        }

        for (String movieId : movieIdSet) {
            result.addAll(movieMapper.getFilmById(movieId));
        }
        return ResMsg.succWithData(result);
    }

}
