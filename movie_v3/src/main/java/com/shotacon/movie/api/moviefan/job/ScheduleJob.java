package com.shotacon.movie.api.moviefan.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import com.shotacon.movie.api.moviefan.entity.CinemaEntity;
import com.shotacon.movie.api.moviefan.entity.HallEntity;
import com.shotacon.movie.api.moviefan.entity.ShowsEntity;
import com.shotacon.movie.api.moviefan.exception.BodyHandlerException;
import com.shotacon.movie.api.moviefan.service.MovieFanService;

import lombok.extern.slf4j.Slf4j;

/**
 * 定时任务
 */
@Slf4j
@Component
@EnableAsync
public class ScheduleJob {

    public ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private static volatile boolean isRun = false;

    private static volatile List<HallEntity> hallsList;
    private static volatile List<ShowsEntity> showsList;

    @Autowired
    private MovieFanService movieFanService;

//    @Scheduled(cron = "${job.updateAllJob}")
    @Scheduled(initialDelay = 0, fixedDelay = 1000 * 60 * 60 * 60)
    public boolean updateAllJob() {

        if (isRun) {
            return false;
        }

        isRun = !isRun;

        try {
            // 查询地区信息
            movieFanService.queryLocations();
            
            // 查询影院信息
            List<CinemaEntity> cinemas = movieFanService.queryCinemas();
            movieFanService.insertCinemaBatch(cinemas);
            
            // 查询影片信息
            movieFanService.queryMovies();
            // 院厅和排期
            updateShow(cinemas, true);

            return true;
        } catch (RestClientException | BodyHandlerException e) {
            log.error("updateAllJob error: ", e);
            return false;
        } finally {
            isRun = false;
        }
    }

//    @Scheduled(cron = "${job.updateShowJob}")
//    @Scheduled(initialDelay = 0, fixedDelay = 1000 * 60 * 60 * 60)
    public boolean updateShowJob() {

        if (isRun) {
            return false;
        }

        isRun = !isRun;

        log.info("update show per {}", 30);
        try {
            updateShow(movieFanService.queryCinemas(), false);
            return true;

        } catch (RestClientException | BodyHandlerException e) {
            log.error("updateShowJob error: ", e);
            return false;
        } finally {
            isRun = false;
        }
    }

    /**
     * 拉取排片信息
     * 
     * @param cinemas      影院信息
     * @param isUpdateHall 是否更新院厅信息
     */
    private void updateShow(final List<CinemaEntity> cinemas, final boolean isUpdateHall) {
        long begintime = System.currentTimeMillis();
        log.info("Start updateShowJob As {}", begintime);

        final ExecutorService updateSchedulePool = Executors.newFixedThreadPool(30);
        try {
            // 通过多线程来加速获取影片排片的信息

            final int maxSize = 200;
            final int partCount = cinemas.size() / maxSize + 1;

            hallsList = new ArrayList<>();
            showsList = new ArrayList<>();
            final CountDownLatch latch = new CountDownLatch(partCount);

            log.info("Count {} part of cinemas", partCount);
            for (int i = 0; i < partCount; i++) {
                final int j = i;
                int lastSize = cinemas.size() - (i * maxSize);
                if (lastSize > maxSize) {
                    lastSize = maxSize;
                }
                int fromIndex = i * maxSize;
                int toIndex = fromIndex + lastSize;
                log.info("The {} part", i);
                final List<CinemaEntity> subList = cinemas.subList(fromIndex, toIndex);

                updateSchedulePool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<ShowsEntity> queryShows = new ArrayList<>();
                            List<HallEntity> queryHalls = new ArrayList<>();
                            for (CinemaEntity cinemaEntity : subList) {
                                final int cinemaID = cinemaEntity.getCinemaID();
                                // 根据影院ID查询影厅列表
                                if (isUpdateHall) {
                                    queryHalls.addAll(movieFanService.queryHalls(cinemaID));
                                }
                                // 根据影院ID查询排片
                                queryShows.addAll(movieFanService.queryShows(cinemaID, 0));
                            }
                            synchronized (hallsList) {
                                hallsList.addAll(queryHalls);
                            }
                            synchronized (showsList) {
                                showsList.addAll(queryShows);
                            }
                        } catch (RestClientException | BodyHandlerException e) {
                            log.error("Thread execute queryShows error: ", e);
                        } finally {
                            latch.countDown();
                            log.info("The {} part is down, latch count : {}, showsList size: {}", j, latch.getCount(),
                                    showsList.size());
                        }
                    }
                });
            }
            log.info("Wait all part done");
            latch.await(15, TimeUnit.MINUTES);

            // 处理数据
            synchronized (showsList) {
                if (hallsList.size() > 0) {
                    log.info("insertHallBatch {} begin", hallsList.size());
                    int insertCount = movieFanService.insertHallBatch(hallsList);
                    log.info("insertHallBatch {} data", insertCount);
                }
                if (showsList.size() > 0) {
                    log.info("insertShowBatch {} begin", showsList.size());
                    int insertCount = movieFanService.insertShowBatch(showsList);
                    log.info("insertShowBatch {} end", insertCount);
                }
            }
        } catch (RestClientException | BodyHandlerException | InterruptedException e) {
            long endtime = System.currentTimeMillis();
            log.error("updateShowJob Error As {}, cost {} : {}", endtime, endtime - begintime, e);
        } finally {
            updateSchedulePool.shutdown();
            showsList.clear();
            hallsList.clear();
        }
        long endtime = System.currentTimeMillis();
        log.info("End updateShowJob As {}, cost {} ", endtime, (endtime - begintime) / 60000);
    }

    @Bean
    @PostConstruct
    public AsyncTaskExecutor taskExecutor() {
        threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix("pool-thread");
        threadPoolTaskExecutor.setCorePoolSize(3);
        threadPoolTaskExecutor.setMaxPoolSize(6);
        threadPoolTaskExecutor.setDaemon(true);
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskExecutor.initialize();
        log.info("ThreadPoolTaskExecutor Initialize Success.");
        return threadPoolTaskExecutor;
    }

    @PreDestroy
    public void destroy() {
        if (threadPoolTaskExecutor != null) {
            threadPoolTaskExecutor.shutdown();
            log.info("ThreadPoolTaskExecutor ShutDown.");
        }
    }
}
