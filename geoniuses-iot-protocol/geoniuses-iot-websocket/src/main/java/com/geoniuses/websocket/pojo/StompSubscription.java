package com.geoniuses.websocket.pojo;

import io.netty.channel.Channel;
import io.netty.util.internal.ObjectUtil;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author ：zyf
 * @date ：2020/6/3 16:06
 */
//@Data
public class StompSubscription implements Serializable {
    private String id;
    private String destination;
    private Channel channel;
    private String tenantId;

    public StompSubscription(String id, String destination, Channel channel, String tenantId) {
        this.id = ObjectUtil.checkNotNull(id, "id");
        this.destination = ObjectUtil.checkNotNull(destination, "destination");
        this.channel = ObjectUtil.checkNotNull(channel, "channel");
        this.tenantId = ObjectUtil.checkNotNull(tenantId, "tenantId");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StompSubscription that = (StompSubscription) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(destination, that.destination) &&
                Objects.equals(channel, that.channel) &&
                Objects.equals(tenantId, that.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, destination, channel, tenantId);
    }

    @Override
    public String toString() {
        return "StompSubscription{" +
                "id='" + id + '\'' +
                ", destination='" + destination + '\'' +
                ", channel=" + channel +
                ", tenantId='" + tenantId + '\'' +
                '}';
    }
}
