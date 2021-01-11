/*
 Navicat Premium Data Transfer

 Source Server         : 139.9.221.80_3306
 Source Server Type    : MySQL
 Source Server Version : 50729
 Source Host           : 139.9.221.80:3306
 Source Schema         : csmall

 Target Server Type    : MySQL
 Target Server Version : 50729
 File Encoding         : 65001

 Date: 17/12/2020 15:30:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for litemall_activity
-- ----------------------------
DROP TABLE IF EXISTS `litemall_activity`;
CREATE TABLE `litemall_activity`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `activity_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '活动板块名称',
  `pic_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `add_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of litemall_activity
-- ----------------------------
INSERT INTO `litemall_activity` VALUES (1, '精品时装', 'https://img.pgwlxx.com/bar8tvwjasxtczf4n5oo.png', '2020-11-23 14:59:00', NULL);
INSERT INTO `litemall_activity` VALUES (2, '时尚零食', 'https://img.pgwlxx.com/igfjb0n1d18kelnyixku.png', '2020-11-23 15:04:10', NULL);
INSERT INTO `litemall_activity` VALUES (3, '家居百货', 'https://img.pgwlxx.com/7cvo410nroiu86hs6qxy.png', '2020-11-30 17:12:29', NULL);
INSERT INTO `litemall_activity` VALUES (4, '数码家电', 'https://img.pgwlxx.com/ozc713cs5b9qzakdooj5.png', '2020-11-23 15:11:20', NULL);

-- ----------------------------
-- Table structure for litemall_ad
-- ----------------------------
DROP TABLE IF EXISTS `litemall_ad`;
CREATE TABLE `litemall_ad`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '广告标题',
  `link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '所广告的商品页面或者活动页面链接地址',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '广告宣传图片',
  `position` tinyint(3) NULL DEFAULT 1 COMMENT '广告位置：1则是首页',
  `sort_order` tinyint(3) NULL DEFAULT 50 COMMENT '位置顺序',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '活动内容',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '0：商品，1：外部链接',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '广告开始时间',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '广告结束时间',
  `enabled` tinyint(1) NULL DEFAULT 0 COMMENT '是否启动',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `enabled`(`enabled`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '广告表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for litemall_address
-- ----------------------------
DROP TABLE IF EXISTS `litemall_address`;
CREATE TABLE `litemall_address`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '收货人名称',
  `user_id` int(11) NOT NULL DEFAULT 0 COMMENT '用户表的用户ID',
  `province` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '行政区域表的省',
  `city` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '行政区域表的市',
  `county` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '行政区域表的区县',
  `street` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '街道',
  `address_detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '详细收货地址',
  `area_code` char(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地区编码',
  `postal_code` char(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮政编码',
  `tel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '手机号码',
  `is_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '收货地址表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for litemall_admin
-- ----------------------------
DROP TABLE IF EXISTS `litemall_admin`;
CREATE TABLE `litemall_admin`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '管理员名称',
  `password` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '管理员密码',
  `last_login_ip` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '最近一次登录IP地址',
  `last_login_time` datetime(0) NULL DEFAULT NULL COMMENT '最近一次登录时间',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '\'' COMMENT '头像图片',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  `role_ids` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '[]' COMMENT '角色列表',
  `status` int(1) NULL DEFAULT 0 COMMENT '0管理员，1工厂账号',
  `plant_id` int(11) NULL DEFAULT NULL COMMENT '若status为1必须填写',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of litemall_admin
-- ----------------------------
INSERT INTO `litemall_admin` VALUES (1, 'admin123', '$2a$10$yJJrvLCDEeGL3z7xYmM.sefVSvbsOfD1YeRlnDW7bF//Bnwk.iknG', '183.12.236.9', '2020-12-17 14:26:15', 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif', '2018-02-01 00:00:00', '2020-12-17 14:26:15', 0, '[1]', 0, NULL);
INSERT INTO `litemall_admin` VALUES (2, 'devops', '$2a$10$RGrXaMJEJHY.xL8bCRTvh.P7D1sdDBxKlPXgn6DH9IkxfDZvIM9sC', '121.35.186.180', '2020-12-17 12:30:07', 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif', '2018-02-01 00:00:00', '2020-12-17 12:30:07', 0, '[1]', 0, NULL);

-- ----------------------------
-- Table structure for litemall_aftersale
-- ----------------------------
DROP TABLE IF EXISTS `litemall_aftersale`;
CREATE TABLE `litemall_aftersale`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户账号',
  `aftersale_sn` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '售后编号',
  `order_id` int(11) NOT NULL COMMENT '订单id',
  `plant_id` int(11) NOT NULL COMMENT '工厂ID',
  `order_goods_id` int(11) NULL DEFAULT NULL COMMENT '订单子表id',
  `service_type` int(11) NULL DEFAULT NULL COMMENT '服务类型 （1.仅退款,    2.退货退款）',
  `cargo_status` int(11) NULL DEFAULT NULL COMMENT '货物状态（1.未收到货，2已收到货）',
  `reason` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '退款原因',
  `amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '退款金额',
  `pictures` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '[]' COMMENT '退款凭证图片链接数组',
  `comment` varchar(511) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '退款说明',
  `courier_number` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '快递单号',
  `courier_company` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '快递公司',
  `status` int(6) NULL DEFAULT 0 COMMENT '退款状态（0未申请，1后台允许退货，2后台驳回申请，3后台确认收货通过审核(退款中)，4后台审核驳回，5.退款成功，6.退款失败，7.用户取消，11待收货申请，12已完成申请）',
  `reject_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '驳回原因',
  `handle_time` datetime(0) NULL DEFAULT NULL COMMENT '管理员操作时间',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '添加时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  `pay_id` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支付时上游单号',
  `last_operator` int(11) NULL DEFAULT NULL COMMENT '操作人id',
  `refund_id` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上游退款单号',
  `confirm_return` tinyint(1) NULL DEFAULT 0 COMMENT '是否收到退货',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '售后表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for litemall_alliance
-- ----------------------------
DROP TABLE IF EXISTS `litemall_alliance`;
CREATE TABLE `litemall_alliance`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户表ID',
  `alliance_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '加盟商名称',
  `alliance_card_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '加盟商身份证',
  `alliance_pic` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片保存路径',
  `group_id` int(11) NULL DEFAULT NULL COMMENT '所属分组',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属地址',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for litemall_alliance_group
-- ----------------------------
DROP TABLE IF EXISTS `litemall_alliance_group`;
CREATE TABLE `litemall_alliance_group`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分组名称',
  `group_address` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分组地区',
  `profits_pro` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分润比例',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_alliance_sale
-- ----------------------------
DROP TABLE IF EXISTS `litemall_alliance_sale`;
CREATE TABLE `litemall_alliance_sale`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `alliance_name` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '加盟真实姓名 or 系统',
  `alliance_userid` int(11) NULL DEFAULT -1 COMMENT '加盟商在用户表ID，注：-1为系统',
  `order_sn` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单号',
  `order_goods_id` int(11) NULL DEFAULT NULL COMMENT '订单子表id',
  `category_id` int(11) NULL DEFAULT NULL COMMENT '商品所属类目id',
  `goods_name` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `goods_number` int(11) NOT NULL COMMENT '销售数量',
  `goods_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '销售单价',
  `plant_cost_price` decimal(10, 2) NOT NULL COMMENT '工厂成本单价',
  `coupon_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '优惠金额',
  `total_profit` decimal(10, 2) NOT NULL COMMENT '总利润=数量*（ 销售单价-成本单价 ）-优惠金额  小于0，也为0处理',
  `group_id` int(11) NULL DEFAULT NULL COMMENT '分组id',
  `plant_id` int(11) NULL DEFAULT NULL COMMENT '工厂id',
  `add_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 ',
  `order_close_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单关闭时间',
  `order_add_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '加盟商销售数据表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for litemall_brand
-- ----------------------------
DROP TABLE IF EXISTS `litemall_brand`;
CREATE TABLE `litemall_brand`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '品牌商名称',
  `desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '品牌商简介',
  `pic_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '品牌商页的品牌商图片',
  `sort_order` tinyint(3) NULL DEFAULT 50,
  `floor_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '品牌商的商品低价，仅用于页面展示',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '品牌商表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_cart
-- ----------------------------
DROP TABLE IF EXISTS `litemall_cart`;
CREATE TABLE `litemall_cart`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户表的用户ID',
  `goods_id` int(11) NULL DEFAULT NULL COMMENT '商品表的商品ID',
  `goods_code_id` int(11) NULL DEFAULT NULL COMMENT '商品编码id',
  `goods_name` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '商品货品的价格',
  `number` smallint(5) NULL DEFAULT 0 COMMENT '商品货品的数量',
  `spec_code_id` int(11) NULL DEFAULT NULL COMMENT '规格编码货品表ID',
  `specifications` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品规格值列表，采用JSON数组格式',
  `checked` tinyint(1) NULL DEFAULT 1 COMMENT '购物车中商品是否选择状态',
  `pic_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品图片或者商品货品图片',
  `is_on_sale` tinyint(1) NULL DEFAULT 1 COMMENT '是否上架',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '购物车商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_category
-- ----------------------------
DROP TABLE IF EXISTS `litemall_category`;
CREATE TABLE `litemall_category`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '类目名称',
  `keywords` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '类目关键字，以JSON数组格式',
  `desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '类目广告语介绍',
  `pid` int(11) NULL DEFAULT 0 COMMENT '父类目ID',
  `icon_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '类目图标',
  `pic_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '类目图片',
  `level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'L1',
  `sort_order` tinyint(3) NULL DEFAULT 0 COMMENT '排序',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `parent_id`(`pid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '类目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_collect
-- ----------------------------
DROP TABLE IF EXISTS `litemall_collect`;
CREATE TABLE `litemall_collect`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT 0 COMMENT '用户表的用户ID',
  `value_id` int(11) NOT NULL DEFAULT 0 COMMENT '如果type=0，则是商品ID；如果type=1，则是专题ID',
  `type` tinyint(3) NOT NULL DEFAULT 0 COMMENT '收藏类型，如果type=0，则是商品ID；如果type=1，则是专题ID',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `goods_id`(`value_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_comment
-- ----------------------------
DROP TABLE IF EXISTS `litemall_comment`;
CREATE TABLE `litemall_comment`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value_id` int(11) NOT NULL DEFAULT 0 COMMENT '如果type=0，则是商品评论；如果是type=1，则是专题评论。',
  `type` tinyint(3) NOT NULL DEFAULT 0 COMMENT '评论类型，如果type=0，则是商品评论；如果是type=1，则是专题评论；',
  `content` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '评论内容',
  `admin_content` varchar(511) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '管理员回复内容',
  `user_id` int(11) NOT NULL DEFAULT 0 COMMENT '用户表的用户ID',
  `has_picture` tinyint(1) NULL DEFAULT 0 COMMENT '是否含有图片',
  `pic_urls` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片地址列表，采用JSON数组格式',
  `star` smallint(6) NULL DEFAULT 1 COMMENT '评分， 1-5',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `id_value`(`value_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_coupon
-- ----------------------------
DROP TABLE IF EXISTS `litemall_coupon`;
CREATE TABLE `litemall_coupon`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '优惠券名称',
  `desc` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '优惠券介绍，通常是显示优惠券使用限制文字',
  `tag` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '优惠券标签，例如新人专用',
  `total` int(11) NOT NULL DEFAULT 0 COMMENT '优惠券数量，如果是0，则是无限量',
  `discount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '优惠金额，',
  `min` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '最少消费金额才能使用优惠券。',
  `limit` smallint(6) NULL DEFAULT 1 COMMENT '用户领券限制数量，如果是0，则是不限制；默认是1，限领一张.',
  `type` smallint(6) NULL DEFAULT 0 COMMENT '优惠券赠送类型，如果是0则通用券，用户领取；如果是1，则是注册赠券；如果是2，则是优惠券码兑换；',
  `status` smallint(6) NULL DEFAULT 0 COMMENT '优惠券状态，如果是0则是正常可用；如果是1则是过期; 如果是2则是下架。',
  `goods_type` smallint(6) NULL DEFAULT 0 COMMENT '商品限制类型，如果0则全商品，如果是1则是类目限制，如果是2则是商品限制。',
  `goods_value` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '[]' COMMENT '商品限制值，goods_type如果是0则空集合，如果是1则是类目集合，如果是2则是商品集合。',
  `code` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '优惠券兑换码',
  `time_type` smallint(6) NULL DEFAULT 0 COMMENT '有效时间限制，如果是0，则基于领取时间的有效天数days；如果是1，则start_time和end_time是优惠券有效期；',
  `days` smallint(6) NULL DEFAULT 0 COMMENT '基于领取时间的有效天数days。',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '使用券开始时间',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '使用券截至时间',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `code`(`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '优惠券信息及规则表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of litemall_coupon
-- ----------------------------
INSERT INTO `litemall_coupon` VALUES (1, '新用户优惠卷', '全场通用', '无限制', 0, 30.00, 0.00, 1, 1, 0, 0, '[]', NULL, 0, 365, NULL, NULL, '2020-10-13 10:36:11', '2020-10-13 10:36:14', 0);
INSERT INTO `litemall_coupon` VALUES (2, '回赠优惠卷', '全场通用', '无限制', 0, 10.00, 0.00, 1, 0, 0, 0, '[]', NULL, 0, 365, NULL, NULL, '2020-10-13 10:37:39', '2020-10-13 10:37:41', 0);

-- ----------------------------
-- Table structure for litemall_coupon_user
-- ----------------------------
DROP TABLE IF EXISTS `litemall_coupon_user`;
CREATE TABLE `litemall_coupon_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `coupon_id` int(11) NOT NULL COMMENT '优惠券ID',
  `status` smallint(6) NULL DEFAULT 0 COMMENT '使用状态, 如果是0则未使用；如果是1则已使用；如果是2则已过期；如果是3则已经下架；',
  `used_time` datetime(0) NULL DEFAULT NULL COMMENT '使用时间',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '有效期开始时间',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '有效期截至时间',
  `pay_order_sn` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支付订单号',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '优惠券用户使用表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_feedback
-- ----------------------------
DROP TABLE IF EXISTS `litemall_feedback`;
CREATE TABLE `litemall_feedback`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT 0 COMMENT '用户表的用户ID',
  `username` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户名称',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '手机号',
  `feed_type` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '反馈类型',
  `content` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '反馈内容',
  `status` int(3) NOT NULL DEFAULT 0 COMMENT '状态',
  `has_picture` tinyint(1) NULL DEFAULT 0 COMMENT '是否含有图片',
  `pic_urls` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片地址列表，采用JSON数组格式',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `id_value`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '意见反馈表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_footprint
-- ----------------------------
DROP TABLE IF EXISTS `litemall_footprint`;
CREATE TABLE `litemall_footprint`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT 0 COMMENT '用户表的用户ID',
  `goods_id` int(11) NOT NULL DEFAULT 0 COMMENT '浏览商品ID',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户浏览足迹表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_goods
-- ----------------------------
DROP TABLE IF EXISTS `litemall_goods`;
CREATE TABLE `litemall_goods`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_sn` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品编号',
  `name` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品名称',
  `category_id` int(11) NULL DEFAULT 0 COMMENT '商品所属类目ID',
  `brand_id` int(11) NULL DEFAULT 0,
  `gallery` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品宣传图片列表，采用JSON数组格式',
  `keywords` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '商品关键字，采用逗号间隔',
  `brief` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '商品简介',
  `is_on_sale` tinyint(1) NULL DEFAULT 1 COMMENT '是否上架',
  `sort_order` smallint(4) NULL DEFAULT 100,
  `pic_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品页面商品图片',
  `share_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品MP4视频地址',
  `is_new` tinyint(1) NULL DEFAULT 0 COMMENT '是否新品首发，如果设置则可以在新品首发页面展示',
  `is_hot` tinyint(1) NULL DEFAULT 0 COMMENT '是否人气推荐，如果设置则可以在人气推荐页面展示',
  `is_home` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否首页展示',
  `home_sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '首页展示排序',
  `unit` varchar(31) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '件' COMMENT '商品单位，例如件、盒',
  `counter_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '专柜价格',
  `retail_price` decimal(10, 2) NULL DEFAULT 100000.00 COMMENT '零售价格',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '商品详细介绍，是富文本格式',
  `sales` int(11) NULL DEFAULT 0 COMMENT '销售数量',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  `goods_code_id` int(11) NULL DEFAULT NULL COMMENT '商品编码id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `goods_sn`(`goods_sn`) USING BTREE,
  INDEX `cat_id`(`category_id`) USING BTREE,
  INDEX `brand_id`(`brand_id`) USING BTREE,
  INDEX `sort_order`(`sort_order`) USING BTREE,
  INDEX `addTime`(`add_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1000000 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品基本信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_goods_activity
-- ----------------------------
DROP TABLE IF EXISTS `litemall_goods_activity`;
CREATE TABLE `litemall_goods_activity`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `activity_id` int(11) NOT NULL COMMENT '活动板块ID',
  `goods_id` int(11) NOT NULL COMMENT '商品ID',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `activityGoodsUnique`(`activity_id`, `goods_id`) USING BTREE COMMENT '活动下商品唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for litemall_goods_attribute
-- ----------------------------
DROP TABLE IF EXISTS `litemall_goods_attribute`;
CREATE TABLE `litemall_goods_attribute`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) NOT NULL DEFAULT 0 COMMENT '商品表的商品ID',
  `attribute` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品参数名称',
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品参数值',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `goods_id`(`goods_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品参数表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for litemall_goods_code
-- ----------------------------
DROP TABLE IF EXISTS `litemall_goods_code`;
CREATE TABLE `litemall_goods_code`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_code` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '商品编码',
  `code_name` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '编码名称',
  `add_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `goods_code`(`goods_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品编码表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_goods_product
-- ----------------------------
DROP TABLE IF EXISTS `litemall_goods_product`;
CREATE TABLE `litemall_goods_product`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) NOT NULL DEFAULT 0 COMMENT '商品表的商品ID',
  `specifications` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品规格值列表，采用JSON数组格式',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品货品价格',
  `number` int(11) NOT NULL DEFAULT 0 COMMENT '商品货品数量',
  `url` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品货品图片',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `goods_id`(`goods_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品货品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_goods_spec
-- ----------------------------
DROP TABLE IF EXISTS `litemall_goods_spec`;
CREATE TABLE `litemall_goods_spec`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_code_id` int(11) NOT NULL DEFAULT 0 COMMENT '商品编码',
  `spec` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品规格名称',
  `value` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品规格值',
  `add_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `goods_code`(`goods_code_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品规格表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_goods_specification
-- ----------------------------
DROP TABLE IF EXISTS `litemall_goods_specification`;
CREATE TABLE `litemall_goods_specification`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) NOT NULL DEFAULT 0 COMMENT '商品表的商品ID',
  `specification` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品规格名称',
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品规格值',
  `pic_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品规格图片',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `goods_id`(`goods_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品规格表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_groupon
-- ----------------------------
DROP TABLE IF EXISTS `litemall_groupon`;
CREATE TABLE `litemall_groupon`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL COMMENT '关联的订单ID',
  `groupon_id` int(11) NULL DEFAULT 0 COMMENT '如果是开团用户，则groupon_id是0；如果是参团用户，则groupon_id是团购活动ID',
  `rules_id` int(11) NOT NULL COMMENT '团购规则ID，关联litemall_groupon_rules表ID字段',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `share_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '团购分享图片地址',
  `creator_user_id` int(11) NOT NULL COMMENT '开团用户ID',
  `creator_user_time` datetime(0) NULL DEFAULT NULL COMMENT '开团时间',
  `status` smallint(6) NULL DEFAULT 0 COMMENT '团购活动状态，开团未支付则0，开团中则1，开团失败则2',
  `add_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '团购活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_groupon_rules
-- ----------------------------
DROP TABLE IF EXISTS `litemall_groupon_rules`;
CREATE TABLE `litemall_groupon_rules`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) NOT NULL COMMENT '商品表的商品ID',
  `goods_name` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
  `pic_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品图片或者商品货品图片',
  `discount` decimal(63, 0) NOT NULL COMMENT '优惠金额',
  `discount_member` int(11) NOT NULL COMMENT '达到优惠条件的人数',
  `expire_time` datetime(0) NULL DEFAULT NULL COMMENT '团购过期时间',
  `status` smallint(6) NULL DEFAULT 0 COMMENT '团购规则状态，正常上线则0，到期自动下线则1，管理手动下线则2',
  `add_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `goods_id`(`goods_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '团购规则表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_issue
-- ----------------------------
DROP TABLE IF EXISTS `litemall_issue`;
CREATE TABLE `litemall_issue`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `question` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '问题标题',
  `answer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '问题答案',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '常见问题表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_keyword
-- ----------------------------
DROP TABLE IF EXISTS `litemall_keyword`;
CREATE TABLE `litemall_keyword`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `keyword` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '关键字',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '关键字的跳转链接',
  `is_hot` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否是热门关键字',
  `is_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否是默认关键字',
  `sort_order` int(11) NOT NULL DEFAULT 100 COMMENT '排序',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '关键字表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_log
-- ----------------------------
DROP TABLE IF EXISTS `litemall_log`;
CREATE TABLE `litemall_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `admin` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '管理员',
  `ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '管理员地址',
  `type` int(11) NULL DEFAULT NULL COMMENT '操作分类',
  `action` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作动作',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '操作状态',
  `result` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作结果，或者成功消息，或者失败消息',
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '补充信息',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_node_relation
-- ----------------------------
DROP TABLE IF EXISTS `litemall_node_relation`;
CREATE TABLE `litemall_node_relation`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `ancestor` int(10) NOT NULL DEFAULT 0 COMMENT '祖先节点',
  `descendant` int(10) NOT NULL DEFAULT 0 COMMENT '后代节点',
  `distance` tinyint(4) NOT NULL DEFAULT 0 COMMENT '相隔层级，>=1',
  `node_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0为普通用户，1为加盟商',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_anc_desc`(`ancestor`, `descendant`) USING BTREE,
  INDEX `idx_desc`(`descendant`) USING BTREE,
  INDEX `ancestor`(`ancestor`) USING BTREE COMMENT '祖先节点索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '邀请节点关系表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for litemall_notice
-- ----------------------------
DROP TABLE IF EXISTS `litemall_notice`;
CREATE TABLE `litemall_notice`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通知标题',
  `content` varchar(511) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通知内容',
  `admin_id` int(11) NULL DEFAULT 0 COMMENT '创建通知的管理员ID，如果是系统内置通知则是0.',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_notice_admin
-- ----------------------------
DROP TABLE IF EXISTS `litemall_notice_admin`;
CREATE TABLE `litemall_notice_admin`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `notice_id` int(11) NULL DEFAULT NULL COMMENT '通知ID',
  `notice_title` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通知标题',
  `admin_id` int(11) NULL DEFAULT NULL COMMENT '接收通知的管理员ID',
  `read_time` datetime(0) NULL DEFAULT NULL COMMENT '阅读时间，如果是NULL则是未读状态',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_order
-- ----------------------------
DROP TABLE IF EXISTS `litemall_order`;
CREATE TABLE `litemall_order`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户表的用户ID',
  `order_sn` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单编号',
  `pay_order_sn` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付订单编号，用于支付提交参数',
  `order_status` smallint(6) NULL DEFAULT NULL COMMENT '订单状态：101 订单生成，未支付；102，下单未支付用户取消；103，下单未支付超期系统自动取消 201 支付完成，商家未发货；202，订单生产，已付款未发货，用户申请退款；203，管理员执行退款操作，确认退款成功；\r\n301 商家发货，用户未确认；\r\n401 用户确认收货，订单结束； 402 系统自动确认收货，订单结束。501订单关闭',
  `consignee` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收货人名称',
  `mobile` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收货人手机号',
  `address` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收货具体地址',
  `goods_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品总费用',
  `freight_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '配送费用',
  `coupon_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '优惠金额',
  `order_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单费用， = goods_price + freight_price - coupon_price',
  `actual_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '实付费用， = order_price - integral_price',
  `pay_id` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '付款单号',
  `pay_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '付款方式',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '付款时间',
  `ship_sn` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发货编号',
  `ship_channel` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发货快递公司',
  `ship_time` datetime(0) NULL DEFAULT NULL COMMENT '发货开始时间',
  `delivery_status` tinyint(1) NULL DEFAULT 0 COMMENT '物流状态：0未发货，1已发货',
  `reminder_shipment` tinyint(1) NULL DEFAULT 0 COMMENT '是否提醒发货，0未提醒，1已提醒',
  `confirm_time` datetime(0) NULL DEFAULT NULL COMMENT '用户确认收货时间',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '订单关闭时间，超时无法申请售后',
  `add_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  `plant_id` int(11) NULL DEFAULT NULL COMMENT '工厂id',
  `is_benefit` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0代表需要进行分润的，1：已分润，或不需要分润',
  `alliance_userid` int(11) NULL DEFAULT -1 COMMENT '所属加盟商在用户表ID，注：-1为系统',
  `message` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户订单留言',
  `order_refund_status` smallint(6) NOT NULL DEFAULT 0 COMMENT '退款状态：0未申请，1全部申请退款，2部分申请退款，3退款中，4.部分退款，5全部退款，6.退款失败，7.退款申请部分驳回，8退款申请全部驳回',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_sn`(`order_sn`) USING BTREE COMMENT '订单号唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for litemall_order_goods
-- ----------------------------
DROP TABLE IF EXISTS `litemall_order_goods`;
CREATE TABLE `litemall_order_goods`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_sn` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '订单表的订单编号',
  `is_refund` tinyint(1) NULL DEFAULT 0 COMMENT '是否退款，0未退款，1退款',
  `goods_id` int(11) NOT NULL DEFAULT 0 COMMENT '商品表的商品ID',
  `goods_code_id` int(11) NULL DEFAULT NULL COMMENT '商品编码ID',
  `plant_id` int(11) NULL DEFAULT NULL COMMENT '工厂id',
  `goods_name` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品名称',
  `goods_sn` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品编号',
  `product_id` int(11) NOT NULL DEFAULT 0 COMMENT '规格编码ID',
  `number` smallint(5) NOT NULL DEFAULT 0 COMMENT '商品货品的购买数量',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品货品的售价',
  `goods_cost_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '货品具体工厂的成本价',
  `coupon_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '优惠金额',
  `specifications` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品货品的规格列表',
  `pic_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品货品图片或者商品图片',
  `comment` int(11) NULL DEFAULT 0 COMMENT '订单商品评论，如果是-1，则超期不能评价；如果是0，则可以评价；如果其他值，则是comment表里面的评论ID。',
  `add_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  `afterSale_status` int(6) NOT NULL DEFAULT 0 COMMENT '退款状态（0未申请，1后台允许退货，2后台驳回申请，3后台确认收货通过审核(退款中)，4后台审核驳回，5.退款成功，6.退款失败，7.用户取消，11待收货申请，12已完成申请）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `order_sn`(`order_sn`) USING BTREE,
  INDEX `goods_id`(`goods_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_permission
-- ----------------------------
DROP TABLE IF EXISTS `litemall_permission`;
CREATE TABLE `litemall_permission`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) NULL DEFAULT NULL COMMENT '角色ID',
  `permission` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of litemall_permission
-- ----------------------------
INSERT INTO `litemall_permission` VALUES (1, 1, '*', '2020-11-12 00:00:00', '2020-11-12 00:00:00', 0);

-- ----------------------------
-- Table structure for litemall_plant
-- ----------------------------
DROP TABLE IF EXISTS `litemall_plant`;
CREATE TABLE `litemall_plant`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `plant_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '工厂名称',
  `plant_contacts` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工厂联系人',
  `plant_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工厂电话',
  `plant_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工厂地址',
  `send_contacts` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '发货联系人',
  `send_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '发货电话',
  `send_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '发货地址',
  `receive_contacts` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '退货联系人',
  `receive_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '退货电话',
  `receive_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '退货地址',
  `add_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_product_factory
-- ----------------------------
DROP TABLE IF EXISTS `litemall_product_factory`;
CREATE TABLE `litemall_product_factory`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_code_id` int(11) NOT NULL COMMENT '商品编码id',
  `spec_code_id` int(11) NOT NULL DEFAULT 0 COMMENT '规格编码ID',
  `factory_id` int(11) NOT NULL DEFAULT 0 COMMENT '工厂ID',
  `buy_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '进货价',
  `cost_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '成本价格',
  `goods_stock` int(11) NOT NULL DEFAULT 0 COMMENT '商品库存',
  `warn_stock` int(11) NOT NULL DEFAULT 0 COMMENT '预警库存',
  `add_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `spec_code`(`spec_code_id`) USING BTREE,
  INDEX `goods_code`(`goods_code_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '规格编码工厂表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_region
-- ----------------------------
DROP TABLE IF EXISTS `litemall_region`;
CREATE TABLE `litemall_region`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pid` int(11) NOT NULL DEFAULT 0 COMMENT '行政区域父ID，例如区县的pid指向市，市的pid指向省，省的pid则是0',
  `name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '行政区域名称',
  `type` tinyint(3) NOT NULL DEFAULT 0 COMMENT '行政区域类型，如如1则是省， 如果是2则是市，如果是3则是区县',
  `code` int(11) NOT NULL DEFAULT 0 COMMENT '行政区域编码',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `parent_id`(`pid`) USING BTREE,
  INDEX `region_type`(`type`) USING BTREE,
  INDEX `agency_id`(`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '行政区域表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_role
-- ----------------------------
DROP TABLE IF EXISTS `litemall_role`;
CREATE TABLE `litemall_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `desc` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色描述',
  `enabled` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name_UNIQUE`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of litemall_role
-- ----------------------------
INSERT INTO `litemall_role` VALUES (1, '超级管理员', '所有模块的权限', 1, '2019-01-01 00:00:00', '2019-01-01 00:00:00', 0);

-- ----------------------------
-- Table structure for litemall_search_history
-- ----------------------------
DROP TABLE IF EXISTS `litemall_search_history`;
CREATE TABLE `litemall_search_history`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户表的用户ID',
  `keyword` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '搜索关键字',
  `from` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '搜索来源，如pc、wx、app',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '搜索历史表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_specode_product
-- ----------------------------
DROP TABLE IF EXISTS `litemall_specode_product`;
CREATE TABLE `litemall_specode_product`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_code_id` int(11) NOT NULL DEFAULT 0 COMMENT '商品编码',
  `spec_code` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '规格编码',
  `specs_desc` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品规格值列表，采用JSON数组格式',
  `shop_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '售价',
  `url` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '商品货品图片',
  `total_stock` int(12) NULL DEFAULT 0 COMMENT '货品总库存',
  `add_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `spec_code`(`spec_code`, `goods_code_id`) USING BTREE,
  INDEX `goods_code`(`goods_code_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '规格编码货品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_stock_record
-- ----------------------------
DROP TABLE IF EXISTS `litemall_stock_record`;
CREATE TABLE `litemall_stock_record`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_code` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '商品编码',
  `code_name` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编码名称',
  `specs_code` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '规格编码',
  `specs_desc` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品货品的规格列表',
  `plant_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `number` int(11) NULL DEFAULT 0 COMMENT '库存',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品图片',
  `status` int(11) NOT NULL COMMENT '状态（1：出库，2入库）',
  `add_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `operator` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'system' COMMENT '操作员',
  `remarks` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '库存记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_storage
-- ----------------------------
DROP TABLE IF EXISTS `litemall_storage`;
CREATE TABLE `litemall_storage`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件的唯一索引',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件类型',
  `size` int(11) NOT NULL COMMENT '文件大小',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件访问链接',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `key`(`key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件存储表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_system
-- ----------------------------
DROP TABLE IF EXISTS `litemall_system`;
CREATE TABLE `litemall_system`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '系统配置名',
  `key_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '系统配置值',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of litemall_system
-- ----------------------------
INSERT INTO `litemall_system` VALUES (1, 'litemall_order_unpaid', '30', '2020-10-28 16:57:17', '2020-10-28 16:57:17', 0);
INSERT INTO `litemall_system` VALUES (2, 'litemall_wx_index_new', '6', '2020-10-28 16:57:17', '2020-10-28 16:57:17', 0);
INSERT INTO `litemall_system` VALUES (3, 'litemall_mall_latitude', '31.201900', '2020-10-28 16:57:17', '2020-10-28 16:57:17', 0);
INSERT INTO `litemall_system` VALUES (4, 'litemall_order_unconfirm', '7', '2020-10-28 16:57:17', '2020-10-28 16:57:17', 0);
INSERT INTO `litemall_system` VALUES (5, 'litemall_wx_share', 'false', '2020-10-28 16:57:17', '2020-10-28 16:57:17', 0);
INSERT INTO `litemall_system` VALUES (6, 'litemall_express_freight_min', '0', '2020-10-28 16:57:17', '2020-10-28 16:57:17', 0);
INSERT INTO `litemall_system` VALUES (7, 'litemall_mall_name', 'litemall', '2020-10-28 16:57:17', '2020-10-28 16:57:17', 0);
INSERT INTO `litemall_system` VALUES (8, 'litemall_customer_phone', '18124751870', '2020-10-28 16:57:17', '2020-12-05 16:45:18', 0);
INSERT INTO `litemall_system` VALUES (9, 'litemall_express_freight_value', '0', '2020-10-28 16:57:18', '2020-10-28 16:57:18', 0);
INSERT INTO `litemall_system` VALUES (10, 'litemall_mall_qq', '705144434', '2020-10-28 16:57:18', '2020-10-28 16:57:18', 0);
INSERT INTO `litemall_system` VALUES (11, 'litemall_customer_qq', '3090432293', '2020-10-28 16:57:18', '2020-12-05 16:45:18', 0);
INSERT INTO `litemall_system` VALUES (12, 'litemall_wx_index_hot', '20', '2020-10-28 16:57:18', '2020-12-07 09:05:22', 0);
INSERT INTO `litemall_system` VALUES (13, 'litemall_customer_wx', 'pangu202001', '2020-10-28 16:57:18', '2020-12-05 16:45:18', 0);
INSERT INTO `litemall_system` VALUES (14, 'litemall_order_comment', '7', '2020-10-28 16:57:18', '2020-10-28 16:57:18', 0);
INSERT INTO `litemall_system` VALUES (15, 'litemall_wx_catlog_goods', '4', '2020-10-28 16:57:18', '2020-10-28 16:57:18', 0);
INSERT INTO `litemall_system` VALUES (16, 'litemall_mall_longitude', '121.587839', '2020-10-28 16:57:18', '2020-10-28 16:57:18', 0);
INSERT INTO `litemall_system` VALUES (17, 'litemall_mall_phone', '021-xxxx-xxxx', '2020-10-28 16:57:18', '2020-10-28 16:57:18', 0);
INSERT INTO `litemall_system` VALUES (18, 'litemall_wx_catlog_list', '4', '2020-10-28 16:57:18', '2020-10-28 16:57:18', 0);
INSERT INTO `litemall_system` VALUES (19, 'litemall_mall_address', '上海', '2020-10-28 16:57:18', '2020-10-28 16:57:18', 0);
INSERT INTO `litemall_system` VALUES (20, 'litemall_wx_index_brand', '4', '2020-10-28 16:57:18', '2020-10-28 16:57:18', 0);
INSERT INTO `litemall_system` VALUES (21, 'litemall_wx_index_topic', '4', '2020-10-28 16:57:18', '2020-10-28 16:57:18', 0);
INSERT INTO `litemall_system` VALUES (22, 'litemall_order_close', '7', '2020-11-20 11:13:53', '2020-11-20 11:13:58', 0);
INSERT INTO `litemall_system` VALUES (23, 'litemall_customer_email', '3090432293@qq.com', '2020-12-07 17:16:20', '2020-12-07 17:16:22', 0);

-- ----------------------------
-- Table structure for litemall_topic
-- ----------------------------
DROP TABLE IF EXISTS `litemall_topic`;
CREATE TABLE `litemall_topic`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '\'' COMMENT '专题标题',
  `subtitle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '\'' COMMENT '专题子标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '专题内容，富文本格式',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '专题相关商品最低价',
  `read_count` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1k' COMMENT '专题阅读量',
  `pic_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '专题图片',
  `sort_order` int(11) NULL DEFAULT 100 COMMENT '排序',
  `goods` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '专题相关商品，采用JSON数组格式',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `topic_id`(`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '专题表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for litemall_user
-- ----------------------------
DROP TABLE IF EXISTS `litemall_user`;
CREATE TABLE `litemall_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名称',
  `password` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户密码',
  `gender` tinyint(3) NOT NULL DEFAULT 0 COMMENT '性别：0 未知， 1男， 1 女',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `last_login_time` datetime(0) NULL DEFAULT NULL COMMENT '最近一次登录时间',
  `last_login_ip` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '最近一次登录IP地址',
  `user_level` tinyint(3) NULL DEFAULT 0 COMMENT '0 普通用户，1 加盟商',
  `nickname` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户昵称或网络名称',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户手机号码',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户头像图片',
  `weixin_openid` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '微信登录openid',
  `session_key` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '微信登录会话KEY',
  `token` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'APP的token',
  `status` tinyint(3) NOT NULL DEFAULT 0 COMMENT '0 可用, 1 禁用, 2 注销',
  `add_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  `invitation_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邀请码',
  `referral_user_id` int(11) NULL DEFAULT NULL COMMENT '推荐人id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_name`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;


SET FOREIGN_KEY_CHECKS = 1;
