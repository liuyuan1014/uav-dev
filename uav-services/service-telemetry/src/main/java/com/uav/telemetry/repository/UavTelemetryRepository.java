package com.uav.telemetry.repository;

import com.uav.telemetry.entity.UavTelemetryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 无人机遥测数据仓库
 */
@Repository
public interface UavTelemetryRepository extends MongoRepository<UavTelemetryEntity, String> {

    // 根据设备ID查询所有数据
    List<UavTelemetryEntity> findByDeviceId(String deviceId);

    // 根据设备ID倒序查询所有数据
    List<UavTelemetryEntity> findByDeviceIdOrderByTimestampDesc(String deviceId);

    // 根据设备ID和时间范围查询数据
    @Query("{ 'deviceId': ?0, 'timestamp': { $gte: ?1, $lte: ?2 }}")
    List<UavTelemetryEntity> findByDeviceIdAndTimestampBetweenOrderByTimestampAsc(String deviceId, Long startTime, Long endTime);
}