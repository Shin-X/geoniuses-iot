package com.geoniuses.udp;

import com.geoniuses.core.mapper.TransferMapper;
import com.geoniuses.core.server.DataSourceFactory;
import com.geoniuses.udp.coder.DATA_6216Encoder;
import com.geoniuses.udp.coder.PsDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Date;
import java.util.Map;

/**
 * Created  zyf in 2019/10/8 14:45
 */
public class UDPServer implements Runnable {

    private final Logger log = LogManager.getLogger(UDPServer.class);

    public static String protocol_code = "ZYDL_Water03";
    public static String PubSubTopic = "pub_Water03";

    private Map<String,Object> map;
    private TransferMapper transferMapper;
    private DataSourceFactory dataSourceFactory;

    public UDPServer(Map<String,Object> map ,DataSourceFactory dataSourceFactory){
        this.map = map;
        this.dataSourceFactory=dataSourceFactory;

    }
        private void start() throws InterruptedException{

            EventLoopGroup  group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.INFO));

                            ch.pipeline().addLast("decoder",new PsDecoder());
                            ch.pipeline().addLast(new DATA_6216Encoder());
                            ch.pipeline().addLast(new PsHandler(map,dataSourceFactory));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 512)
                    .option(ChannelOption.SO_BROADCAST,true)
                    .option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = bootstrap.bind(Integer.parseInt(map.get("port").toString())).sync();
            if (f.isSuccess()) {
                log.debug(new Date() + ": PS协议端口【" + map.get("port").toString() + "】 绑定成功");
            } else {
                log.error(new Date() + ": PS协议端口【" + map.get("port").toString() + "】 绑定失败");
            }
            log.info("PsServer start success");

            f.channel().closeFuture().syncUninterruptibly();
        }

        @Override
        public void run() {
            try {
                start();
            }catch (Exception e){
                log.error("psServer start failed", e);
            }
        }


}
