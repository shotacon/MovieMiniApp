package com.shotacon.movie.api.moviefan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.shotacon.movie.api.moviefan.constant.MovieFanConstants;
import com.shotacon.movie.api.moviefan.entity.CinemaEntity;
import com.shotacon.movie.api.moviefan.entity.HallEntity;
import com.shotacon.movie.api.moviefan.entity.LocationEntity;
import com.shotacon.movie.api.moviefan.entity.MovieEntity;
import com.shotacon.movie.api.moviefan.entity.OrderEntity;
import com.shotacon.movie.api.moviefan.entity.ShowsEntity;
import com.shotacon.movie.api.moviefan.exception.BodyHandlerException;
import com.shotacon.movie.api.moviefan.mapper.MovieFanMapper;
import com.shotacon.movie.api.moviefan.util.MovieFanUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 电影票友逻辑类
 * 
 * @author shotacon
 *
 */
@Slf4j
@Service
public class MovieFanService {

    @Autowired
    private MovieFanMapper movieFanMapper;

//    private static final String tempPath = "/data/log/tempfile";

    /**
     * 查询订单
     * 
     * @param orderExternalID
     * @return
     * @throws RestClientException
     * @throws BodyHandlerException
     */
    public OrderEntity queryOrder(String orderExternalID) throws RestClientException, BodyHandlerException {
        List<OrderEntity> orderList = movieFanMapper.queryOrder(orderExternalID);

        OrderEntity queryOrder = MovieFanUtil.queryOrder(orderExternalID);
        if (null == orderList || orderList.size() <= 0) {
            movieFanMapper.insertOrder(queryOrder);
        } else {
            movieFanMapper.updateOrder(queryOrder);
        }
        return queryOrder;
    }

    /**
     * 查询开展了业务或是提供影讯信息的所有地区信息
     * 
     * @throws RestClientException
     * @throws BodyHandlerException
     */
    public List<LocationEntity> queryLocations() throws RestClientException, BodyHandlerException {
        List<LocationEntity> result = MovieFanUtil.queryLocations();
        if (null == result || result.size() <= 0) {
            return result;
        }
        log.info("LocationEntity size: {}", result.size());
        deleteLocationBatch();
        movieFanMapper.insertLocationBatch(result);
        return result;
    }

    /**
     * 删除所有地区信息
     * 
     * @return
     */
    public int deleteLocationBatch() {
        int delete = movieFanMapper.deleteLocationBatch();
        log.info("Delete Locations : {} ", delete);
        return delete;
    }

    /**
     * 分页查询所有影院列表（每页100条）
     * 
     * @throws RestClientException
     * @throws BodyHandlerException
     */
    public List<CinemaEntity> queryCinemas() throws RestClientException, BodyHandlerException {
        List<CinemaEntity> result = MovieFanUtil.queryCinemas();
        if (null == result || result.size() <= 0) {
            return result;
        }
        log.info("CinemaEntity size: {}", result.size());
        return result;
    }

    public int insertCinemaBatch(List<CinemaEntity> result) throws RestClientException, BodyHandlerException {
        if (null == result || result.size() <= 0) {
            return 0;
        }
        deleteCinemaBatch();
        return movieFanMapper.insertCinemaBatch(result);
    }

    /**
     * 删除所有影院信息
     * 
     * @return
     */
    public int deleteCinemaBatch() {
        int delete = movieFanMapper.deleteCinemaBatch();
        log.info("Delete Cinemas : {} ", delete);
        return delete;
    }

    /**
     * 查询所有影片信息
     * 
     * @throws RestClientException
     * @throws BodyHandlerException
     */
    public List<MovieEntity> queryMovies() throws RestClientException, BodyHandlerException {
        List<MovieEntity> result = MovieFanUtil.queryMovies();
        if (null == result || result.size() <= 0) {
            return result;
        }
        log.info("MovieEntity size: {}", result.size());
        deleteMovieBatch();
        movieFanMapper.insertMovieBatch(result);
        return result;
    }

    /**
     * 删除所有影片信息
     * 
     * @return
     */
    public int deleteMovieBatch() {
        int delete = movieFanMapper.deleteMovieBatch();
        log.info("Delete Movies : {} ", delete);
        return delete;
    }

