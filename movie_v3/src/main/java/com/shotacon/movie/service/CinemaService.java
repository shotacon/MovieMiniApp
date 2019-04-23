package com.shotacon.movie.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shotacon.movie.config.ErrorCode;
import com.shotacon.movie.mapper.CinemaMapper;
import com.shotacon.movie.mapper.RegionMapper;
import com.shotacon.movie.model.ResMsg;

@Service
public class CinemaService {

    @Autowired
    private CinemaMapper cinemaMapper;
    @Autowired
    private RegionMapper regionMapper;

    public ResMsg getCinema(String latitude, String longitude, String area, String cityId, int pageIndex, int pageNum,
            String orderBy) {

        List<Map<String, Object>> regionByCityName = regionMapper.getRegionByCityId(cityId);
        if (null == regionByCityName || regionByCityName.size() <= 0) {
            return ResMsg.fail(ErrorCode.NO_CITY_NAME, "城市不存在");
        }
        if (StringUtils.isNotBlank(area)) {
            List<String> areaList = regionMapper.getRegionAreaByCityId(area, cityId);
            if (null == areaList || areaList.size() <= 0) {
                return ResMsg.fail(ErrorCode.NO_CITY_AREA, "区域不存在, 或不在该城市范围");
            }
            area = areaList.get(0);
        }

        PageHelper.startPage(pageIndex, pageNum);
        List<Map<String, Object>> cinemaList = cinemaMapper.getCinema(latitude, longitude, area, cityId, orderBy);
//        for (Map<String, Object> map : cinemaList) {
//            Double lat1 = Double.parseDouble(latitude);
//            Double lon1 = Double.parseDouble(longitude);
//            Double lat2 = Double.parseDouble(String.valueOf(map.get("latitude")));
//            Double lon2 = Double.parseDouble(String.valueOf(map.get("longitude")));
//            map.put("distance", IPUtil.getDistance(lon1, lat1, lon2, lat2));
//        }
//        Collections.sort(cinemaList, new Comparator<Map<String, Object>>() {
//
//            @Override
//            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//                Double dis1 = Double.parseDouble(String.valueOf(o1.get("distance")));
//                Double dis2 = Double.parseDouble(String.valueOf(o2.get("distance")));
//                return dis1.compareTo(dis2);
//            }
//
//        });
        return ResMsg.succWithData(new PageInfo<>(cinemaList));

    }

    public ResMsg getCinemaByCinemaId(String cinemaId) {
        return ResMsg.succWithData(cinemaMapper.getCinemaByCinemaId(cinemaId));
    }

    public ResMsg getCinemaByFilm(String latitude, String longitude, String cityId, String area, String filmId,
            String date, int pageIndex, int pageNum) {

        List<Map<String, Object>> regionByCityName = regionMapper.getRegionByCityId(cityId);
        if (null == regionByCityName || regionByCityName.size() <= 0) {
            return ResMsg.fail(ErrorCode.NO_CITY_NAME, "城市不存在");
        }

        if (StringUtils.isNotBlank(area)) {
            List<String> areaList = regionMapper.getRegionAreaByCityId(area, cityId);
            if (null == areaList || areaList.size() <= 0) {
                return ResMsg.fail(ErrorCode.NO_CITY_AREA, "区域不存在, 或不在该城市范围");
            }
            area = areaList.get(0);
        }
        PageHelper.startPage(pageIndex, pageNum);
        List<Map<String, Object>> cinemaList = cinemaMapper.getCinemaByFilm(latitude, longitude, cityId, area, filmId,
                date);
//        for (Map<String, Object> map : cinemaList) {
//            Double lat1 = Double.parseDouble(latitude);
//            Double lon1 = Double.parseDouble(longitude);
//            Double lat2 = Double.parseDouble(String.valueOf(map.get("latitude")));
//            Double lon2 = Double.parseDouble(String.valueOf(map.get("longitude")));
//            map.put("distance", IPUtil.getDistance(lon1, lat1, lon2, lat2));
//        }
//        Collections.sort(cinemaList, new Comparator<Map<String, Object>>() {
//
//            @Override
//            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//                Double dis1 = Double.parseDouble(String.valueOf(o1.get("distance")));
//                Double dis2 = Double.parseDouble(String.valueOf(o2.get("distance")));
//                return dis1.compareTo(dis2);
//            }
//
//        });
        return ResMsg.succWithData(new PageInfo<>(cinemaList));
    }

    public ResMsg searchCinema1(String latitude, String longitude, String cinemaName, String cityName) {
        List<Map<String, Object>> regionByCityName = regionMapper.getRegionByCityName(cityName);
        if (null == regionByCityName || regionByCityName.size() <= 0) {
            return ResMsg.fail(ErrorCode.NO_CITY_NAME, "城市不存在");
        }
        String cityId = String.valueOf(regionByCityName.get(0).get("cityId"));
        List<Map<String, Object>> cinemaList = cinemaMapper.searchCinema1(latitude, longitude, cityId, cinemaName);
//        for (Map<String, Object> map : cinemaList) {
//            Double lat1 = Double.parseDouble(latitude);
//            Double lon1 = Double.parseDouble(longitude);
//            Double lat2 = Double.parseDouble(String.valueOf(map.get("latitude")));
//            Double lon2 = Double.parseDouble(String.valueOf(map.get("longitude")));
//            map.put("distance", IPUtil.getDistance(lon1, lat1, lon2, lat2));
//        }
//        Collections.sort(cinemaList, new Comparator<Map<String, Object>>() {
//
//            @Override
//            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//                Double dis1 = Double.parseDouble(String.valueOf(o1.get("distance")));
//                Double dis2 = Double.parseDouble(String.valueOf(o2.get("distance")));
//                return dis1.compareTo(dis2);
//            }
//
//        });
        return ResMsg.succWithData(cinemaList);
    }

    public ResMsg searchCinema2(String latitude, String longitude, String cinemaName, String cityId) {
        List<Map<String, Object>> regionByCityName = regionMapper.getRegionByCityId(cityId);
        if (null == regionByCityName || regionByCityName.size() <= 0) {
            return ResMsg.fail(ErrorCode.NO_CITY_NAME, "城市不存在");
        }
        List<Map<String, Object>> cinemaList = cinemaMapper.searchCinema1(latitude, longitude, cityId, cinemaName);
//        for (Map<String, Object> map : cinemaList) {
//            Double lat1 = Double.parseDouble(latitude);
//            Double lon1 = Double.parseDouble(longitude);
//            Double lat2 = Double.parseDouble(String.valueOf(map.get("latitude")));
//            Double lon2 = Double.parseDouble(String.valueOf(map.get("longitude")));
//            map.put("distance", IPUtil.getDistance(lon1, lat1, lon2, lat2));
//        }
//        Collections.sort(cinemaList, new Comparator<Map<String, Object>>() {
//
//            @Override
//            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//                Double dis1 = Double.parseDouble(String.valueOf(o1.get("distance")));
//                Double dis2 = Double.parseDouble(String.valueOf(o2.get("distance")));
//                return dis1.compareTo(dis2);
//            }
//
//        });
        return ResMsg.succWithData(cinemaList);
    }

}
