package com.geoniuses.core.pojo;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.geoniuses.core.config.RedisClusterConfig;
import com.geoniuses.core.mapper.TransferMapper;
import com.geoniuses.core.utils.DateUtil;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

public class ProcessData implements Runnable{

	private TransferMapper transferMapper;

	private Map<String,Object> map;

	private LinkedBlockingQueue deque;

	private RedisClusterConfig redisClusterConfig;

	private static final ObjectMapper om = new ObjectMapper();

    private static final Logger logger = LogManager.getLogger(ProcessData.class);

	public ProcessData(Map<String, Object> map, TransferMapper transferMapper, LinkedBlockingQueue deque, RedisClusterConfig redisClusterConfig) {
		this.map = map;
		this.transferMapper = transferMapper;
		this.deque = deque;
		this.redisClusterConfig = redisClusterConfig;
	}

	public void sendToKafka(Map<String,Object> funData) {
		if (CollectionUtils.isEmpty(funData) || !funData.containsKey("sensorNo")){
			return;
		}
		String topic =funData.get("topic").toString();
		String sensorNo = funData.get("sensorNo").toString();
        String date = funData.containsKey("DT") ? funData.get("DT").toString() : DateUtil.getNowDateSync();
		List<Map<String, Object>> list = getBaseData(redisClusterConfig, transferMapper, sensorNo);
		if (!CollectionUtils.isEmpty(list)){
			for(Map<String,Object> item:list){
				if (item.get("order_num") != null && funData.containsKey(item.get("order_num").toString())){
					IotStationConfig stationConfig = om.convertValue(item, IotStationConfig.class);
					Double monitorValue = Double.parseDouble(funData.get(stationConfig.getOrderNum().toString()).toString());
					Double rangeMax = stationConfig.getRangemaxValue();
					Double rangeMin = stationConfig.getRangeminValue();
					Double level1StartAlarm = stationConfig.getLevel1Start();
					Double level1EndAlarm = stationConfig.getLevel1End();
					Double level2StartAlarm = stationConfig.getLevel2Start();
					Double level2EndAlarm = stationConfig.getLevel2End();
					Double level3StartAlarm = stationConfig.getLevel3Start();
					Double level3EndAlarm = stationConfig.getLevel3End();
					Double lowLevel1StartAlarm = stationConfig.getLowLevel1Start();
					Double lowLevel1EndAlarm = stationConfig.getLowLevel1End();
					Double lowLevel2StartAlarm = stationConfig.getLowLevel2Start();
					Double lowLevel2EndAlarm = stationConfig.getLowLevel2End();
					Double lowLevel3StartAlarm = stationConfig.getLowLevel3Start();
					Double lowLevel3EndAlarm = stationConfig.getLowLevel3End();
					String alarm = stationConfig.getIsAlarm();
					Integer dolowWarn = stationConfig.getDolowWarn();
					//????????????????????????0
					Double lon = stationConfig.getX();
					Double lat = stationConfig.getY();
					if (lon < 0 || lon > 180) {
							lon = 0.0;
					}
					if (lat < 0 || lat > 90) {
						lat = 0.0;
					}
//					Double[] point = {lon, lat};
					//??????mysql?????????????????????
//						int state = 0;
					//es?????????????????? 0 ????????????6????????????
					int levelAlarm = 0;
					if (rangeMax == null || rangeMin == null) {
						return;
					}
						//?????????????????????config???
						if ((Double.parseDouble(monitorValue.toString()) <= rangeMax) && (rangeMin <= Double.parseDouble(monitorValue.toString()))){
							//kafka????????????????????????????????????
							if ((Objects.equals("1", alarm) || Objects.equals("1",dolowWarn)) && rangeMax != null && rangeMin != null && rangeMax >= monitorValue && monitorValue >= rangeMin){
								if (Objects.equals("1",alarm) && (level1StartAlarm!= null && level1EndAlarm != null ) && (level1StartAlarm <= monitorValue &&  monitorValue <= level1EndAlarm)) {
									levelAlarm = 1;
								}else if(Objects.equals("1",alarm) &&  (level2StartAlarm!= null && level2EndAlarm != null ) && (level2StartAlarm <= monitorValue &&  monitorValue <= level2EndAlarm)){
									levelAlarm = 2;
								}else if(Objects.equals("1",alarm) && (level3StartAlarm!= null && level3EndAlarm != null ) && (level3StartAlarm <= monitorValue &&  monitorValue <= level3EndAlarm)){
									levelAlarm = 3;
								}else if(Objects.equals("1",dolowWarn) && (lowLevel3StartAlarm!= null && lowLevel3EndAlarm != null ) && (lowLevel3StartAlarm <= monitorValue &&  monitorValue <= lowLevel3EndAlarm)){
									levelAlarm = -3;
								}else if(Objects.equals("1",dolowWarn) && (lowLevel2StartAlarm!= null && lowLevel2EndAlarm != null ) && (lowLevel2StartAlarm <= monitorValue &&  monitorValue <= lowLevel2EndAlarm)){
									levelAlarm = -2;
								} else if(Objects.equals("1",dolowWarn) && (lowLevel1StartAlarm!= null && lowLevel1EndAlarm != null ) && (lowLevel1StartAlarm <= monitorValue &&  monitorValue <= lowLevel1EndAlarm)){
									levelAlarm = -1;
								}
							}
							//????????????
							if (levelAlarm != 0) {
								Map<String, Object> alertMap = new HashMap<>();
								alertMap.put("tag_code", stationConfig.getTagCode());
								alertMap.put("tag_value", monitorValue.toString());
								alertMap.put("add_time", date);
								alertMap.put("is_send", false);
								alertMap.put("is_set", false);
								alertMap.put("do_people", stationConfig.getCrtUser());
								alertMap.put("do_date", date);
								alertMap.put("crt_user", stationConfig.getCrtUser());
								alertMap.put("talent_id", stationConfig.getTenantId());
								alertMap.put("point", lat + "," + lon);
								alertMap.put("elevation", null == stationConfig.getZ() ? 0 : Float.parseFloat(stationConfig.getZ().toString()));
								alertMap.put("device_key", sensorNo);
								alertMap.put("big_industry_code", stationConfig.getBigIndustryCode());
								alertMap.put("big_industry_name", stationConfig.getBigIndustryName());
								alertMap.put("mid_industry_code", stationConfig.getMidIndustryCode());
								alertMap.put("mid_industry_name", stationConfig.getMidIndustryName());
								alertMap.put("min_industry_code", stationConfig.getMinIndustryCode());
								alertMap.put("min_industry_name", stationConfig.getMinIndustryName());
								alertMap.put("station_name", stationConfig.getStationName());
								alertMap.put("station_key", stationConfig.getStationKey());
								alertMap.put("tag_name", stationConfig.getTagName());
								alertMap.put("units", stationConfig.getUnits());
								alertMap.put("level_now", levelAlarm);
								//?????????kafka
								this.sendkafka(topic, new JSONObject(alertMap).toJSONString());
								alertMap.clear();
							} else {//????????????
								Map<String, Object> historyMap = new HashMap<>();
								historyMap.put("tag_code", stationConfig.getTagCode());
								//??????????????????????????????
								historyMap.put("tag_value", monitorValue.toString());
								historyMap.put("add_time", date);
								historyMap.put("talent_id", stationConfig.getTenantId());
								historyMap.put("point", lat + "," + lon);
								historyMap.put("elevation",stationConfig.getZ() == null ? 0 : Float.parseFloat(stationConfig.getZ().toString()));
								historyMap.put("device_key", sensorNo);
								historyMap.put("big_industry_code", stationConfig.getBigIndustryCode());
								historyMap.put("big_industry_name", stationConfig.getBigIndustryName());
								historyMap.put("mid_industry_code", stationConfig.getMidIndustryCode());
								historyMap.put("mid_industry_name", stationConfig.getMidIndustryName());
								historyMap.put("min_industry_code", stationConfig.getMinIndustryCode());
								historyMap.put("min_industry_name", stationConfig.getMinIndustryName());
								historyMap.put("station_name", stationConfig.getStationName());
								historyMap.put("station_key", stationConfig.getStationKey());
								historyMap.put("tag_name", stationConfig.getTagName());
								historyMap.put("units", stationConfig.getUnits());
								historyMap.put("is_delete", 0);
								historyMap.put("level_now", levelAlarm);
								//?????????kafka
								this.sendkafka(topic, new JSONObject(historyMap).toJSONString());
								historyMap.clear();
							}
							//???????????????
						} else {
							Map<String, Object> abnormalMap = new HashMap<>();
							abnormalMap.put("tag_code", stationConfig.getTagCode());
							abnormalMap.put("tag_value", monitorValue.toString());
							abnormalMap.put("crt_time", date);
							abnormalMap.put("station_key", stationConfig.getStationKey());
							abnormalMap.put("add_time", date);
							abnormalMap.put("talent_id", stationConfig.getTenantId());
							abnormalMap.put("crt_user", stationConfig.getCrtUser());

							abnormalMap.put("is_delete", false);
							abnormalMap.put("rangemin_value", stationConfig.getRangeminValue());
							abnormalMap.put("rangemax_value", stationConfig.getRangemaxValue());
							abnormalMap.put("remark", "?????????");
							abnormalMap.put("units", stationConfig.getUnits());
							abnormalMap.put("device_key", sensorNo);
							//???????????????6
							abnormalMap.put("level_now", levelAlarm);
							//?????????kafka
							this.sendkafka(topic, new JSONObject(abnormalMap).toJSONString());
							abnormalMap.clear();
						}
					}
				}
			}
		list.clear();
	}

