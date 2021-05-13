package com.geoniuses.websocket;



import com.geoniuses.websocket.coder.StompWebSocketProtocolCodec;
import com.geoniuses.websocket.pojo.WebSocketService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ：zyf
 * @date ：2020/7/30 15:56
 */
@Slf4j
public class WebSocketServer implements Runnable{
        private String PORT = "port";
        private Map<String, Object> map;
        private WebSocketService webSocketService;

        public WebSocketServer(WebSocketService webSocketService, Map<String, Object> map) {
            this.webSocketService = webSocketService;
            this.map = map;
        }

        public void start() throws InterruptedException {
            //处理Accept连接事件的线程
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            //处理hadnler的工作线程，其实也就是处理IO读写 。线程数据默认为 CPU 核心数乘以2
            EventLoopGroup workGroup = new NioEventLoopGroup();
            //创建ServerBootstrap实例
            ServerBootstrap b = new ServerBootstrap();
            //初始化ServerBootstrap的线程组
            b.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                    //在ServerChannelInitializer中初始化ChannelPipeline责任链，并添加到serverBootstrap中
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            //打印日志
                            ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.INFO));
                            //对客户端，如果在60秒内没有向服务端发送心跳，就主动断开
                            //三个参数分别为读/写/读写的空闲，我们只针对读写空闲检测
                            //五分钟没有反应就剔除
                            ch.pipeline().addLast(new IdleStateHandler(5, 0, 0, TimeUnit.MINUTES));
                            ch.pipeline().addLast(new HttpServerCodec());
                            ch.pipeline().addLast(new HttpObjectAggregator(64 * 1024));
                            ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws", "v10.stomp"));
                            ch.pipeline().addLast(new ChunkedWriteHandler());
                            ch.pipeline().addLast(new StompWebSocketProtocolCodec(webSocketService));
                        }
                    });

            //绑定端口后，开启监听
            ChannelFuture f = b.bind(Integer.parseInt(map.get(PORT).toString())).sync();
            if (f.isSuccess()) {
                log.debug(new Date() + ": websocket协议端口【" + map.get(PORT) + "】 绑定成功");
            } else {
                log.error(new Date() + ": websocket协议端口【" + map.get(PORT) + "】 绑定失败");
            }
            log.info("WebsocketServer start success");

            f.channel().closeFuture().syncUninterruptibly();
        }


        @Override
        public void run() {
            try {
                start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
