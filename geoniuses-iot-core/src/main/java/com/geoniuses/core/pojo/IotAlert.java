package com.geoniuses.core.pojo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author ：zyf
 * @date ：2020/7/23 17:54
 * ES 报警表对象
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotAlert implements Serializable {
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
    private String point;
    private Float elevation;
    private Boolean is_send;
    private Boolean is_set;
    private String crt_user;
    private Integer level_now;
    private String do_people;
    private String do_date;

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

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public Float getElevation() {
        return elevation;
    }

    public void setElevation(Float elevation) {
        this.elevation = elevation;
    }

    public Boolean getIs_send() {
        return is_send;
    }

    public void setIs_send(Boolean is_send) {
        this.is_send = is_send;
    }

    public Boolean getIs_set() {
        return is_set;
    }

    public void setIs_set(Boolean is_set) {
        this.is_set = is_set;
    }

    public String getCrt_user() {
        return crt_user;
    }

    public void setCrt_user(String crt_user) {
        this.crt_user = crt_user;
    }


    public Integer getLevel_now() {
        return level_now;
    }

    public void setLevel_now(Integer level_now) {
        this.level_now = level_now;
    }

    public String getDo_people() {
        return do_people;
    }

    public void setDo_people(String do_people) {
        this.do_people = do_people;
    }

    public String getDo_date() {
        return do_date;
    }

    public void setDo_date(String do_date) {
        this.do_date = do_date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IotAlert iotAlert = (IotAlert) o;
        return Objects.equals(station_key, iotAlert.station_key) &&
                Objects.equals(tag_code, iotAlert.tag_code) &&
                Objects.equals(tag_value, iotAlert.tag_value) &&
                Objects.equals(add_time, iotAlert.add_time) &&
                Objects.equals(talent_id, iotAlert.talent_id) &&
                Objects.equals(device_key, iotAlert.device_key) &&
                Objects.equals(units, iotAlert.units) &&
                Objects.equals(big_industry_code, iotAlert.big_industry_code) &&
                Objects.equals(big_industry_name, iotAlert.big_industry_name) &&
                Objects.equals(mid_industry_code, iotAlert.mid_industry_code) &&
                Objects.equals(mid_industry_name, iotAlert.mid_industry_name) &&
                Objects.equals(min_industry_code, iotAlert.min_industry_code) &&
                Objects.equals(min_industry_name, iotAlert.min_industry_name) &&
                Objects.equals(station_name, iotAlert.station_name) &&
                Objects.equals(tag_name, iotAlert.tag_name) &&
                Objects.equals(point, iotAlert.point) &&
                Objects.equals(elevation, iotAlert.elevation) &&
                Objects.equals(is_send, iotAlert.is_send) &&
                Objects.equals(is_set, iotAlert.is_set) &&
                Objects.equals(crt_user, iotAlert.crt_user) &&
                Objects.equals(level_now, iotAlert.level_now) &&
                Objects.equals(do_people, iotAlert.do_people) &&
                Objects.equals(do_date, iotAlert.do_date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(station_key, tag_code, tag_value, add_time, talent_id, device_key, units, big_industry_code, big_industry_name, mid_industry_code, mid_industry_name, min_industry_code, min_industry_name, station_name, tag_name, point, elevation, is_send, is_set, crt_user, level_now, do_people, do_date);
    }

    @Override
    public String toString() {
        return "IotAlert{" +
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
                ", point='" + point + '\'' +
                ", elevation=" + elevation +
                ", is_send=" + is_send +
                ", is_set=" + is_set +
                ", crt_user='" + crt_user + '\'' +
                ", level_now=" + level_now +
                ", do_people='" + do_people + '\'' +
                ", do_date='" + do_date + '\'' +
                '}';
    }
}
