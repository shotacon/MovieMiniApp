package com.shotacon.movie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shotacon.movie.config.ErrorCode;
import com.shotacon.movie.exception.MapRequestException;
import com.shotacon.movie.exception.ValidateException;
import com.shotacon.movie.model.ResMsg;
import com.shotacon.movie.service.RegionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v2/api/")
@Api(tags = { "地区操作" })
public class RegionController extends BaseController {

    @Autowired
    private RegionService regionService;

    @GetMapping("/region")
    @ApiOperation(value = "获取全部区域", notes = "获取全部区域", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getAllRegion() {
        return regionService.getAllRegion();
    }

    @GetMapping("/region/tip")
    @ApiOperation(value = "根据输入获取提示列表", notes = "根据输入获取提示列表", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getRegionByTrip(@RequestParam(value = "trip", required = true) String trip) {
        try {
            trip = validateString(trip, "text");
        } catch (ValidateException e) {
            log.error("Param validate fail: ", e);
            return ResMsg.fail(ErrorCode.PARAM_ERROR, e.getMessage());
        }
        String pattern = "^[A-Za-z0-9]+$";
        boolean isPinYin = trip.matches(pattern);
        return regionService.getRegionByTrip(trip, isPinYin);
    }

    @GetMapping("/region/group")
    @ApiOperation(value = "根据首字母分组获取城市列表", notes = "根据首字母分组获取城市列表", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getRegionGroup() {
        return regionService.getRegionGroup();
    }

    @GetMapping("/region/area")
    @ApiOperation(value = "获取城市下属所有地区", notes = "获取城市下属所有地区", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getRegionArea(
            @ApiParam(value = "城市名称(例如: 北京市, 河北省), 全词匹配, 必填", name = "cityName") @RequestParam(value = "cityName", required = true) String cityName) {
        return regionService.getRegionArea(cityName);
    }

    @GetMapping("/region/info")
    @ApiOperation(value = "获取城市信息", notes = "获取城市信息", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getRegionInfo(
            @ApiParam(value = "城市名称(例如: 北京市, 河北省), 全词匹配, 必填", name = "cityName") @RequestParam(value = "cityName", required = true) String cityName) {
        return regionService.getRegionInfo(cityName);
    }

    @GetMapping("/region/address")
    @ApiOperation(value = "经纬度获取地理信息", notes = "经纬度获取地理信息", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getAddressInfo(@RequestParam(value = "longitude", required = true) String longitude,
            @RequestParam(value = "latitude", required = true) String latitude) {
        try {
            return regionService.getAddressInfo(longitude, latitude);
        } catch (MapRequestException e) {
            log.error("Map request error: ", e);
            return ResMsg.fail(ErrorCode.NET_ERROR, e.getMessage());
        }
    }

}
