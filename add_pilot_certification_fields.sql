-- 飞手管理功能 - 数据库扩展脚本
-- 执行时间：2026-02-22

-- 1. 扩展 uav_pilot 表
USE uav_delivery;

-- 身份证信息
ALTER TABLE uav_pilot ADD COLUMN id_card_front_img VARCHAR(255) COMMENT '身份证正面照片URL';
ALTER TABLE uav_pilot ADD COLUMN id_card_back_img VARCHAR(255) COMMENT '身份证反面照片URL';

-- 认证审核
ALTER TABLE uav_pilot ADD COLUMN audit_time DATETIME COMMENT '审核时间';
ALTER TABLE uav_pilot ADD COLUMN auditor_id BIGINT COMMENT '审核人ID';
ALTER TABLE uav_pilot ADD COLUMN audit_remark VARCHAR(500) COMMENT '审核备注';
ALTER TABLE uav_pilot ADD COLUMN reject_reason VARCHAR(500) COMMENT '拒绝原因';

-- 飞手评级
ALTER TABLE uav_pilot ADD COLUMN rating DECIMAL(3,2) DEFAULT 5.00 COMMENT '飞手评分（1-5分）';
ALTER TABLE uav_pilot ADD COLUMN total_missions INT DEFAULT 0 COMMENT '累计完成任务数';

-- 2. 创建飞手认证审核记录表
CREATE TABLE IF NOT EXISTS pilot_certification_audit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pilot_id BIGINT NOT NULL COMMENT '飞手ID',
    submit_time DATETIME NOT NULL COMMENT '提交时间',
    audit_time DATETIME COMMENT '审核时间',
    auditor_id BIGINT COMMENT '审核人ID',
    audit_status INT NOT NULL COMMENT '审核状态：0=待审核, 1=审核通过, 2=审核拒绝',
    audit_remark VARCHAR(500) COMMENT '审核备注',
    reject_reason VARCHAR(500) COMMENT '拒绝原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_pilot_id (pilot_id),
    INDEX idx_audit_status (audit_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='飞手认证审核记录表';

-- 3. 在 uav_mission 表添加飞手评分字段
ALTER TABLE uav_mission ADD COLUMN pilot_rating INT COMMENT '飞手评分（1-5分）';