	/**
	 * ???redis???mysql?????????????????????
	 * @param redisClusterConfig
	 * @param transferMapper
	 * @param sensorNo
	 * @return
	 */
	private List<Map<String, Object>> getBaseData(RedisClusterConfig redisClusterConfig, TransferMapper transferMapper, String sensorNo) {
		List<Map<String,Object>> list = null;
		//??????redis???????????????key
		Boolean exists = redisClusterConfig.getLettuceConnectionFactory().getClusterConnection().keyCommands().exists(sensorNo.getBytes());
		if (exists){
			// ???redis???????????? ????????????????????????????????????????????????
			byte[] bytes = redisClusterConfig.getLettuceConnectionFactory().getClusterConnection().stringCommands().get(sensorNo.getBytes());
			try {
				list = om.readValue(bytes, List.class);
			} catch (IOException e) {
				e.printStackTrace();
				logger.log(Level.ERROR,"???redis?????????????????????",e);
			}
		}else {
			list = transferMapper.getTagBySensorNo(sensorNo);
			//??????redis???????????????mysql
			//????????????????????????????????????????????????
			if (!CollectionUtils.isEmpty(list)){
				String configListStr = JSONUtils.toJSONString(list);
				redisClusterConfig.getLettuceConnectionFactory().getClusterConnection().stringCommands().set(sensorNo.getBytes(),configListStr.getBytes());
				logger.log(Level.INFO,"??????redis?????????====");
			}
		}
		return list;
	}

