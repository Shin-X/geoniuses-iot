package com.geoniuses.core.pojo.protocol;


import com.geoniuses.core.enums.ProtocolEnum;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 * @auther: zyf
 * @Date: 2020/10/14 10:20
 * @Description
 */
@Data
public class ProtocolContext {
    private ChannelHandlerContext context;
    private Object object;
    private ProtocolEnum procotolType;
}
