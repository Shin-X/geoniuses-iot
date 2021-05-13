package com.geoniuses.websocket.pojo;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoniuses.core.config.RedisClusterConfig;
import com.geoniuses.core.mapper.TransferMapper;
import io.netty.handler.codec.stomp.DefaultStompFrame;
import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompFrame;
import io.netty.handler.codec.stomp.StompHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author ：zyf
 * @date ：2020/4/22 15:06
 */

public class PubSubProcress implements Runnable {

    private static final Logger logger = LogManager.getLogger(PubSubProcress.class);

    private static final String LEVEL_ALARM = "levelAlarm";
    private String kafkaData;

    private ConcurrentMap<String, Set<StompSubscription>> destinations;

    private RedisClusterConfig redisClusterConfig;

    private TransferMapper transferMapper;
    private  RestTemplate restTemplate;

    private static final ObjectMapper om = new ObjectMapper();

    public PubSubProcress(String arg, ConcurrentMap<String, Set<StompSubscription>> destinations, RedisClusterConfig redisClusterConfig, TransferMapper transferMapper, RestTemplate restTemplate) {
        this.kafkaData = arg;
        this.destinations = destinations;
        this.redisClusterConfig = redisClusterConfig;
        this.transferMapper = transferMapper;
        this.restTemplate = restTemplate;
    }

