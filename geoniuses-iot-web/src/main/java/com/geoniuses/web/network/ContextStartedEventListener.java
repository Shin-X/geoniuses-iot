package com.geoniuses.web.network;

import com.geoniuses.core.server.DataSourceFactory;
import com.geoniuses.mqtt.MqttServer;
import com.geoniuses.mqtt.MqttServerHandler;
import com.geoniuses.core.mapper.TransferMapper;
import com.geoniuses.tcp.TcpServer;
import com.geoniuses.udp.UDPServer;
import com.geoniuses.websocket.WebSocketServer;
import com.geoniuses.websocket.pojo.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/*
 *描述:
 * @author liuxin
 * @date 2021/5/8 17:35
 * @param null:
 * @return
 */

@Component
public class ContextStartedEventListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private MqttServerHandler mqttServerHandler;

    @Autowired
    TransferMapper transferMapper;

    @Value("${mqtt.port}")
    private int mqttPort;
    @Autowired
    DataSourceFactory dataSourceFactory;
    @Autowired
    private WebSocketService webSocketService;

    private final Executor executor = Executors.newFixedThreadPool(2);
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        List<Map<String, Object>> protocolList = new ArrayList<>();
        Map mqttMap = new HashMap();
        mqttMap.put("port",mqttPort);
        protocolList.add(mqttMap);
        executor.execute(new MqttServer(mqttServerHandler,mqttMap));
        Map<String,Object> map = new HashMap<>();
        map.put("port",6666);
        Map<String,Object> mapudp = new HashMap<>();
        mapudp.put("port",8888);
        Map<String,Object> mapweb = new HashMap<>();
        mapweb.put("port",9999);

        Executors.newSingleThreadExecutor().execute(new TcpServer(map));

        Executors.newSingleThreadExecutor().execute(new UDPServer(mapudp,dataSourceFactory));

        Executors.newSingleThreadExecutor().execute(new WebSocketServer(webSocketService,mapweb));
    }
}
