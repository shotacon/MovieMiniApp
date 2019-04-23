package com.shotacon.movie.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import com.shotacon.movie.api.moviefan.entity.ShowSeatsEntity;
import com.shotacon.movie.api.moviefan.entity.ShowsEntity;
import com.shotacon.movie.api.moviefan.exception.BodyHandlerException;
import com.shotacon.movie.api.moviefan.job.ScheduleJob;
import com.shotacon.movie.api.moviefan.util.MovieFanUtil;
import com.shotacon.movie.model.ResMsg;
import com.shotacon.movie.utils.newapi.SeatUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v2/api/")
@Api(tags = { "一些后台操作" })
public class MovieFanController {

    @Autowired
    private ScheduleJob scheduleJob;

    @GetMapping("/queryShow")
    @ApiOperation(value = "查询指定排期", notes = "查询指定排期", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg queryShow(
            @ApiParam(value = "movieID", name = "movieID") @RequestParam(value = "movieID", required = true) int movieID,
            @ApiParam(value = "cinemaID", name = "cinemaID") @RequestParam(value = "cinemaID", required = true) int cinemaID) {
        List<ShowsEntity> queryShowSeats = new ArrayList<>();
        try {
            queryShowSeats = MovieFanUtil.queryShows(cinemaID, movieID);
        } catch (RestClientException | BodyHandlerException e) {
            e.printStackTrace();
        }
        return ResMsg.succWithData(queryShowSeats);
    }

    @GetMapping("/seatHandler")
    @ApiOperation(value = "展示座位图", notes = "展示座位图", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg showSeat(
            @ApiParam(value = "排期ID", name = "showid") @RequestParam(value = "showid", required = true) String showid) {
        List<ShowSeatsEntity> queryShowSeats = new ArrayList<>();
        try {
            queryShowSeats = MovieFanUtil.queryShowSeats(showid, null);
        } catch (RestClientException | BodyHandlerException e) {
            e.printStackTrace();
        }
        return ResMsg.succWithData(SeatUtil.printSeat(queryShowSeats));
    }

    @GetMapping("/queryLocations")
    @ApiOperation(value = "查询所有地区", notes = "查询所有地区", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg queryLocations() {

        try {
            return ResMsg.succWithData(MovieFanUtil.queryLocations());
        } catch (RestClientException | BodyHandlerException e) {
            return ResMsg.UnknowWithMsg(e.getMessage());
        }
    }

    @GetMapping("/doAllUpdateJob")
    @ApiOperation(value = "主动调用job, 更新所有信息", notes = "主动调用job, 更新所有信息", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg doAllUpdateJob() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    scheduleJob.updateAllJob();
                }
            });
        } catch (Exception e) {
            log.error("Run doAllUpdateJob Error:", e);
        } finally {
            service.shutdown();
        }
        return ResMsg.succ();
    }

    @GetMapping("/updateShowJob")
    @ApiOperation(value = "主动调用job, 更新影片排期", notes = "主动调用job, 更新影片排期", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg updateShowJob() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    scheduleJob.updateShowJob();
                }
            });
        } catch (Exception e) {
            log.error("Run updateShowJob Error:", e);
        } finally {
            service.shutdown();
        }
        return ResMsg.succ();
    }
}