    //    @SneakyThrows
    @Override
    public void run() {
        //逻辑处理
       // List<Map<String, Object>> pushData = this.getPushDataForUser();
        //简化业务
        ArrayList<Map<String, Object>> pushData = new ArrayList<>();
        Map map = null;
        try {
            map = om.readValue(kafkaData, Map.class);
            map.put("topic","05");
            map.put("user","bd3df3a4add446e9b0f3ac919e5415ef");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        pushData.add(map);

        for (Map<String, Object> data : pushData) {
            String userTopic = data.get("user").toString();
            Set<StompSubscription> stompSubscriptions = destinations.get(data.get("topic").toString());
            for (StompSubscription stompSubscription : stompSubscriptions) {
                if (stompSubscription.getTenantId().equals(userTopic)) {
                    StompFrame messageFrame = new DefaultStompFrame(StompCommand.MESSAGE);
//                        StompFrame messageFrame = new DefaultStompFrame(StompCommand.MESSAGE, Unpooled.copiedBuffer(JSON.toJSONString(data), Charset.forName("UTF-8")));
                    String id = UUID.randomUUID().toString();
                    messageFrame.headers()
                            .set(StompHeaders.MESSAGE_ID, id)
                            .set(StompHeaders.SUBSCRIPTION, stompSubscription.getId())
                            .set("data", JSON.toJSONString(data));
                    stompSubscription.getChannel().writeAndFlush(messageFrame);
                }
            }
        }


     /*   if (CollectionUtils.isEmpty(pushData)) {
            return;
        }
        for (Map<String, Object> data : pushData) {
            logger.info("消息==========》[{}]", data);
            String userTopic = data.get("user").toString();
            logger.info("推送的用户==》[{}]", userTopic);
//            if (destinations.)
            Set<StompSubscription> stompSubscriptions = destinations.get(data.get("topic").toString());
//            System.out.println("stompSubscriptions.size() = " + stompSubscriptions.size());
//            System.out.println("destinations.size() = " + destinations.size());
            //如果取出为空，说明此订阅的为井盖，井盖目的地为多个
            if (CollectionUtils.isEmpty(stompSubscriptions)) {
                for (Map.Entry<String, Set<StompSubscription>> entry : destinations.entrySet()) {
                    //key的格式为 011291，021291，031291
                    if (entry.getKey().contains(",")) {
                        String[] split = entry.getKey().split(",");
                        //str 可能为 011291 021291 031291 ......
                        for (String str : split) {
                            if (str.startsWith(data.get("topic").toString())) {
                                Set<StompSubscription> stompSubscriptionsWellCover = entry.getValue();
                                for (StompSubscription stompSubscription : stompSubscriptionsWellCover) {
                                    if (stompSubscription.getTenantId().equals(userTopic)) {
                                        StompFrame messageFrame = new DefaultStompFrame(StompCommand.MESSAGE);
//                                        StompFrame messageFrame = new DefaultStompFrame(StompCommand.MESSAGE, Unpooled.copiedBuffer(JSON.toJSONString(data), Charset.forName("UTF-8")));
                                        String id = UUID.randomUUID().toString();
                                        messageFrame.headers()
                                                .set(StompHeaders.MESSAGE_ID, id)
                                                .set(StompHeaders.SUBSCRIPTION, stompSubscription.getId())
                                                .set("data", JSON.toJSONString(data));
                                        stompSubscription.getChannel().writeAndFlush(messageFrame);
                                    }
                                }
                                return;
                            }
                        }
                    }
                }
            } else {
                for (StompSubscription stompSubscription : stompSubscriptions) {
                    if (stompSubscription.getTenantId().equals(userTopic)) {
                        StompFrame messageFrame = new DefaultStompFrame(StompCommand.MESSAGE);
//                        StompFrame messageFrame = new DefaultStompFrame(StompCommand.MESSAGE, Unpooled.copiedBuffer(JSON.toJSONString(data), Charset.forName("UTF-8")));
                        String id = UUID.randomUUID().toString();
                        messageFrame.headers()
                                .set(StompHeaders.MESSAGE_ID, id)
                                .set(StompHeaders.SUBSCRIPTION, stompSubscription.getId())
                                .set("data", JSON.toJSONString(data));
                        stompSubscription.getChannel().writeAndFlush(messageFrame);
                    }
                }
            }
        }*/
    }

    private List<Map<String, Object>> getPushDataForUser() {
        Map map = null;
        try {
            map = om.readValue(kafkaData, Map.class);
        } catch (IOException e) {
            logger.error("kafka数据解析错误 ==========> ,[{}]    数据===》,[{}]", e.getMessage(), kafkaData);
            e.printStackTrace();
        }
        if (CollectionUtils.isEmpty(map)) {
            return Collections.emptyList();
        }
        String sensorNo = map.get("sensorNo").toString();

        Map finalMap = map;

        List<Map<String, Object>> list = null;
        //判断redis中是否存在key
        Boolean exists = redisClusterConfig.getLettuceConnectionFactory().getClusterConnection().keyCommands().exists(sensorNo.getBytes());
        if (exists) {
            //从redis中取数据 根据传感器编号取出对应所有监测项
            byte[] bytes = redisClusterConfig.getLettuceConnectionFactory().getClusterConnection().stringCommands().get(sensorNo.getBytes());
            try {
                list = om.readValue(bytes, List.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            list = transferMapper.getTagBySensorNo(sensorNo);
        }
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> resultList = new ArrayList<>();
        //根据sensorNo查询出此传感器的主题并匹配出订阅中的主题
        String bigIndustryCode = list.get(0).get("big_industry_code").toString();
        String midIndustryCode = list.get(0).get("mid_industry_code").toString();
        String minIndustryCode = list.get(0).get("min_industry_code").toString();
        String Industry = bigIndustryCode + midIndustryCode + minIndustryCode;
        //查询出每个用户的数据
        if (CollectionUtils.isEmpty(destinations)) {
            return Collections.emptyList();
        }
//            byte[] bytes = redisTemplateConfig.getRedisTemplate().getConnectionFactory().getClusterConnection().stringCommands().get(bigIndustryCode.getBytes());
//            Set<Map<String,Object>> destinationList = null;
//            try {
//                destinationList = om.readValue(bytes, Set.class);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        Set<StompSubscription> stompSubscriptions = destinations.get(bigIndustryCode);
        if (CollectionUtils.isEmpty(stompSubscriptions)) {
            for (Map.Entry<String, Set<StompSubscription>> entry : destinations.entrySet()) {
                //说明key为井盖的011291 021991 031291 或 011291 031291 041291
                if (entry.getKey().contains(Industry)) {
                    stompSubscriptions = entry.getValue();
                    if (CollectionUtils.isEmpty(stompSubscriptions)) {
                        //删除无用的通道
                        destinations.remove(entry.getKey());
                        continue;
                    }
                    break;
                }
            }
        }
        if (CollectionUtils.isEmpty(stompSubscriptions)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> finalList = list;
        stompSubscriptions.forEach(destination -> {
//                Map<String, Object> stationKey = null;
            Map<String, Object> resultMap = new HashMap<>();
            //判断kafka发送的数据是否存在报警项

            int status = 0;
            //遍历Kafka发送的数据
            for (Map<String, Object> item : finalList) {
                if (item.get("order_num") != null) {
                    //如果kafka中数据位和传感器监测项中的排序号相等,查看报警范围
                    String tenantId = item.get("tenant_id") == null ? null : item.get("tenant_id").toString();
                    //kafka中的数据位应对应数据库中的数据位，并且订阅的租户对应数据中的租户，才执行封装数据
                    if (finalMap.containsKey(item.get("order_num").toString()) && destination.getTenantId().equals(tenantId)) {
//                            stationKey =  finalList.get(0);
                        //监测项中有报警项，说明在这条数据为报警数据
                        if (status == 1) {
                            break;
                        }
                        Double tagValue = Double.parseDouble(finalMap.get(item.get("order_num").toString()).toString());
                        //封装监测项
                        resultMap.put(item.get("order_num").toString(), tagValue);
                        //量程最大值
                        Double rangeMax = item.get("rangemax_value") == null ? null : Double.parseDouble(item.get("rangemax_value").toString());
                        //量程最小值
                        Double rangeMin = item.get("rangemin_value") == null ? null : Double.parseDouble(item.get("rangemin_value").toString());
                        //1级报警开始
                        Double level1StartAlarm = item.get("level1_start") == null ? null : Double.parseDouble(item.get("level1_start").toString());
                        Double level1EndAlarm = item.get("level1_end") == null ? null : Double.parseDouble(item.get("level1_end").toString());
                        //2级报警开始
                        Double level2StartAlarm = item.get("level2_start") == null ? null : Double.parseDouble(item.get("level2_start").toString());
                        Double level2EndAlarm = item.get("level2_end") == null ? null : Double.parseDouble(item.get("level2_end").toString());
                        //3级报警开始
                        Double level3StartAlarm = item.get("level3_start") == null ? null : Double.parseDouble(item.get("level3_start").toString());
                        Double level3EndAlarm = item.get("level3_end") == null ? null : Double.parseDouble(item.get("level3_end").toString());

                        //开启报警 1：是  0；否
                        String alarm = item.get("is_alarm").toString();
                        //开启下行报警 1：是  0；否
                        String dolowWarn = item.get("dolow_warn").toString();
                        //下行1级报警开始
                        Double lowLevel1StartAlarm = item.get("low_level1_start") == null ? null : Double.parseDouble(item.get("low_level1_start").toString());
                        Double lowLevel1EndAlarm = item.get("low_level1_end") == null ? null : Double.parseDouble(item.get("low_level1_end").toString());

                        //下行2级报警
                        Double lowLevel2StartAlarm = item.get("low_level2_start") == null ? null : Double.parseDouble(item.get("low_level2_start").toString());
                        Double lowLevel2EndAlarm = item.get("low_level2_end") == null ? null : Double.parseDouble(item.get("low_level2_end").toString());

                        //下行3级报警
                        Double lowLevel3StartAlarm = item.get("low_level3_start") == null ? null : Double.parseDouble(item.get("low_level3_start").toString());
                        Double lowLevel3EndAlarm = item.get("low_level3_end") == null ? null : Double.parseDouble(item.get("low_level3_end").toString());

                        Double lon = item.get("x") == null ? null : Double.parseDouble(item.get("x").toString());
                        Double lat = item.get("y") == null ? null : Double.parseDouble(item.get("y").toString());
                        String stationKey = item.get("station_key") == null ? null : item.get("station_key").toString();
                        String bigCode = item.get("big_industry_code") == null ? null : item.get("big_industry_code").toString();
                        String midCode = item.get("mid_industry_code") == null ? null : item.get("mid_industry_code").toString();
                        String minCode = item.get("min_industry_code") == null ? null : item.get("min_industry_code").toString();
                        String tagCode = item.get("tag_code") == null ? null : item.get("tag_code").toString();
                        resultMap.put("topic", bigCode);
                        resultMap.put("lon", lon);
                        resultMap.put("lat", lat);
                        resultMap.put("stationKey", stationKey);
                        resultMap.put("IndustryCode", bigCode + midCode + minCode);
                        resultMap.put("tag_code", tagCode);
                        //kafka发送的值必须在量程范围内
                        if ((Objects.equals("1", alarm) || Objects.equals("1", dolowWarn)) && rangeMax != null && rangeMin != null && rangeMax >= tagValue && tagValue >= rangeMin) {
                            //如果在1级报警和3级报警之间或者在下行三级报警和下行一级报警之间那么说名此监测项为报警
                            if ((level1StartAlarm != null && level1EndAlarm != null) && (level1StartAlarm <= tagValue && tagValue <= level1EndAlarm)) {
                                resultMap.put(LEVEL_ALARM, 1);
                                status = 1;
                            } else if ((level2StartAlarm != null && level2EndAlarm != null) && (level2StartAlarm <= tagValue && tagValue <= level2EndAlarm)) {
                                resultMap.put(LEVEL_ALARM, 2);
                                status = 1;
                            } else if ((level3StartAlarm != null && level3EndAlarm != null) && (level3StartAlarm <= tagValue && tagValue <= level3EndAlarm)) {
                                resultMap.put(LEVEL_ALARM, 3);
                                status = 1;
                            } else if ((lowLevel3StartAlarm != null && lowLevel3EndAlarm != null) && (lowLevel3StartAlarm <= tagValue && tagValue <= lowLevel3EndAlarm)) {
                                resultMap.put(LEVEL_ALARM, -3);
                                status = 1;
                            } else if ((lowLevel2StartAlarm != null && lowLevel2EndAlarm != null) && (lowLevel2StartAlarm <= tagValue && tagValue <= lowLevel2EndAlarm)) {
                                resultMap.put(LEVEL_ALARM, -2);
                                status = 1;
                            } else if ((lowLevel1StartAlarm != null && lowLevel1EndAlarm != null) && (lowLevel1StartAlarm <= tagValue && tagValue <= lowLevel1EndAlarm)) {
                                resultMap.put(LEVEL_ALARM, -1);
                                status = 1;
                            }
                        }
                    }
                }
            }

            //需求：一个监测点有两个设备或多个设备，如果此时有一个设备报警，另一个设备发送正常数 不推送
            //一个设备是报警状态，另一个设备此时有报警数据 推送
            AtomicBoolean flag = new AtomicBoolean(false);
            Map<String,Object> params = new HashMap<>();
            params.put("tenantId",destination.getTenantId());
            System.out.println("resultMap = " + resultMap);
            params.put("stationKey",resultMap.get("stationKey").toString());
            params.put("sensor_code",sensorNo);
            List<Map<String, Object>> configByStationKey = transferMapper.getConfigByStationKey(params);
            for (Map<String,Object> station : configByStationKey){
                if (Objects.equals(station.get("state").toString(),"1") &&  //其他设备存在报警
                        status != 1 ){
//                        && //当前没有报警
//                        !Objects.equals(station.get("tag_code").toString(),resultMap.get("tag_code"))){ //不同的检测项
                    //true为不推送
                        flag.set(true);
                        return;
                    }
                }

            //获取监测点类型
            if (!flag.get()){
                //判断是否在施工区域内，true在施工区域内，不推送
                String url = "http://iot1:9999/index/judgeLinkArea?tenantId="+destination.getTenantId()+"&stationKey="+resultMap.get("stationKey").toString();
                Map<String,String> param = new HashMap<>();
                param.put("stationKey",resultMap.get("stationKey").toString());
                param.put("tenantId",destination.getTenantId());
                Boolean forObject = restTemplate.getForObject(url, Boolean.class);
                if (forObject){
                    return;
                }
                //                String subStationKey = stationKey.get("big_industry_code").toString()+stationKey.get("mid_industry_code")+stationKey.get("min_industry_code");
                resultMap.put("user", destination.getTenantId());
    //                resultMap.put("topic", stationKey.get("big_industry_code"));
                resultMap.put("status", status);
                resultMap.put("sensorNo", sensorNo);
    //                resultMap.put("stationKey", stationKey.get("station_key"));
    //                resultMap.put("lon", lon);
    //                resultMap.put("lat", stationKey.get("Y"));
    //                resultMap.put("IndustryCode", subStationKey);
    //                System.out.println("resultMap = " + resultMap);
                resultList.add(resultMap);
            }
        });

        return resultList;
    }
}
