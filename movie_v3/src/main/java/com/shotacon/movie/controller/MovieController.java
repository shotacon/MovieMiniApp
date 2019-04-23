package com.shotacon.movie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shotacon.movie.config.ErrorCode;
import com.shotacon.movie.exception.ValidateException;
import com.shotacon.movie.model.ResMsg;
import com.shotacon.movie.service.MovieService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v2/api/")
@Api(tags = { "电影操作" })
public class MovieController extends BaseController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/film/hot")
    @ApiOperation(value = "获取地区热映电影", notes = "获取地区热映电影", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getHotFilm(
            @ApiParam(value = "城市名称(例如: 北京市), 全词匹配, 必填", name = "cityName") @RequestParam(value = "cityName", required = true) String cityName,
            @ApiParam(value = "选填, 默认为1", name = "pageIndex") @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
            @ApiParam(value = "选填, 默认为6", name = "pageNum") @RequestParam(value = "pageNum", defaultValue = "6", required = false) int pageNum) {
        return movieService.getFilm(cityName, pageIndex, pageNum, "hot");
    }

    @GetMapping("/film/soon")
    @ApiOperation(value = "获取地区即将上映电影", notes = "获取地区即将上映电影", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getSoonFilm(@RequestParam(value = "cityName", required = true) String cityName,
            @ApiParam(value = "选填, 默认为1", name = "pageIndex") @RequestParam(value = "pageIndex", defaultValue = "1") int pageIndex,
            @ApiParam(value = "选填, 默认为6", name = "pageNum") @RequestParam(value = "pageNum", defaultValue = "6") int pageNum) {
        return movieService.getFilm(cityName, pageIndex, pageNum, "soon");
    }

    @GetMapping("/film/{filmId}")
    @ApiOperation(value = "根据电影ID获取电影详情", notes = "根据电影ID获取电影详情", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getFilm(@PathVariable(value = "filmId", required = true) String filmId) {
        return movieService.getFilm(filmId);
    }

    @GetMapping("/film/cinema/{cinemaId}")
    @ApiOperation(value = "根据影院及日期获取上映电影列表", notes = "根据影院及日期获取上映电影列表", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getFilmByCinema(@PathVariable(value = "cinemaId", required = true) String cinemaId,
            @ApiParam(value = "查询日期,该场次上映的日期, 格式: yyyy-mm-dd", name = "date") @RequestParam(value = "date", required = true) String date) {
        try {
            validateDate(date, "date", "yyyy-MM-dd");
        } catch (ValidateException e) {
            log.error("Param validate fail: ", e);
            return ResMsg.fail(ErrorCode.PARAM_ERROR, e.getMessage());
        }
        return movieService.getFilmByCinema(cinemaId, date);
    }

    @PostMapping("/film/mark")
    @ApiOperation(value = "标记电影为想看", notes = "标记电影为想看", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg markWantWatch(@RequestHeader(name = "Authorization") String token,
            @RequestParam(value = "filmId", required = true) String filmId) {
        try {
            validateString(token, "token");
        } catch (ValidateException e) {
            log.error("Param validate fail: ", e);
            return ResMsg.fail(ErrorCode.PARAM_ERROR, e.getMessage());
        }
        return movieService.markWantWatch(token, filmId);
    }

    @GetMapping("/film/mark")
    @ApiOperation(value = "获取想看电影列表", notes = "获取想看电影列表", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getWantWatch(@RequestHeader(name = "Authorization", required = true) String token,
            @ApiParam(value = "选填, 默认为1", name = "pageIndex") @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
            @ApiParam(value = "选填, 默认为6", name = "pageNum") @RequestParam(value = "pageNum", defaultValue = "6", required = false) int pageNum) {
        return movieService.getWantWatch(token, pageIndex, pageNum);
    }

    @GetMapping("/film/watched")
    @ApiOperation(value = "获取看过的电影列表", notes = "获取看过的电影列表", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getWatched(@RequestHeader(name = "Authorization", required = true) String token,
            @ApiParam(value = "选填, 默认为1", name = "pageIndex") @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
            @ApiParam(value = "选填, 默认为6", name = "pageNum") @RequestParam(value = "pageNum", defaultValue = "6", required = false) int pageNum) {
        return movieService.getWatched(token, pageIndex, pageNum);
    }

    @GetMapping("/film/isMark/{filmId}")
    @ApiOperation(value = "是否想看", notes = "是否想看", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg isWantWatch(@RequestHeader(name = "Authorization") String token,
            @PathVariable(value = "filmId", required = true) int filmId) {
        try {
            validateString(token, "token");
        } catch (ValidateException e) {
            log.error("Param validate fail: ", e);
            return ResMsg.fail(ErrorCode.PARAM_ERROR, e.getMessage());
        }
        return movieService.isWantWatch(token, filmId);
    }

    @GetMapping("/film/search")
    @ApiOperation(value = "搜索电影", notes = "搜索电影", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg search(@RequestParam(value = "text", required = true) String text) {
        return movieService.search(text);
    }

    @GetMapping("/schedule")
    @ApiOperation(value = "获取电影排期", notes = "搜索电影", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getSchedules(@RequestParam(value = "cinemaId", required = true) int cinemaId, // 电影院ID
            @ApiParam(value = "影片ID", name = "filmId") @RequestParam(value = "filmId", required = true) int filmId, // 电影ID
            @ApiParam(value = "查询日期,该场次上映的日期, 格式: yyyy-mm-dd", name = "date") @RequestParam(value = "date", required = true) String date, // 查询日期,该场次上映的日期
            @ApiParam(value = "选填, 默认为1", name = "pageIndex") @RequestParam(value = "pageIndex", defaultValue = "1") int pageIndex, // 分页页码,选填,不填默认为1
            @ApiParam(value = "选填, 默认为6", name = "pageNum") @RequestParam(value = "pageNum", defaultValue = "6") int pageNum) {
        return movieService.getSchedules(cinemaId, filmId, date, pageIndex, pageNum);
    }

}