	/**
	 * ?????????kafka
	 * @param topic
	 * @param toJSONString
	 */
	private void sendkafka(String topic, String toJSONString) {
//		long starttime = System.currentTimeMillis();
		ProducerRecord record = new ProducerRecord(topic,toJSONString);
//		System.out.println("?????????kafka?????????========================>toJSONString = " + toJSONString);
//		long finalStarttime = starttime;
		Producer producer = null;
		try {
			producer = (Producer) deque.take();
			producer.send(record, (metadata, exception) -> {
//			System.out.println("offset:" + metadata.offset() +
//					"\npartition:" + metadata.partition() +
//					"\ntopic:"+ metadata.topic() +
//					"\nserializedKeySize:" + metadata.serializedKeySize()		+
//					"\nserializedValueSize:" + metadata.serializedValueSize() + "\n");
		   if (exception == null) {
//				   System.out.println("\nreceive asygn ack : " + (System.currentTimeMillis() - finalStarttime) + "ms");
			   }
			});
			deque.put(producer);
		} catch (InterruptedException e) {
			logger.log(Level.ERROR, "kafka producer deque error", e);
			Thread.currentThread().interrupt();
		}
    }

    @Override
	public void run() {
		sendToKafka(map);
	}

//	private void sendToKafka1(Map<String, Object> map) {
//		String str = "{\"do_date\":\"2020-10-20 14:51:00\",\"elevation\":0.0,\"station_name\":\"155???????????????\",\"is_send\":false,\"device_key\":\"155\",\"tag_code\":\"3508211010_011291_00001_1215_1\",\"is_set\":false,\"mid_industry_code\":\"12\",\"tag_name\":\"155???????????????155_????????????\",\"min_industry_code\":\"91\",\"units\":\"\",\"mid_industry_name\":\"??????????????????\",\"point\":\"25.834114,116.335737\",\"tag_value\":\"1.0\",\"do_people\":\"??????\",\"big_industry_name\":\"??????\",\"min_industry_name\":\"????????????\",\"big_industry_code\":\"01\",\"level_now\":1,\"station_key\":\"3508211010_011291_00001\",\"crt_user\":\"??????\",\"add_time\":\"2020-10-20 14:57:13\",\"talent_id\":\"b1e0b14339694df6aea48760caba26d2\"}";
//		for (int i = 0; i < 4; i++) {
//			sendkafka("test_jg",str);
//		}
//	}

}
