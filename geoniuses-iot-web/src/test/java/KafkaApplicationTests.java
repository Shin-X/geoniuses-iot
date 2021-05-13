import com.alibaba.fastjson.JSONObject;
import com.geoniuses.web.Application;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
class KafkaApplicationTests {

    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Test
    void contextLoads() {
        String s = "{\n" +
                "  \"11\": -85,\n" +
                "  \"sensorNo\": 155,\n" +
                "  \"12\": 8,\n" +
                "  \"13\": \"Alarm\",\n" +
                "  \"14\": 0,\n" +
                "  \"15\": 0,\n" +
                "  \"16\": 0,\n" +
                "  \"17\": \"Second\",\n" +
                "  \"18\": 0,\n" +
                "  \"19\": 1,\n" +
                "  \"DT\": \"2021-04-20 15:49:05\",\n" +
                "  \"1\": 224,\n" +
                "  \"2\": 1,\n" +
                "  \"3\": 201905160001,\n" +
                "  \"pubSubTopic\": \"pub_WellCover99\",\n" +
                "  \"7\": \"触发器数据\",\n" +
                "  \"8\": -120,\n" +
                "  \"9\": \"0.16\",\n" +
                "  \"topic\": \"ZYDL_WellCover99\",\n" +
                "  \"10\": 8\n" +
                "}";

        Map<String,Object> map = new HashMap<>();
        map.put("sensorNo",89);
        map.put("1",224);
        map.put("2",1);
        map.put("11",-85);
        map.put("DT","2021-04-23 15:10:05");
        map.put("7","触发器数据");
        map.put("3","201905160001");
        map.put("19",1);
        map.put("18",0);
        String s1 = new JSONObject(map).toJSONString();
        kafkaTemplate.send("ZYDL_WellCover99_Push_Airport_Liquid_Level-9", s1);
    }

}
