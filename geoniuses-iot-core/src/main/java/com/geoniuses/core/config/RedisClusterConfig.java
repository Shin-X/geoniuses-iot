package com.geoniuses.core.config;

import io.lettuce.core.RedisURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ：zyf
 * @date ：2020/3/19 16:46
 */
@Configuration
public class RedisClusterConfig {
    @Value("${spring.redis.cluster.nodes}")
    private String nodes;
    @Value("${spring.redis.cluster.command-timeout}")
    private int commandTimeout;
    @Value("${spring.redis.password}")
    private String password;

    //    @Bean
//    public LettuceClusterConnection getLettuceClusterConnection(){
//        String [] serverArray= nodes.split(",");
//        Set<RedisURI> nodes=new HashSet<>();
//        for (String ipPort:serverArray){
//            String [] ipPortPair=ipPort.split(":");
//            RedisURI redisURI = new RedisURI(ipPortPair[0].trim(),Integer.valueOf(ipPortPair[1].trim()), Duration.ofMillis(commandTimeout));
//            redisURI.setPassword(password);
//            nodes.add(redisURI);
//        }
//        RedisClusterClient redisClusterClient = RedisClusterClient.create(nodes);
//        return new LettuceClusterConnection(redisClusterClient);
//    }
    @Bean
    public LettuceConnectionFactory getLettuceConnectionFactory() {
        String[] serverArray = nodes.split(",");
        Set<RedisURI> nodes = new HashSet<>();
        for (String ipPort : serverArray) {
            String[] ipPortPair = ipPort.split(":");
            RedisURI redisURI = new RedisURI(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim()), Duration.ofMillis(commandTimeout));
            nodes.add(redisURI);
        }
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(CollectionUtils.arrayToList(serverArray));
        redisClusterConfiguration.setPassword(RedisPassword.of(password));
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisClusterConfiguration);
        // 使用前先校验连接
        factory.setValidateConnection(true);
        return factory;
    }
}
