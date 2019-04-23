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

 Date: 28/01/2019 22:55:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tihe_wx_order
-- ----------------------------
DROP TABLE IF EXISTS `tihe_wx_order`;
CREATE TABLE `tihe_wx_order` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '商户订单号',
  `amount` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '支付金额',
  `body` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '商品详情',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'LOCKING: 锁座中; LOCK_SUCCESS: 锁座成功; LOCK_FAIL: 锁座失败; UN_LOCK_SUCCESS: 座位解锁成功; UN_LOCK_FAIL: 座位解锁失败; PAYING: 正在支付; PAY_SUCCESS: 支付成功; PAY_CANCEL: 正在支付; PAY_FAIL: 支付失败; TICKETING_SUCCESS: 出票成功; TICKETING_FAIL: 出票失败; TICKETING_DOING: 出票中; TICKETING_RETRYING: 出票重试中',
  `openId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '用户的openid',
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '错误信息',
  `update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_orderno` (`order_no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;
