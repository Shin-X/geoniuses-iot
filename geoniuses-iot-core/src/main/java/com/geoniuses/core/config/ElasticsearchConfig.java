package com.geoniuses.core.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.IOReactorException;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author ：zyf
 * @date ：2020/7/17 17:41
 */
@Configuration
public class ElasticsearchConfig {

    private final static String username = "elastic";
    private final static String password = "zydl2020";
    @Bean
    @Scope("singleton")
    public RestHighLevelClient elasticsearchTemplate(){
        final IOReactorConfig ioReactorConfig = IOReactorConfig.custom().setIoThreadCount(
                Runtime.getRuntime().availableProcessors()
                ).setConnectTimeout(10).setRcvBufSize(5).setSoKeepAlive(true).build();
        PoolingNHttpClientConnectionManager connManager = null;
        try {
            connManager = new PoolingNHttpClientConnectionManager(new
                    DefaultConnectingIOReactor(ioReactorConfig));
        } catch (IOReactorException e) {
            e.printStackTrace();
        }
        connManager.setMaxTotal(100);
        connManager.setDefaultMaxPerRoute(100);
        /*用户认证对象*/
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        /*设置账号密码*/
        credentialsProvider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(username, password));
        /*创建rest client对象*/
        PoolingNHttpClientConnectionManager finalConnManager = connManager;
        RestClientBuilder builder = RestClient.builder(
                new HttpHost("host1", 9200),
                new HttpHost("host2", 9200),
                new HttpHost("host3", 9200))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                        return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                                .setConnectionManager(finalConnManager);
                    }
                });
        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }
}
