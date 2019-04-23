/*
 Navicat Premium Data Transfer

 Source Server         : 47.95.117.48
 Source Server Type    : MySQL
 Source Server Version : 80013
 Source Host           : 47.95.117.48:3306
 Source Schema         : movie_1

 Target Server Type    : MySQL
 Target Server Version : 80013
 File Encoding         : 65001

 Date: 28/01/2019 22:54:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tihe_moviefan_cinema
-- ----------------------------
DROP TABLE IF EXISTS `tihe_moviefan_cinema`;
CREATE TABLE `tihe_moviefan_cinema` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `baidu_latitude` decimal(19,2) NOT NULL,
  `baidu_longitude` decimal(19,2) NOT NULL,
  `cinema_address` varchar(255) NOT NULL,
  `cinema_id` int(11) NOT NULL,
  `cinema_name` varchar(255) NOT NULL,
  `cinema_status` int(11) NOT NULL,
  `city` int(11) NOT NULL,
  `county` int(11) NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone` varchar(255) NOT NULL,
  `province` int(11) NOT NULL,
  PRIMARY KEY (`id`,`cinema_id`) USING BTREE,
  UNIQUE KEY `idx_cinema_id` (`cinema_id`),
  KEY `idx_cinema_name` (`cinema_name`)
) ENGINE=InnoDB AUTO_INCREMENT=601269 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for tihe_moviefan_hall
-- ----------------------------
DROP TABLE IF EXISTS `tihe_moviefan_hall`;
CREATE TABLE `tihe_moviefan_hall` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `cinema_id` int(11) NOT NULL,
  `hall_id` int(11) NOT NULL,
  `hall_name` varchar(255) NOT NULL,
  `hall_status` int(11) NOT NULL,
  `hall_type` int(11) NOT NULL,
  `intro` varchar(255) NOT NULL,
  `seat_count` int(11) NOT NULL,
  PRIMARY KEY (`id`,`hall_id`) USING BTREE,
  UNIQUE KEY `idx_id` (`cinema_id`,`hall_id`)
) ENGINE=InnoDB AUTO_INCREMENT=774045 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for tihe_moviefan_location
-- ----------------------------
DROP TABLE IF EXISTS `tihe_moviefan_location`;
CREATE TABLE `tihe_moviefan_location` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `location_id` int(10) NOT NULL DEFAULT '0',
  `parent_id` int(10) NOT NULL DEFAULT '0',
  `location_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `name_cn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `name_en` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `name_pinyin` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `name_pinyin_short` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `name_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `is_hot` int(3) NOT NULL DEFAULT '0',
  `latitude` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `longitude` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`id`,`location_id`) USING BTREE,
  UNIQUE KEY `idx_id` (`location_id`),
  KEY `idx_name_cn` (`name_cn`),
  KEY `idx_name_en` (`name_en`),
  KEY `idx_name_pinyin` (`name_pinyin`),
  KEY `idx_type` (`location_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=262509 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for tihe_moviefan_movie
-- ----------------------------
DROP TABLE IF EXISTS `tihe_moviefan_movie`;
CREATE TABLE `tihe_moviefan_movie` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `actors` varchar(255) NOT NULL,
  `director` varchar(255) NOT NULL,
  `film_length` int(11) NOT NULL,
  `intro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `movie_id` int(11) NOT NULL,
  `movie_image` varchar(255) NOT NULL,
  `movie_lanages` varchar(255) NOT NULL,
  `movie_name_cn` varchar(255) NOT NULL,
  `movie_name_en` varchar(255) NOT NULL,
  `movie_types` varchar(255) NOT NULL,
  `movie_versions` varchar(255) NOT NULL,
  `release_time` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`movie_id`) USING BTREE,
  UNIQUE KEY `idx_movie_id` (`movie_id`),
  KEY `idx_movie_name` (`movie_name_cn`)
) ENGINE=InnoDB AUTO_INCREMENT=6442 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for tihe_moviefan_order
-- ----------------------------
DROP TABLE IF EXISTS `tihe_moviefan_order`;
CREATE TABLE `tihe_moviefan_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `returned_order_status` int(11) NOT NULL,
  `take_ticket_position` varchar(255) NOT NULL,
  `amount` double NOT NULL,
  `cinema_address` varchar(255) NOT NULL,
  `cinema_name` varchar(255) NOT NULL,
  `cinema_ticket_code` varchar(255) NOT NULL,
  `create_time` varchar(255) NOT NULL,
  `end_time` varchar(255) NOT NULL,
  `external_order_status` int(11) NOT NULL,
  `hall_name` varchar(255) NOT NULL,
  `movie_name` varchar(255) NOT NULL,
  `orderexternal_id` varchar(255) NOT NULL,
  `pay_state` int(11) NOT NULL,
  `print_code` varchar(255) NOT NULL,
  `quantity` int(11) NOT NULL,
  `seat_name` varchar(255) NOT NULL,
  `start_time` varchar(255) NOT NULL,
  `verify_code` varchar(255) NOT NULL,
  `qr_code` text,
  PRIMARY KEY (`id`,`orderexternal_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for tihe_moviefan_show_seats
-- ----------------------------
DROP TABLE IF EXISTS `tihe_moviefan_show_seats`;
CREATE TABLE `tihe_moviefan_show_seats` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `col_num` varchar(255) NOT NULL,
  `love_code` varchar(255) NOT NULL,
  `row_num` varchar(255) NOT NULL,
  `seat_id` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `type` int(11) NOT NULL,
  `x_coord` int(11) NOT NULL,
  `y_coord` int(11) NOT NULL,
  PRIMARY KEY (`id`,`seat_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for tihe_moviefan_shows
-- ----------------------------
DROP TABLE IF EXISTS `tihe_moviefan_shows`;
CREATE TABLE `tihe_moviefan_shows` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cinema_id` int(11) NOT NULL DEFAULT '0',
  `hall_id` int(11) NOT NULL DEFAULT '0',
  `hall_name` varchar(255) NOT NULL DEFAULT '',
  `language` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `movie_id` int(11) NOT NULL DEFAULT '0',
  `price` int(11) NOT NULL DEFAULT '0',
  `show_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `showtime_id` int(11) NOT NULL DEFAULT '0',
  `status` int(11) NOT NULL DEFAULT '0',
  `ticket_end_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `ticket_start_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `film_length` int(10) NOT NULL DEFAULT '0',
  `retail_price` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`,`showtime_id`) USING BTREE,
  KEY `idx_query` (`cinema_id`,`movie_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8368958 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;
