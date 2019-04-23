package com.shotacon.movie.api.moviefan.entity;

import com.shotacon.movie.utils.old.DateUtil;

import lombok.ToString;

@ToString
//@Table(name = "tihe_moviefan_movie")
public class MovieEntity {

    private long id;

    // (name = "movie_id", nullable = false)
    private int movieID; // 电影ID

    // (name = "movie_name_cn", nullable = false)
    private String movieNameCN; // 电影中文名

    // (name = "movie_name_en", nullable = false)
    private String movieNameEN; // 电影英文名

    // (name = "movie_image", nullable = false)
    private String movieImage; // 图片(在URL前加上http://image.moviefan.com.cn/)

    // (name = "movie_versions", nullable = false)
    private String movieVersions; // 影片版本（2D，3D，IMAX，IMAX3D…….）

    // (name = "intro", nullable = false)
    private String intro; // 影片描述

    // (name = "director", nullable = false)
    private String director; // 导演

    // (name = "actors", nullable = false)
    private String actors; // 演员

    // (name = "movie_types", nullable = false)
    private String movieTypes; // 影片类型

    // (name = "movie_lanages", nullable = false)
    private String movieLanages; // 影片语言

    // (name = "film_length", nullable = false)
    private int filmLength; // 影片时长

    // (name = "release_time", nullable = false)
    private String releaseTime; // 上映时间

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMovieID() {
        return movieID;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    public String getMovieNameCN() {
        return movieNameCN;
    }

    public void setMovieNameCN(String movieNameCN) {
        this.movieNameCN = movieNameCN;
    }

    public String getMovieNameEN() {
        return movieNameEN;
    }

    public void setMovieNameEN(String movieNameEN) {
        this.movieNameEN = movieNameEN;
    }

    public String getMovieImage() {
        return movieImage;
    }

    public void setMovieImage(String movieImage) {
        this.movieImage = movieImage;
    }

    public String getMovieVersions() {
        return movieVersions;
    }

    public void setMovieVersions(String movieVersions) {
        this.movieVersions = movieVersions;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getMovieTypes() {
        return movieTypes;
    }

    public void setMovieTypes(String movieTypes) {
        this.movieTypes = movieTypes;
    }

    public String getMovieLanages() {
        return movieLanages;
    }

    public void setMovieLanages(String movieLanages) {
        this.movieLanages = movieLanages;
    }

    public int getFilmLength() {
        return filmLength;
    }

    public void setFilmLength(int filmLength) {
        this.filmLength = filmLength;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = DateUtil.handlerDateFromMF(releaseTime);
    }

}
