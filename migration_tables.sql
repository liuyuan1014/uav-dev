-- =============================================
-- 无人机调度平台 - 数据库迁移脚本
-- 从代驾项目迁移的业务表
-- =============================================

-- 1. 飞手账户表
CREATE TABLE IF NOT EXISTS `pilot_account` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pilot_id` BIGINT NOT NULL COMMENT '飞手ID',
  `total_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '账户总金额',
  `lock_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '锁定金额',
  `available_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '可用金额',
  `total_income_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总收入',
  `total_pay_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总支出',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pilot_id` (`pilot_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='飞手账户表';

-- 2. 飞手设置表
CREATE TABLE IF NOT EXISTS `pilot_settings` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pilot_id` BIGINT NOT NULL COMMENT '飞手ID',
  `service_status` TINYINT NOT NULL DEFAULT 0 COMMENT '服务状态：0-停止服务 1-开始服务',
  `mission_distance` DECIMAL(10,2) DEFAULT 50.00 COMMENT '任务距离设置（公里）',
  `accept_distance` DECIMAL(10,2) DEFAULT 5.00 COMMENT '接单距离设置（公里）',
  `is_auto_accept` TINYINT NOT NULL DEFAULT 0 COMMENT '是否自动接单：0-否 1-是',
  `max_flight_height` INT DEFAULT 500 COMMENT '最大飞行高度（米）',
  `max_flight_distance` DECIMAL(10,2) DEFAULT 10.00 COMMENT '最大飞行距离（公里）',
  `accept_night_mission` TINYINT NOT NULL DEFAULT 0 COMMENT '是否接受夜间任务：0-否 1-是',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pilot_id` (`pilot_id`),
  KEY `idx_service_status` (`service_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='飞手设置表';

-- 3. 飞手账户明细表
CREATE TABLE IF NOT EXISTS `pilot_account_detail` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pilot_id` BIGINT NOT NULL COMMENT '飞手ID',
  `trade_type` TINYINT NOT NULL COMMENT '交易类型：1-收入 2-支出 3-冻结 4-解冻',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '交易金额',
  `balance` DECIMAL(10,2) NOT NULL COMMENT '交易后余额',
  `mission_id` BIGINT DEFAULT NULL COMMENT '关联任务ID',
  `content` VARCHAR(500) DEFAULT NULL COMMENT '交易内容描述',
  `trade_no` VARCHAR(50) DEFAULT NULL COMMENT '交易编号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_pilot_id` (`pilot_id`),
  KEY `idx_mission_id` (`mission_id`),
  KEY `idx_trade_no` (`trade_no`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='飞手账户明细表';

-- 4. 飞手登录日志表
CREATE TABLE IF NOT EXISTS `pilot_login_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pilot_id` BIGINT NOT NULL COMMENT '飞手ID',
  `ip_address` VARCHAR(50) DEFAULT NULL COMMENT '登录IP地址',
  `device_type` TINYINT DEFAULT NULL COMMENT '登录设备类型：1-Android 2-iOS 3-Web',
  `device_model` VARCHAR(100) DEFAULT NULL COMMENT '设备型号',
  `os_version` VARCHAR(50) DEFAULT NULL COMMENT '操作系统版本',
  `app_version` VARCHAR(50) DEFAULT NULL COMMENT 'APP版本',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '登录状态：0-失败 1-成功',
  `fail_reason` VARCHAR(200) DEFAULT NULL COMMENT '失败原因',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_pilot_id` (`pilot_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='飞手登录日志表';

-- 5. 任务账单表
CREATE TABLE IF NOT EXISTS `mission_bill` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `mission_id` BIGINT NOT NULL COMMENT '任务ID',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `pilot_id` BIGINT NOT NULL COMMENT '飞手ID',
  `pay_way` TINYINT DEFAULT NULL COMMENT '支付方式：1-微信 2-支付宝 3-现金',
  `mission_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '任务金额',
  `coupon_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '优惠券金额',
  `pay_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '实付金额',
  `pilot_income` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '飞手收入',
  `platform_income` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '平台收入',
  `pay_status` TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态：0-未支付 1-已支付 2-已退款',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mission_id` (`mission_id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_pilot_id` (`pilot_id`),
  KEY `idx_pay_status` (`pay_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务账单表';

-- 6. 任务评价表
CREATE TABLE IF NOT EXISTS `mission_comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `mission_id` BIGINT NOT NULL COMMENT '任务ID',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `pilot_id` BIGINT NOT NULL COMMENT '飞手ID',
  `rate` TINYINT NOT NULL COMMENT '评分：1-5星',
  `content` VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
  `tags` VARCHAR(200) DEFAULT NULL COMMENT '评价标签（多个标签用逗号分隔）',
  `is_anonymous` TINYINT NOT NULL DEFAULT 0 COMMENT '是否匿名：0-否 1-是',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待审核 1-已通过 2-已拒绝',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mission_id` (`mission_id`),
  KEY `idx_pilot_id` (`pilot_id`),
  KEY `idx_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务评价表';

-- 7. 任务监控表
CREATE TABLE IF NOT EXISTS `mission_monitor` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `mission_id` BIGINT NOT NULL COMMENT '任务ID',
  `pilot_id` BIGINT NOT NULL COMMENT '飞手ID',
  `uav_id` BIGINT DEFAULT NULL COMMENT '无人机ID',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '监控状态：0-正常 1-异常',
  `exception_type` TINYINT DEFAULT NULL COMMENT '异常类型：1-失联 2-电量低 3-超出范围 4-天气异常 5-其他',
  `exception_desc` VARCHAR(500) DEFAULT NULL COMMENT '异常描述',
  `longitude` VARCHAR(20) DEFAULT NULL COMMENT '当前经度',
  `latitude` VARCHAR(20) DEFAULT NULL COMMENT '当前纬度',
  `altitude` INT DEFAULT NULL COMMENT '当前高度（米）',
  `speed` INT DEFAULT NULL COMMENT '当前速度（米/秒）',
  `battery_level` INT DEFAULT NULL COMMENT '电池电量（百分比）',
  `signal_strength` INT DEFAULT NULL COMMENT '信号强度',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_mission_id` (`mission_id`),
  KEY `idx_pilot_id` (`pilot_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务监控表';

-- 8. 任务状态日志表
CREATE TABLE IF NOT EXISTS `mission_status_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `mission_id` BIGINT NOT NULL COMMENT '任务ID',
  `before_status` INT DEFAULT NULL COMMENT '操作前状态',
  `after_status` INT DEFAULT NULL COMMENT '操作后状态',
  `operate_type` TINYINT NOT NULL COMMENT '操作类型：1-创建 2-接单 3-开始 4-完成 5-取消',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
  `operator_type` TINYINT DEFAULT NULL COMMENT '操作人类型：1-客户 2-飞手 3-系统',
  `operate_desc` VARCHAR(500) DEFAULT NULL COMMENT '操作描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_mission_id` (`mission_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务状态日志表';

-- 9. 增强uav_mission表（添加新字段）
ALTER TABLE `uav_mission`
ADD COLUMN `start_point_longitude` DECIMAL(10,7) DEFAULT NULL COMMENT '起点经度' AFTER `start_location`,
ADD COLUMN `start_point_latitude` DECIMAL(10,7) DEFAULT NULL COMMENT '起点纬度' AFTER `start_point_longitude`,
ADD COLUMN `end_point_longitude` DECIMAL(10,7) DEFAULT NULL COMMENT '终点经度' AFTER `end_location`,
ADD COLUMN `end_point_latitude` DECIMAL(10,7) DEFAULT NULL COMMENT '终点纬度' AFTER `end_point_longitude`,
ADD COLUMN `expect_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '预估金额' AFTER `actual_distance`,
ADD COLUMN `real_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '实际金额' AFTER `expect_amount`,
ADD COLUMN `favour_fee` DECIMAL(10,2) DEFAULT NULL COMMENT '客户好处费' AFTER `fee`,
ADD COLUMN `accept_time` DATETIME DEFAULT NULL COMMENT '飞手接单时间' AFTER `version`,
ADD COLUMN `arrive_time` DATETIME DEFAULT NULL COMMENT '飞手到达时间' AFTER `accept_time`,
ADD COLUMN `start_service_time` DATETIME DEFAULT NULL COMMENT '开始服务时间' AFTER `start_time`,
ADD COLUMN `end_service_time` DATETIME DEFAULT NULL COMMENT '结束服务时间' AFTER `end_time`,
ADD COLUMN `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间' AFTER `end_service_time`,
ADD COLUMN `uav_model` VARCHAR(100) DEFAULT NULL COMMENT '无人机型号' AFTER `complete_time`,
ADD COLUMN `uav_serial_number` VARCHAR(100) DEFAULT NULL COMMENT '无人机序列号' AFTER `uav_model`,
ADD COLUMN `uav_front_photo` VARCHAR(500) DEFAULT NULL COMMENT '无人机起飞前照片' AFTER `uav_serial_number`,
ADD COLUMN `uav_landing_photo` VARCHAR(500) DEFAULT NULL COMMENT '无人机降落后照片' AFTER `uav_front_photo`,
ADD COLUMN `transaction_id` VARCHAR(100) DEFAULT NULL COMMENT '微信支付订单号' AFTER `uav_landing_photo`,
ADD COLUMN `cancel_rule_id` BIGINT DEFAULT NULL COMMENT '取消规则ID' AFTER `transaction_id`;

-- 添加索引
ALTER TABLE `uav_mission` 
ADD INDEX IF NOT EXISTS `idx_accept_time` (`accept_time`),
ADD INDEX IF NOT EXISTS `idx_pay_time` (`pay_time`);

-- =============================================
-- 说明：
-- 1. 所有表都包含基础字段：id, create_time, update_time, is_deleted
-- 2. 金额字段统一使用 DECIMAL(10,2) 类型
-- 3. 时间字段统一使用 DATETIME 类型
-- 4. 状态字段统一使用 TINYINT 类型
-- 5. 所有外键关联字段都添加了索引
-- 6. uav_mission表采用ALTER方式添加新字段，避免影响现有数据
-- =============================================