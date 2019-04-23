package com.shotacon.movie.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shotacon.movie.config.ErrorCode;
import com.shotacon.movie.exception.MapRequestException;
import com.shotacon.movie.mapper.RegionMapper;
import com.shotacon.movie.model.ResMsg;
import com.shotacon.movie.utils.newapi.WXUtil;

@Service
public class RegionService {

    private static final String[] pinyinArray = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    @Autowired
    RegionMapper regionMapper;

    public ResMsg getAllRegion() {
        return ResMsg.succWithData(regionMapper.getAllRegion());
    }

    public ResMsg getRegionByTrip(String trip, boolean isPinYin) {

        if (isPinYin) {
            return ResMsg.succWithData(regionMapper.getRegionByPinYin(trip.toUpperCase()));
        } else {
            return ResMsg.succWithData(regionMapper.getRegionByNameLike(trip));
        }
    }

    public ResMsg getRegionGroup() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = 0; i < pinyinArray.length; i++) {
            List<Map<String, Object>> regionsByPinyin = regionMapper.getRegionByPinYin(pinyinArray[i]);
            if (regionsByPinyin.size() > 0) {
                Map<String, Object> regionMap = new HashMap<>();
                regionMap.put("pinyin", pinyinArray[i]);
                regionMap.put("cities", regionsByPinyin);
                resultList.add(regionMap);
            }
        }
        return ResMsg.succWithData(resultList);
    }

    public ResMsg getRegionArea(String cityName) {
        List<String> regionArea = regionMapper.getRegionArea(cityName);
        if (regionArea.size() <= 0) {
            return ResMsg.fail(ErrorCode.NO_CITY_AREA, "该城市无下属地区或城市名错误, 请根据区域列表中的中文名称填写, 该字段为全词匹配.");
        }
        return ResMsg.succWithData(regionArea);
    }

    public ResMsg getRegionInfo(String cityName) {
        return ResMsg.succWithData(regionMapper.getRegionByCityName(cityName));
    }

    public ResMsg getAddressInfo(String longitude, String latitude) throws MapRequestException {
        return ResMsg.succWithData(WXUtil.mapInfo(latitude, longitude));
    }

}
