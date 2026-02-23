-- =============================================
-- 无人机派单系统数据库表结构
-- 创建时间: 2026-02-23
-- 说明: 包含任务调度、执行日志、位置历史三张表
-- =============================================

-- 1. 任务调度关联表
-- 用于关联无人机任务与XXL-JOB调度任务
DROP TABLE IF EXISTS `mission_job`;
CREATE TABLE `mission_job` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `mission_id` bigint NOT NULL COMMENT '任务ID',
  `job_id` int NOT NULL COMMENT 'XXL-JOB任务ID',
  `job_group` int NOT NULL COMMENT 'XXL-JOB执行器组ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-运行中，2-已停止',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mission_id` (`mission_id`) COMMENT '任务ID唯一索引',
  KEY `idx_job_id` (`job_id`) COMMENT 'XXL-JOB任务ID索引',
  KEY `idx_status` (`status`) COMMENT '状态索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务调度关联表';

-- 2. XXL-JOB执行日志表
-- 用于记录派单任务的执行日志
DROP TABLE IF EXISTS `xxl_job_log`;
CREATE TABLE `xxl_job_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `job_group` int NOT NULL COMMENT '执行器组ID',
  `job_id` int NOT NULL COMMENT '任务ID',
  `executor_address` varchar(255) DEFAULT NULL COMMENT '执行器地址，如：http://127.0.0.1:9999',
  `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler名称',
  `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
  `trigger_time` datetime DEFAULT NULL COMMENT '调度时间',
  `trigger_code` int DEFAULT NULL COMMENT '调度结果码：200-成功，500-失败',
  `trigger_msg` text COMMENT '调度日志',
  `handle_time` datetime DEFAULT NULL COMMENT '执行时间',
  `handle_code` int DEFAULT NULL COMMENT '执行结果码：200-成功，500-失败',
  `handle_msg` text COMMENT '执行日志',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_job_id` (`job_id`) COMMENT '任务ID索引',
  KEY `idx_trigger_time` (`trigger_time`) COMMENT '调度时间索引',
  KEY `idx_handle_code` (`handle_code`) COMMENT '执行结果码索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='XXL-JOB执行日志表';

-- 3. 飞手位置历史表
-- 用于记录飞手的位置变化历史
DROP TABLE IF EXISTS `pilot_location_history`;
CREATE TABLE `pilot_location_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pilot_id` bigint NOT NULL COMMENT '飞手ID',
  `longitude` decimal(10,7) NOT NULL COMMENT '经度，范围：-180.0000000 到 180.0000000',
  `latitude` decimal(10,7) NOT NULL COMMENT '纬度，范围：-90.0000000 到 90.0000000',
  `address` varchar(255) DEFAULT NULL COMMENT '地址描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_pilot_id` (`pilot_id`) COMMENT '飞手ID索引',
  KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引',
  KEY `idx_pilot_time` (`pilot_id`, `create_time`) COMMENT '飞手ID和时间联合索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='飞手位置历史表';

-- =============================================
-- 初始化数据（可选）
-- =============================================

-- 插入测试数据示例（根据实际需要取消注释）
-- INSERT INTO `mission_job` (`mission_id`, `job_id`, `job_group`, `status`) VALUES (1001, 1, 1, 1);

-- =============================================
-- 索引说明
-- =============================================
-- mission_job表:
--   - uk_mission_id: 确保每个任务只能有一个调度任务
--   - idx_job_id: 加速通过XXL-JOB任务ID查询
--   - idx_status: 加速按状态筛选

-- xxl_job_log表:
--   - idx_job_id: 加速查询特定任务的执行日志
--   - idx_trigger_time: 加速按时间范围查询日志
--   - idx_handle_code: 加速查询失败的执行记录

-- pilot_location_history表:
--   - idx_pilot_id: 加速查询特定飞手的位置历史
--   - idx_create_time: 加速按时间范围查询
--   - idx_pilot_time: 加速查询特定飞手在特定时间段的位置

-- =============================================
-- 数据清理建议
-- =============================================
-- 建议定期清理历史数据，避免表过大影响性能：

-- 1. 清理30天前的执行日志
-- DELETE FROM `xxl_job_log` WHERE `create_time` < DATE_SUB(NOW(), INTERVAL 30 DAY);

-- 2. 清理90天前的位置历史
-- DELETE FROM `pilot_location_history` WHERE `create_time` < DATE_SUB(NOW(), INTERVAL 90 DAY);

-- 3. 清理已停止的任务关联记录（谨慎操作）
-- DELETE FROM `mission_job` WHERE `status` = 2 AND `update_time` < DATE_SUB(NOW(), INTERVAL 7 DAY);

-- =============================================
-- 表结构验证
-- =============================================
-- 执行以下SQL验证表是否创建成功：
-- SHOW TABLES LIKE '%mission_job%';
-- SHOW TABLES LIKE '%xxl_job_log%';
-- SHOW TABLES LIKE '%pilot_location_history%';

-- 查看表结构：
-- DESC mission_job;
-- DESC xxl_job_log;
-- DESC pilot_location_history;

-- =============================================
-- 完成
-- =============================================
-- 表创建完成！
-- 接下来请：
-- 1. 启动XXL-JOB调度中心
-- 2. 启动UAV-SERVICE服务
-- 3. 测试派单功能