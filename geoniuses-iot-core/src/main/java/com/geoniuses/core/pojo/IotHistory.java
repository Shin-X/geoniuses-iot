package com.geoniuses.core.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author ：zyf
 * @date ：2020/7/23 17:54
 * ES 历史表对象
 * <p>
 * "elevation": 10.0,
 * "station_name": "89号兆征路_林业局路中",
 * "device_key": "89",
 * "tag_code": "3508211000_011291_00033_1212_1",
 * "mid_industry_code": "12",
 * "tag_name": "排水89_信噪比",
 * "min_industry_code": "91",
 * "units": "dB",
 * "mid_industry_name": "排水井盖大类",
 * "point": [
 * 116.347082,
 * 25.838613
 * ],
 * "is_delete": 0,
 * "tag_value": "8.0",
 * "big_industry_name": "排水",
 * "min_industry_name": "排水井盖",
 * "big_industry_code": "01",
 * "level_now": 0,
 * "station_key": "3508211000_011291_00033",
 * "add_time": "2020-07-27 14:01:14",
 * "talent_id": "bd3df3a4add446e9b0f3ac919e5415ef"
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotHistory implements Serializable {

    private String station_key;
    private String tag_code;
    private String tag_value;
    private String add_time;
    private String talent_id;
    private String device_key;
    private String units;
    private String big_industry_code;
    private String big_industry_name;
    private String mid_industry_code;
    private String mid_industry_name;
    private String min_industry_code;
    private String min_industry_name;
    private String station_name;
    private String tag_name;
    private String is_delete;
    private Float elevation;
    private String point;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IotHistory that = (IotHistory) o;
        return Objects.equals(station_key, that.station_key) &&
                Objects.equals(tag_code, that.tag_code) &&
                Objects.equals(tag_value, that.tag_value) &&
                Objects.equals(add_time, that.add_time) &&
                Objects.equals(talent_id, that.talent_id) &&
                Objects.equals(device_key, that.device_key) &&
                Objects.equals(units, that.units) &&
                Objects.equals(big_industry_code, that.big_industry_code) &&
                Objects.equals(big_industry_name, that.big_industry_name) &&
                Objects.equals(mid_industry_code, that.mid_industry_code) &&
                Objects.equals(mid_industry_name, that.mid_industry_name) &&
                Objects.equals(min_industry_code, that.min_industry_code) &&
                Objects.equals(min_industry_name, that.min_industry_name) &&
                Objects.equals(station_name, that.station_name) &&
                Objects.equals(tag_name, that.tag_name) &&
                Objects.equals(is_delete, that.is_delete) &&
                Objects.equals(elevation, that.elevation) &&
                Objects.equals(point, that.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(station_key, tag_code, tag_value, add_time, talent_id, device_key, units, big_industry_code, big_industry_name, mid_industry_code, mid_industry_name, min_industry_code, min_industry_name, station_name, tag_name, is_delete, elevation, point);
    }

    public String getStation_key() {
        return station_key;
    }

    public void setStation_key(String station_key) {
        this.station_key = station_key;
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

    public String getBig_industry_code() {
        return big_industry_code;
    }

    public void setBig_industry_code(String big_industry_code) {
        this.big_industry_code = big_industry_code;
    }

    public String getBig_industry_name() {
        return big_industry_name;
    }

    public void setBig_industry_name(String big_industry_name) {
        this.big_industry_name = big_industry_name;
    }

    public String getMid_industry_code() {
        return mid_industry_code;
    }

    public void setMid_industry_code(String mid_industry_code) {
        this.mid_industry_code = mid_industry_code;
    }

    public String getMid_industry_name() {
        return mid_industry_name;
    }

    public void setMid_industry_name(String mid_industry_name) {
        this.mid_industry_name = mid_industry_name;
    }

    public String getMin_industry_code() {
        return min_industry_code;
    }

    public void setMin_industry_code(String min_industry_code) {
        this.min_industry_code = min_industry_code;
    }

    public String getMin_industry_name() {
        return min_industry_name;
    }

    public void setMin_industry_name(String min_industry_name) {
        this.min_industry_name = min_industry_name;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public String getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(String is_delete) {
        this.is_delete = is_delete;
    }

    public Float getElevation() {
        return elevation;
    }

    public void setElevation(Float elevation) {
        this.elevation = elevation;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "IotHistory{" +
                "station_key='" + station_key + '\'' +
                ", tag_code='" + tag_code + '\'' +
                ", tag_value='" + tag_value + '\'' +
                ", add_time='" + add_time + '\'' +
                ", talent_id='" + talent_id + '\'' +
                ", device_key='" + device_key + '\'' +
                ", units='" + units + '\'' +
                ", big_industry_code='" + big_industry_code + '\'' +
                ", big_industry_name='" + big_industry_name + '\'' +
                ", mid_industry_code='" + mid_industry_code + '\'' +
                ", mid_industry_name='" + mid_industry_name + '\'' +
                ", min_industry_code='" + min_industry_code + '\'' +
                ", min_industry_name='" + min_industry_name + '\'' +
                ", station_name='" + station_name + '\'' +
                ", tag_name='" + tag_name + '\'' +
                ", is_delete='" + is_delete + '\'' +
                ", elevation=" + elevation +
                ", point='" + point + '\'' +
                '}';
    }
}
