package com.geoniuses.tcp;


import com.geoniuses.tcp.coder.ManholeCoverEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author liuxin
 * @Date: 2021/4/28 17:39
 * @Description:
 */
public class TcpServer implements Runnable{
    private final Logger logger = LogManager.getLogger(TcpServer.class);

    public static final String PROTOCOL_TOPIC = "ZYDL_WellCover99";
    public static final String PUBSUB_TOPIC = "pub_WellCover99";
    //    public static final String PROTOCOL_TOPIC = "test_jg";
//    public static final String PUBSUB_TOPIC = "test_Push";
    private Map<String,Object> map;
  //  private ProcotolService procotolService;
    public TcpServer(Map<String,Object> map){
        this.map = map;
       // this.procotolService = procotolService;
    }

    private void start() throws InterruptedException{
        //处理Accept连接事件的线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //处理hadnler的工作线程，其实也就是处理IO读写 。线程数据默认为 CPU 核心数乘以2
        EventLoopGroup workGroup = new NioEventLoopGroup();
        //创建ServerBootstrap实例
        ServerBootstrap b = new ServerBootstrap();
        //初始化ServerBootstrap的线程组
        ServerBootstrap channel = b.group(bossGroup, workGroup).channel(NioServerSocketChannel.class);

        //在ServerChannelInitializer中初始化ChannelPipeline责任链，并添加到serverBootstrap中
        channel.childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        //打印日志
                        ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.INFO));
                        //根据报文协议自定义解码器
                        ch.pipeline().addLast("decoder", new TCPDecoder());
                        //handler中为自己的业务处理
                        ch.pipeline().addLast("requestHandler", new Handler());
                        ch.pipeline().addLast("idleStateHandler",new IdleStateHandler(10,0,0, TimeUnit.MINUTES));
                        //回复设备的解码器，其实就是封装好设备所需的数据发送给设备
                        ch.pipeline().addLast("encoder", new ManholeCoverEncoder());
                    }
                })
                //标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
                //每个channel的配置
                //TCP 层面的接收和发送缓冲区大小设置，在 Netty 中分别对应 ChannelOption 的 SO_SNDBUF 和 SO_RCVBUF
                .option(ChannelOption.SO_RCVBUF, 128)
                .option(ChannelOption.SO_SNDBUF, 256)
                .option(ChannelOption.SO_BACKLOG, 512)
                .option(EpollChannelOption.TCP_KEEPINTVL, 10)
                .option(EpollChannelOption.TCP_KEEPCNT, 3)
                .option(EpollChannelOption.TCP_KEEPIDLE, 30)
                //AdaptiveRecvByteBufAllocator：容量动态调整的接收缓冲区分配器，它会根据之前 Channel 接收到的数据报大小进行计算，如果连续填充满接收缓冲区的可写空间，则动态扩展容量。如果连续 2 次接收到的数据报都小于指定值，则收缩当前的容量，以节约内存
//                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                // 是否启用心跳保活机机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        //绑定端口后，开启监听
        ChannelFuture f = b.bind(Integer.parseInt(map.get("port").toString())).sync();
        if (f.isSuccess()) {
            logger.debug(new Date() + ": MODBUS协议端口【" + map.get("port")+ "】 绑定成功");
        } else {
            logger.error(new Date() + ": MODBUS协议端口【" + map.get("port") + "】 绑定失败");
        }
        logger.info("ModbusServer start success");

        f.channel().closeFuture().syncUninterruptibly();
    }

    @Override
    public void run() {
        try {
            start();
        }catch (Exception e){
            logger.error("ModbusServer start failed", e);
        }
    }
}

