-- 添加任务取消相关字段到 uav_mission 表
-- 执行前请确保已连接到正确的数据库

USE uav_delivery;

-- 添加取消类型字段（对应 CancelReasonEnum 的 code: 1-7）
ALTER TABLE uav_mission 
ADD COLUMN cancel_type INT COMMENT '取消类型：1=客户取消-计划变更, 2=飞手取消-设备故障, 3=飞手取消-天气恶劣, 4=系统取消-支付超时, 5=客户取消-地址错误, 6=飞手取消-紧急情况, 7=系统取消-超时未接单';

-- 添加操作者类型字段（1=客户, 2=飞手, 3=系统）
ALTER TABLE uav_mission 
ADD COLUMN operator_type INT COMMENT '操作者类型：1=客户, 2=飞手, 3=系统';

-- 添加取消原因描述字段（冗余字段，方便查询）
ALTER TABLE uav_mission 
ADD COLUMN cancel_reason VARCHAR(200) COMMENT '取消原因描述';

-- 添加取消时间字段
ALTER TABLE uav_mission 
ADD COLUMN cancel_time DATETIME COMMENT '取消时间';

-- 添加完成时间字段
ALTER TABLE uav_mission 
ADD COLUMN complete_time DATETIME COMMENT '完成时间';

-- 查看表结构确认
DESC uav_mission;

-- 查询示例：查看所有字段
SELECT * FROM uav_mission LIMIT 1;