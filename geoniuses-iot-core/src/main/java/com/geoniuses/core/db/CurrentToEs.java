package com.geoniuses.core.db;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoniuses.core.config.ElasticsearchConfig;
import com.geoniuses.core.config.RedisClusterConfig;
import com.geoniuses.core.pojo.IotAbnormal;
import com.geoniuses.core.pojo.IotAlert;
import com.geoniuses.core.pojo.IotHistory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author ：zyf
 * @date ：2020/9/4 14:58
 */
@Component
public class CurrentToEs implements Runnable {

    private static final Logger log = LogManager.getLogger(CurrentToEs.class);

    private final ObjectMapper om = new ObjectMapper();
    private static final String HISTORY = "iot_history";
    private static final String ALERT = "iot_alert";
    private static final String ABNORMAL = "iot_abnormal";
    @Autowired
    private ElasticsearchConfig config;
    @Autowired
    private RedisClusterConfig redisClusterConfig;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy");
    private List<ConsumerRecord<String, String>> records;

    public CurrentToEs(List<ConsumerRecord<String, String>> records, ElasticsearchConfig config, RedisClusterConfig redisClusterConfig) {
        this.records = records;
        this.config = config;
        this.redisClusterConfig = redisClusterConfig;
    }
    @Override
    public void run() {
        if (records.size() == 0) {
            return;
        }
        processData(records);
    }

