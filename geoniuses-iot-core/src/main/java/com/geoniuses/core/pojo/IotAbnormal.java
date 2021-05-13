package com.geoniuses.core.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author ：zyf
 * @date ：2020/7/23 17:55
 * ES 异常表对象
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotAbnormal {

    private String crtTime;
    private String crtUser;
    private String station_key;
    private String remark;
    private String tag_code;
    private String tag_value;
    private String add_time;
    private String talent_id;
    private String device_key;
    private String units;
    private Boolean is_delete;
    private Double rangemin_value;
    private Double rangemax_value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IotAbnormal that = (IotAbnormal) o;
        return Objects.equals(crtTime, that.crtTime) &&
                Objects.equals(crtUser, that.crtUser) &&
                Objects.equals(station_key, that.station_key) &&
                Objects.equals(remark, that.remark) &&
                Objects.equals(tag_code, that.tag_code) &&
                Objects.equals(tag_value, that.tag_value) &&
                Objects.equals(add_time, that.add_time) &&
                Objects.equals(talent_id, that.talent_id) &&
                Objects.equals(device_key, that.device_key) &&
                Objects.equals(units, that.units) &&
                Objects.equals(is_delete, that.is_delete) &&
                Objects.equals(rangemin_value, that.rangemin_value) &&
                Objects.equals(rangemax_value, that.rangemax_value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(crtTime, crtUser, station_key, remark, tag_code, tag_value, add_time, talent_id, device_key, units, is_delete, rangemin_value, rangemax_value);
    }

    public String getCrtTime() {
        return crtTime;
    }

    public void setCrtTime(String crtTime) {
        this.crtTime = crtTime;
    }

    public String getCrtUser() {
        return crtUser;
    }

    public void setCrtUser(String crtUser) {
        this.crtUser = crtUser;
    }

    public String getStation_key() {
        return station_key;
    }

    public void setStation_key(String station_key) {
        this.station_key = station_key;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTag_code() {
        return tag_code;
    }

    public void setTag_code(String tag_code) {
        this.tag_code = tag_code;
    }

    public String getTag_value() {
        return tag_value;
    }

    public void setTag_value(String tag_value) {
        this.tag_value = tag_value;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getTalent_id() {
        return talent_id;
    }

    public void setTalent_id(String talent_id) {
        this.talent_id = talent_id;
    }

    public String getDevice_key() {
        return device_key;
    }

    public void setDevice_key(String device_key) {
        this.device_key = device_key;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public Boolean getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(Boolean is_delete) {
        this.is_delete = is_delete;
    }

    public Double getRangemin_value() {
        return rangemin_value;
    }

    public void setRangemin_value(Double rangemin_value) {
        this.rangemin_value = rangemin_value;
    }

    public Double getRangemax_value() {
        return rangemax_value;
    }

    public void setRangemax_value(Double rangemax_value) {
        this.rangemax_value = rangemax_value;
    }

    @Override
    public String toString() {
        return "IotAbnormal{" +
                "crtTime='" + crtTime + '\'' +
                ", crtUser='" + crtUser + '\'' +
                ", station_key='" + station_key + '\'' +
                ", remark='" + remark + '\'' +
                ", tag_code='" + tag_code + '\'' +
                ", tag_value='" + tag_value + '\'' +
                ", add_time='" + add_time + '\'' +
                ", talent_id='" + talent_id + '\'' +
                ", device_key='" + device_key + '\'' +
                ", units='" + units + '\'' +
                ", is_delete=" + is_delete +
                ", rangemin_value=" + rangemin_value +
                ", rangemax_value=" + rangemax_value +
                '}';
    }
}
