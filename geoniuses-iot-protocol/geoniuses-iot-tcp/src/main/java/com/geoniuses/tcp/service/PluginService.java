package com.geoniuses.tcp.service;

import io.netty.buffer.ByteBuf;

import java.util.Map;

/**
 * @Author liuxin
 * @Date: 2021/4/22 13:51
 * @Description:
 */
public interface PluginService {
    public void service();

    public Map<String, Object> parser(ByteBuf byteBuf);
}
