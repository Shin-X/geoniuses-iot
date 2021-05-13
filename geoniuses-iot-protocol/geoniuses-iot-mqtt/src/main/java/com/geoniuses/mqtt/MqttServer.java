package com.geoniuses.mqtt;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
public class MqttServer implements Runnable {
        private String PORT = "port";
        private Map<String, Object> map;
        private MqttServerHandler mqttServerHandler;

        public MqttServer(MqttServerHandler mqttServerHandler, Map<String, Object> map) {
            this.map = map;
            this.mqttServerHandler = mqttServerHandler;
        }
        private void start() throws InterruptedException {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workGroup = new NioEventLoopGroup();
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.INFO));
                            ch.pipeline().addLast(new MqttDecoder(65535));
                            ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(10, 0, 0, TimeUnit.MINUTES));
                            ch.pipeline().addLast(mqttServerHandler);
                            ch.pipeline().addLast(MqttEncoder.INSTANCE);
                        }
                    });
            ChannelFuture f = b.bind(Integer.parseInt(map.get(PORT).toString())).sync();
            if (f.isSuccess()) {
                log.debug(new Date() + ": MQTT协议端口【" + map.get(PORT) + "】 绑定成功");
            } else {
                log.error(new Date() + ": MQTT协议端口【" + map.get(PORT) + "】 绑定失败");
            }
            log.debug("MqttServer start success");

            f.channel().closeFuture().syncUninterruptibly();
        }

        @Override
        public void run() {
            try {
                start();
            } catch (Exception e) {
                log.error("MqttServer start failed", e);
            }
        }
    }
