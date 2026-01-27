package com.uav.service.telemetry;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 无人机遥测数据仓库
 */
@Repository
public interface UavTelemetryRepository extends MongoRepository<UavTelemetryEntity, String> {
    
    List<UavTelemetryEntity> findByDeviceId(String deviceId);
    
    List<UavTelemetryEntity> findByDeviceIdOrderByTimestampDesc(String deviceId);
}