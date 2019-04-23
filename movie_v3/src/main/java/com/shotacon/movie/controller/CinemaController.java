package com.shotacon.movie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shotacon.movie.config.ErrorCode;
import com.shotacon.movie.exception.ValidateException;
import com.shotacon.movie.model.ResMsg;
import com.shotacon.movie.service.CinemaService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v2/api/")
@Api(tags = { "影院操作" })
public class CinemaController extends BaseController {

    @Autowired
    private CinemaService cinemaService;

    @GetMapping("/cinema/byCity")
    @ApiOperation(value = "获取城市影院信息", notes = "获取城市影院信息", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getCinema(
            @ApiParam(value = "城市编号，必填", name = "cityId") @RequestParam(value = "cityId", required = true) String cityId,
            @ApiParam(value = "默认为1", name = "pageIndex") @RequestParam(value = "pageIndex", defaultValue = "1") int pageIndex,
            @ApiParam(value = "默认为6", name = "pageNum") @RequestParam(value = "pageNum", defaultValue = "6") int pageNum,
            @ApiParam(value = "区域，选填", name = "area") @RequestParam(value = "area", required = false) String area,
            @ApiParam(value = "用户纬度，必填", name = "latitude") @RequestParam(value = "latitude", required = true) String latitude,
            @ApiParam(value = "用户经度，必填", name = "longitude") @RequestParam(value = "longitude", required = true) String longitude,
            @ApiParam(value = "根据距离排序，选填，默认为0, 0为升序，1位降序", name = "orderBy") @RequestParam(value = "orderBy", required = false, defaultValue = "0") String orderBy) {
        return cinemaService.getCinema(latitude, longitude, area, cityId, pageIndex, pageNum, orderBy);
    }

    @GetMapping("/cinema/{cinemaId}")
    @ApiOperation(value = "通过影院ID获取影院信息", notes = "通过影院ID获取影院信息", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getCinemaByCinemaId(@PathVariable(value = "cinemaId") String cinemaId) {
        return cinemaService.getCinemaByCinemaId(cinemaId);
    }

    @GetMapping("/cinema/byFilm")
    @ApiOperation(value = "通过城市与电影信息获取影院", notes = "通过城市与电影信息获取影院", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getCinemaByFilm(
            @ApiParam(value = "城市编号，必填", name = "cityId") @RequestParam(value = "cityId", required = true) String cityId,
            @ApiParam(value = "页码，选填，默认为1", name = "pageIndex") @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
            @ApiParam(value = "每页数量,选填,不填默认为6", name = "pageNum") @RequestParam(value = "pageNum", defaultValue = "6", required = false) int pageNum,
            @ApiParam(value = "用户纬度，必填", name = "latitude") @RequestParam(value = "latitude", required = true) String latitude,
            @ApiParam(value = "用户经度，必填", name = "longitude") @RequestParam(value = "longitude", required = true) String longitude,
            @ApiParam(value = "区域，选填", name = "area") @RequestParam(value = "area", required = false) String area,
            @ApiParam(value = "日期，必填，格式为yyyy-MM-dd", name = "date") @RequestParam(value = "date", required = true) String date,
            @ApiParam(value = "电影ID，必填", name = "filmId") @RequestParam(value = "filmId", required = true) String filmId) {
        try {
            validateDate(date, "date", "yyyy-MM-dd");
        } catch (ValidateException e) {
            log.error("Param validate fail: ", e);
            return ResMsg.fail(ErrorCode.PARAM_ERROR, e.getMessage());
        }
        return cinemaService.getCinemaByFilm(latitude, longitude, cityId, area, filmId, date, pageIndex, pageNum);
    }

    @GetMapping("/search1")
    @ApiOperation(value = "搜索影院", notes = "搜索影院", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg searchCinema1(
            @ApiParam(value = "城市名称(例: 北京市), 全词匹配，必填", name = "cityName") @RequestParam(value = "cityName", required = true) String cityName,
            @ApiParam(value = "影院名称, 模糊匹配，必填", name = "cinemaName") @RequestParam(value = "cinemaName", required = true) String cinemaName,
            @ApiParam(value = "用户纬度，必填", name = "latitude") @RequestParam(value = "latitude", required = true) String latitude,
            @ApiParam(value = "用户经度，必填", name = "longitude") @RequestParam(value = "longitude", required = true) String longitude) {
        return cinemaService.searchCinema1(latitude, longitude, cinemaName, cityName);
    }

    @GetMapping("/search2")
    @ApiOperation(value = "搜索影院", notes = "搜索影院", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg searchCinema2(
            @ApiParam(value = "城市ID，必填", name = "cityId") @RequestParam(value = "cityId", required = true) String cityId,
            @ApiParam(value = "影院名称, 模糊匹配，必填", name = "cinemaName") @RequestParam(value = "cinemaName", required = true) String cinemaName,
            @ApiParam(value = "用户纬度，必填", name = "latitude") @RequestParam(value = "latitude", required = true) String latitude,
            @ApiParam(value = "用户经度，必填", name = "longitude") @RequestParam(value = "longitude", required = true) String longitude) {
        return cinemaService.searchCinema2(latitude, longitude, cinemaName, cityId);
    }

}
