package com.shotacon.movie.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shotacon.movie.mapper.BannerMapper;
import com.shotacon.movie.model.ResMsg;

@Service
public class BannerService {

    @Autowired
    BannerMapper bannerMapper;

    public ResMsg getFilmBanner() {
        return ResMsg.succWithData(bannerMapper.getFilmBanner());
    }

    public ResMsg getCinemaBanner() {
        return ResMsg.succWithData(bannerMapper.getCinemaBanner());
    }

}
