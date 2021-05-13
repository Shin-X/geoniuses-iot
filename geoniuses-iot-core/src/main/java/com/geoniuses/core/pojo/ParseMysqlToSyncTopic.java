package com.geoniuses.core.pojo;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.geoniuses.core.config.RedisClusterConfig;
import com.geoniuses.core.mapper.TransferMapper;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 将解析的数据发送到syncUpdateMysql主题，用于更新mysql
 */
public class ParseMysqlToSyncTopic implements Runnable{

	private TransferMapper transferMapper;

	private Map<String,Object> map;

	private LinkedBlockingQueue deque;

	private RedisClusterConfig redisClusterConfig;

	private static final ObjectMapper om = new ObjectMapper();

	private static final Logger logger = LogManager.getLogger(ProcessData.class);

	public ParseMysqlToSyncTopic(Map<String, Object> map, TransferMapper transferMapper, LinkedBlockingQueue deque, RedisClusterConfig redisClusterConfig) {
		this.map = map;
		this.transferMapper = transferMapper;
		this.deque = deque;
		this.redisClusterConfig = redisClusterConfig;
	}

		public void sendToKafka(Map<String,Object> funData) {
		if (CollectionUtils.isEmpty(funData)){
			return;
		}

		if (!funData.containsKey("sensorNo")){
			return;
		}
		String sensorNo = funData.get("sensorNo").toString();
		String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		if (funData.containsKey("DT")){
			date = funData.get("DT").toString();
		}
		List<Map<String,Object>> list = null;
		//判断redis中是否存在key
		Boolean exists = redisClusterConfig.getLettuceConnectionFactory().getClusterConnection().keyCommands().exists(sensorNo.getBytes());
		if (exists){
			//从redis中取数据 根据传感器编号取出对应所有监测项
			byte[] bytes = redisClusterConfig.getLettuceConnectionFactory().getClusterConnection().stringCommands().get(sensorNo.getBytes());
			try {
				list = om.readValue(bytes, List.class);
			} catch (IOException e) {
				e.printStackTrace();
				logger.log(Level.ERROR,"从redis中取出数据错误",e);
			}
		}else {
			//如果redis中没有查询mysql
			//根据传感器编号取出对应所有监测项
			list = transferMapper.getTagBySensorNo(sensorNo);
			if (!CollectionUtils.isEmpty(list)){
				String configListStr = JSONUtils.toJSONString(list);
				redisClusterConfig.getLettuceConnectionFactory().getClusterConnection().stringCommands().set(sensorNo.getBytes(),configListStr.getBytes());
				logger.log(Level.INFO,"存入redis中成功====");
			}
		}

		if (!CollectionUtils.isEmpty(list)){
			for(Map<String,Object> item:list ){
				if (item.get("order_num") != null && funData.containsKey(item.get("order_num").toString())){
						Double monitorValue = Double.parseDouble(funData.get(item.get("order_num").toString()).toString());
						String tagCode = item.get("tag_code") == null ? null : item.get("tag_code").toString();
						Double level1StartAlarm = item.get("level1_start") == null ? null : Double.parseDouble(item.get("level1_start").toString());
						Double level1EndAlarm = item.get("level1_end") == null ? null : Double.parseDouble(item.get("level1_end").toString());
						Double level2StartAlarm = item.get("level2_start") == null ? null : Double.parseDouble(item.get("level2_start").toString());
						Double level2EndAlarm = item.get("level2_end") == null ? null : Double.parseDouble(item.get("level2_end").toString());
						Double level3StartAlarm = item.get("level3_start") == null ? null : Double.parseDouble(item.get("level3_start").toString());
						Double level3EndAlarm = item.get("level3_end") == null ? null : Double.parseDouble(item.get("level3_end").toString());
						Double rangeMax = item.get("rangemax_value") == null ? null : Double.parseDouble(item.get("rangemax_value").toString());
						Double rangeMin = item.get("rangemin_value") == null ? null : Double.parseDouble(item.get("rangemin_value").toString());
						String alarm = item.get("is_alarm") == null ? null : item.get("is_alarm").toString();
						String dolowWarn = item.get("dolow_warn") == null ? null : item.get("dolow_warn").toString();
						//下行1级报警开始
						Double lowLevel1StartAlarm = item.get("low_level1_start") == null ? null : Double.parseDouble(item.get("low_level1_start").toString());
						Double lowLevel1EndAlarm = item.get("low_level1_end") == null ? null : Double.parseDouble(item.get("low_level1_end").toString());
						//下行2级报警
						Double lowLevel2StartAlarm = item.get("low_level2_start") == null ? null : Double.parseDouble(item.get("low_level2_start").toString());
						Double lowLevel2EndAlarm = item.get("low_level2_end") == null ? null : Double.parseDouble(item.get("low_level2_end").toString());
						//下行3级报警
						Double lowLevel3StartAlarm = item.get("low_level3_start") == null ? null : Double.parseDouble(item.get("low_level3_start").toString());
						Double lowLevel3EndAlarm = item.get("low_level3_end") == null ? null : Double.parseDouble(item.get("low_level3_end").toString());
						//更新mysql中是否报警字段
						int state = 0;
						if (rangeMax == null || rangeMin == null) {
							return;
						}
						//未超量程，更新config表
						if ((Double.parseDouble(monitorValue.toString()) <= rangeMax) && (rangeMin <= Double.parseDouble(monitorValue.toString()))){
							//kafka发送的值必须在量程范围内
							if ((Objects.equals("1",alarm) || Objects.equals("1",dolowWarn)) && rangeMax != null && rangeMin != null && rangeMax >= monitorValue && monitorValue >= rangeMin){
								if (Objects.equals("1",alarm) && (level1StartAlarm!= null && level1EndAlarm != null ) && (level1StartAlarm <= monitorValue &&  monitorValue <= level1EndAlarm)) {
//									System.out.println(" 一级报警------------------------" );
									state = 1;
								}else if(Objects.equals("1",alarm) &&  (level2StartAlarm!= null && level2EndAlarm != null ) && (level2StartAlarm <= monitorValue &&  monitorValue <= level2EndAlarm)){
//									System.out.println(" 二级报警------------------------" );
									state = 1;
								}else if(Objects.equals("1",alarm) && (level3StartAlarm!= null && level3EndAlarm != null ) && (level3StartAlarm <= monitorValue &&  monitorValue <= level3EndAlarm)){
//									System.out.println("三级报警------------------------" );
									state = 1;
								}else if(Objects.equals("1",dolowWarn) && (lowLevel3StartAlarm!= null && lowLevel3EndAlarm != null ) && (lowLevel3StartAlarm <= monitorValue &&  monitorValue <= lowLevel3EndAlarm)){
//									System.out.println("下行三级报警------------------------" );
									state = 1;
								}else if(Objects.equals("1",dolowWarn) && (lowLevel2StartAlarm!= null && lowLevel2EndAlarm != null ) && (lowLevel2StartAlarm <= monitorValue &&  monitorValue <= lowLevel2EndAlarm)){
//									System.out.println("下行二级报警------------------------" );
									state = 1;
								} else if(Objects.equals("1",dolowWarn) && (lowLevel1StartAlarm!= null && lowLevel1EndAlarm != null ) && (lowLevel1StartAlarm <= monitorValue &&  monitorValue <= lowLevel1EndAlarm)){
//									System.out.println("下行一级报警------------------------" );
									state = 1;
								}
							}
							if (null != tagCode){
								Map<String,Object> params = new HashMap<>(4);
								params.put("tag_code",tagCode);
								params.put("monitorValue",monitorValue);
								params.put("state",state);
								params.put("datetime",date);
								//井盖报到数据只更新mysql的更新时间，即save_date
								if (funData.get("13") != null && Objects.equals(funData.get("13").toString(),"Signin")){
									params.remove("monitorValue");
									sendkafkaToMysql(tagCode,new JSONObject(params).toJSONString());
								}else {
									//正常数据照常更新
									sendkafkaToMysql(tagCode,new JSONObject(params).toJSONString());
								}
							}
						}
					}
				}
			}
		list.clear();
	}

		/**
		 * 发送kafka用来更新mysql
		 * @param params
		 */
		private void sendkafkaToMysql(String tagCode,String params) {
		long starttime = System.currentTimeMillis();
		ProducerRecord record = new ProducerRecord("syncMysqlTopic",tagCode,params);
//		System.out.println("需要更新mysql的数据========================> " + params);
		long finalStarttime = starttime;
		Producer producer = null;
		try {
			producer = (Producer) deque.take();
			producer.send(record, (metadata, exception) -> {
//				System.out.println("\n需要更新MySQL的主题==》:"+ metadata.topic());
				if (exception == null) {
//					System.out.println("\n需要更新MySQL的ack : " + (System.currentTimeMillis() - finalStarttime) + "ms");
				}
			});
			deque.put(producer);
		} catch (InterruptedException e) {
			logger.log(Level.ERROR, "update kafkaMysql put producer error", e);
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void run() {
	long startTime = System.currentTimeMillis();
	sendToKafka(map);
//	System.out.println("发送kafka=============》"+(System.currentTimeMillis() - startTime)+"ms");
	}
}