    private void processData(List<ConsumerRecord<String, String>> records) {
        BulkRequest bulkRequest = new BulkRequest();
        String year = this.format.format(new Date());
        for (ConsumerRecord<String, String> record: records){
            Map<String,Object> map = null;
            try {
                map = om.readValue(record.value().getBytes(), Map.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (CollectionUtils.isEmpty(map)) {
                return;
            }
            Integer levelNow = Integer.parseInt(map.get("level_now").toString());
            if (null == levelNow){
                return;
            }
            //正常数据 除异常数据其他数据均进入历史数据，不论是报警还是正常数据
            if (levelNow != 6) {
                if (levelNow != 0){
                    //报警数据
                    IotAlert iotAlert = om.convertValue(map, IotAlert.class);
                    createAlertIndex(ALERT+"_"+year);
                    String str = JSON.toJSONString(iotAlert);
                    bulkRequest.add(new IndexRequest(ALERT+"_"+year).source(str, XContentType.JSON));
                }
                //正常数据
                IotHistory iotHistory = om.convertValue(map, IotHistory.class);
                createHistoryIndex(HISTORY+"_"+year);
                String str = JSON.toJSONString(iotHistory);
                    bulkRequest.add(new IndexRequest(HISTORY+"_"+year).source(str, XContentType.JSON));
            }else {
                //异常数据
                IotAbnormal iotAbnormal = om.convertValue(map, IotAbnormal.class);
                    createAbnormalIndex(ABNORMAL+"_"+year);
                String str = JSON.toJSONString(iotAbnormal);
                bulkRequest.add(new IndexRequest(ABNORMAL+"_"+year).source(str, XContentType.JSON));
            }
//            if (levelNow != 0 && levelNow != 6) {
//                //报警数据
//                IotAlert iotAlert = om.convertValue(map, IotAlert.class);
//                    createAlertIndex(ALERT+"_"+year);
//                String str = JSON.toJSONString(iotAlert);
//                bulkRequest.add(new IndexRequest(ALERT+"_"+year).source(str, XContentType.JSON));
//            }
        }
        insertEs(bulkRequest);
    }


    /**
     * 判断索引是否存在
     *
     * @param index
     * @return
     */
    private boolean indexExist(String index) {
        try {
            return config.elasticsearchTemplate().indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * es新增
     *
     * @param bulkRequest
     * @return
     */
    public void insertEs(BulkRequest bulkRequest) {
        RestHighLevelClient client = config.elasticsearchTemplate();
//        List<String> results = new ArrayList<>();
        try {
            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (bulkResponse != null) {
                for (BulkItemResponse bulkItemResponse : bulkResponse) {
                    if (bulkItemResponse.isFailed()) {
                        log.error("报错啦====》【{}】", bulkItemResponse.getFailureMessage());
                        continue;
                    }
                    DocWriteResponse itemResponse = bulkItemResponse.getResponse();
                    if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
                            || bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
//                        IndexResponse indexResponse = (IndexResponse) itemResponse;
//                        System.out.println("新增成功,{}" + indexResponse.toString());

//                        int i = indexResponse.getShardInfo().getSuccessful();
//                        long successful = i;
//                        Long incr = redisClusterConfig.getLettuceConnectionFactory().getClusterConnection().incrBy("test1".getBytes(),successful);
//                        System.out.println("incr = " + incr);
                    }
                }
            }
        } catch (Exception e) {
            log.error("错误[{}]", e);
            e.printStackTrace();

        }
//        finally {
//            try {
//                client.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return results;
    }

    public void createAbnormalIndex(String abnormalIndex) {

        if (!indexExist(abnormalIndex)) {
            try {
                CreateIndexRequest createRequest = new CreateIndexRequest(abnormalIndex);
                //分片设置为3，副本设置为2
                createRequest.settings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2));

                createRequest.mapping(
                        "{\n" +
                                "    \"properties\": {\n" +
                                "      \"crt_time\": {\n" +
                                "        \"type\": \"date\",\n" +
                                "        \"format\": \"yyyy-MM-dd HH:mm:ss\"\n" +
                                "      },\n" +
                                "      \"crt_user\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"is_delete\": {\n" +
                                "        \"type\": \"boolean\"\n" +
                                "      },\n" +
                                "      \"station_key\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"remark\": {\n" +
                                "        \"type\": \"text\"\n" +
                                "      },\n" +
                                "      \"tag_code\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"tag_value\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"add_time\": {\n" +
                                "        \"type\": \"date\",\n" +
                                "        \"format\": \"yyyy-MM-dd HH:mm:ss\"\n" +
                                "      },\n" +
                                "      \"talent_id\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"device_key\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"units\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"rangemin_value\": {\n" +
                                "        \"type\": \"double\"\n" +
                                "      },\n" +
                                "      \"rangemax_value\": {\n" +
                                "        \"type\": \"double\"\n" +
                                "      }\n" +
                                "    }\n" +
                                "  }",
                        XContentType.JSON);
                CreateIndexResponse createIndexResponse = config.elasticsearchTemplate().indices().create(createRequest, RequestOptions.DEFAULT);
                if (createIndexResponse.isAcknowledged()) {
                    System.out.println("索引===>【" + abnormalIndex + "】  创建成功");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createHistoryIndex(String historyIndex) {

        if (!indexExist(historyIndex)) {

            try {
                CreateIndexRequest createRequest = new CreateIndexRequest(historyIndex);
                //分片设置为3，副本设置为2
                createRequest.settings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2));

                createRequest.mapping(
                        "{\n" +
                                "    \"properties\": {\n" +
                                "      \"big_industry_code\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"big_industry_name\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"mid_industry_code\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"mid_industry_name\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"min_industry_code\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"min_industry_name\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"station_key\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"station_name\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"tag_code\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"tag_value\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"tag_name\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"add_time\": {\n" +
                                "        \"type\": \"date\",\n" +
                                "        \"format\": \"yyyy-MM-dd HH:mm:ss\"\n" +
                                "      },\n" +
                                "      \"talent_id\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"point\": {\n" +
                                "        \"type\": \"geo_point\"\n" +
                                "      },\n" +
                                "      \"device_key\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"units\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"elevation\": {\n" +
                                "        \"type\": \"float\"\n" +
                                "      },\n" +
                                "      \"is_delete\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      }\n" +
                                "    }\n" +
                                "  }",
                        XContentType.JSON);
                CreateIndexResponse createIndexResponse = config.elasticsearchTemplate().indices().create(createRequest, RequestOptions.DEFAULT);
                if (createIndexResponse.isAcknowledged()) {
                    System.out.println("索引===>【" + historyIndex + "】  创建成功");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //创建alert索引mapping
    public void createAlertIndex(String alertIndex) {

        if (!indexExist(alertIndex)) {
            try {
                CreateIndexRequest createRequest = new CreateIndexRequest(alertIndex);
                //分片设置为3，副本设置为2
                createRequest.settings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2));

                createRequest.mapping(
                        "{\n" +
                                "    \"properties\": {\n" +
                                "      \"big_industry_code\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"big_industry_name\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"mid_industry_code\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"mid_industry_name\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"min_industry_code\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"min_industry_name\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"station_key\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"station_name\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"tag_code\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"tag_value\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"tag_name\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"add_time\": {\n" +
                                "        \"type\": \"date\",\n" +
                                "        \"format\": \"yyyy-MM-dd HH:mm:ss\"\n" +
                                "      },\n" +
                                "      \"talent_id\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"point\": {\n" +
                                "        \"type\": \"geo_point\"\n" +
                                "      },\n" +
                                "      \"is_send\": {\n" +
                                "        \"type\": \"boolean\"\n" +
                                "      },\n" +
                                "      \"is_set\": {\n" +
                                "        \"type\": \"boolean\"\n" +
                                "      },\n" +
                                "      \"level_now\": {\n" +
                                "        \"type\": \"integer\"\n" +
                                "      },\n" +
                                "      \"do_people\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"crt_user\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"do_date\": {\n" +
                                "        \"type\": \"date\",\n" +
                                "        \"format\": \"yyyy-MM-dd HH:mm:ss\"\n" +
                                "      },\n" +
                                "      \"device_key\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"units\": {\n" +
                                "        \"type\": \"keyword\"\n" +
                                "      },\n" +
                                "      \"elevation\": {\n" +
                                "        \"type\": \"float\"\n" +
                                "      }\n" +
                                "    }\n" +
                                "  }",
                        XContentType.JSON);
                CreateIndexResponse createIndexResponse = config.elasticsearchTemplate().indices().create(createRequest, RequestOptions.DEFAULT);
                if (createIndexResponse.isAcknowledged()) {
                    System.out.println("索引===>【" + alertIndex + "】  创建成功");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
