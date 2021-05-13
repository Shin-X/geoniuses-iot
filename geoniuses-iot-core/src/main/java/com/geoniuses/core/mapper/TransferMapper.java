package com.geoniuses.core.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 传输协议配置表
 */
@Mapper
public interface TransferMapper {

    List<Map<String, Object>> getTagBySensorNo(String sensorNo);

    List<Map<String, Object>> getConfigByStationKey(Map<String, Object> params);

    List<Map<String, Object>> getTransfer();

    int updateByTagCode(Map<String, Object> params);

    int existSensorNo(String sonsorNo);

    long updateBatchConfig(Map<String,Object> updateMap);

}