    /**
     * 查询影院影厅列表
     * 
     * @throws RestClientException
     * @throws BodyHandlerException
     */
    public List<HallEntity> queryHalls(int cinemaID) throws RestClientException, BodyHandlerException {
        List<HallEntity> result = MovieFanUtil.queryHalls(cinemaID);
        if (null == result || result.size() <= 0) {
            return result;
        }
        log.debug("HallEntity size: {}", result.size());
        return result;
    }

    public int insertHallBatch(List<HallEntity> result) throws RestClientException, BodyHandlerException {
        int maxSize = MovieFanConstants.insertSize;
        int size = result.size();
        deleteHallBatch();
        if (size > maxSize) {
            // 分批处理
            int partCount = size / maxSize + 1;
            int insertCount = 0;
            log.info("Insert maxSize: {} ", maxSize);
            for (int i = 0; i < partCount; i++) {
                int lastSize = size - (i * maxSize);
                if (lastSize > maxSize) {
                    lastSize = maxSize;
                }
                int fromIndex = i * maxSize;
                int toIndex = fromIndex + lastSize;
                log.info("Insert {}th part, from index {} to index {} ", i + 1, fromIndex, toIndex);
                insertCount += movieFanMapper.insertHallBatch(result.subList(fromIndex, toIndex));
            }
            return insertCount;
        } else {
            return movieFanMapper.insertHallBatch(result);
        }
    }

    /**
     * 删除所有影院影厅列表
     * 
     * @throws RestClientException
     * @throws BodyHandlerException
     */
    public int deleteHallBatch() throws RestClientException, BodyHandlerException {
        int delete = movieFanMapper.deleteHallBatch();
        log.info("Delete Halls : {} ", delete);
        return delete;
    }

    /**
     * 删除指定影厅
     * 
     * @param cinemaID
     * @return
     */
    public int deleteHallByCinemaID(int cinemaID) {
        int delete = movieFanMapper.deleteHallByCinemaID(cinemaID);
        log.info("Delete Halls : {} ", delete);
        return delete;
    }

    /**
     * 查询指定影院、指定影片的放映场次列表
     * 
     * @throws RestClientException
     * @throws BodyHandlerException
     */
    public List<ShowsEntity> queryShows(int cinemaID, int movieID) throws RestClientException, BodyHandlerException {
        List<ShowsEntity> result = MovieFanUtil.queryShows(cinemaID, movieID);
        if (null == result || result.size() <= 0) {
            return result;
        }
        log.info("ShowsEntity size: {}", result.size());
        return result;
    }

    public int insertShowBatch(List<ShowsEntity> result) throws RestClientException, BodyHandlerException {
        int maxSize = MovieFanConstants.insertSize;
        int size = result.size();
        deleteShowBatch();
        if (size > maxSize) {
            // 分批处理
            int partCount = size / maxSize + 1;
            int insertCount = 0;
            log.info("Insert maxSize: {} ", maxSize);
            for (int i = 0; i < partCount; i++) {
                int lastSize = size - (i * maxSize);
                if (lastSize > maxSize) {
                    lastSize = maxSize;
                }
                int fromIndex = i * maxSize;
                int toIndex = fromIndex + lastSize;
                log.info("Insert {}th part, from index {} to index {} ", i + 1, fromIndex, toIndex);
                insertCount += movieFanMapper.insertShowBatch(result.subList(fromIndex, toIndex));
            }
            return insertCount;
        } else {
            return movieFanMapper.insertShowBatch(result);
        }
    }

    /**
     * 删除所有排片信息
     * 
     * @return
     */
    public int deleteShowBatch() {
        int delete = movieFanMapper.deleteShowBatch();
        log.info("Delete Shows : {} ", delete);
        return delete;
    }

    /**
     * 删除指定排片信息
     * 
     * @param cinemaID
     * @param movieID
     * @return
     */
    public int deleteShowSpecified(int cinemaID, int movieID) {
        int delete = movieFanMapper.deleteShowSpecified(cinemaID, movieID);
        log.info("Delete Shows : {} ", delete);
        return delete;
    }

    public int updateShows(List<ShowsEntity> result) {
        int count = 0;
        for (ShowsEntity showsEntity : result) {
            count += movieFanMapper.updateShows(showsEntity);
        }
        return count;
    }

}
