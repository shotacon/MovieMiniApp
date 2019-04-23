package com.shotacon.movie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shotacon.movie.model.ResMsg;
import com.shotacon.movie.service.BannerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v2/api/")
@Api(tags = { "Banner操作" })
public class BannerController extends BaseController {

    @Autowired
    private BannerService bannerService;

    @GetMapping("/banner/film")
    @ApiOperation(value = "获取首页Banner图", notes = "获取微信UnionId", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getFilmBanner() {
        return bannerService.getFilmBanner();
    }
    
    @GetMapping("/banner/cinema")
    @ApiOperation(value = "获取影院Banner图", notes = "获取微信UnionId", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getCinemaBanner() {
        return bannerService.getCinemaBanner();
    }

}
