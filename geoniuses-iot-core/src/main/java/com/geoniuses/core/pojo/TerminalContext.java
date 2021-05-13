package com.geoniuses.core.pojo;


import com.geoniuses.core.enums.ProtocolEnum;
import io.netty.channel.ChannelHandlerContext;

import java.util.Objects;

/**
 * @Auther Created by l.wang on 2019/10/16.
 */
public class TerminalContext {

    private String createTime; //创建时间
    private String sessionId;//sessionId
    private String remoteIp;//ip
    private String finger;//服务唯一id
    private ProtocolEnum terminalType;//终端类型
    private ChannelHandlerContext ctx;//通道

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getFinger() {
        return finger;
    }

    public void setFinger(String finger) {
        this.finger = finger;
    }

    public ProtocolEnum getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(ProtocolEnum terminalType) {
        this.terminalType = terminalType;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TerminalContext that = (TerminalContext) o;
        return Objects.equals(createTime, that.createTime) &&
                Objects.equals(sessionId, that.sessionId) &&
                Objects.equals(remoteIp, that.remoteIp) &&
                Objects.equals(finger, that.finger) &&
                terminalType == that.terminalType &&
                Objects.equals(ctx, that.ctx);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createTime, sessionId, remoteIp, finger, terminalType, ctx);
    }

    @Override
    public String toString() {
        return "TerminalContext{" +
                "createTime='" + createTime + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", remoteIp='" + remoteIp + '\'' +
                ", finger='" + finger + '\'' +
                ", terminalType=" + terminalType +
                ", ctx=" + ctx +
                '}';
    }
}
