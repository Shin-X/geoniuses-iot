package com.geoniuses.udp.pojo;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.net.InetSocketAddress;

/**
 * Created  zyf in 2019/10/11 9:02
 * 供水、排水 ps协议
 */
@Builder
@Data
@Accessors(chain = true)
public class PsMessage {

    /**
     * 系统识别码
     */
    private int headCode;

    //数据包长度
    private int length;

    //报序号
    private int orderNum;

    //数据包类型 上报历史阶段0x31，结束通讯阶段0x34
    private int funCode;

    //源地址长度
    private int sourcelength;

    //源地址
    private byte[] sourceAddress;

    //目的地地址长度
    private int dstLength;

    //目的地地址
    private byte[] dstAddress;

    //内层协议
    private InnerPsMessage innerPsMessage;

    //校验
    private int outerCrc;

    //远程地址
    private InetSocketAddress inetSocketAddress;

}
