package com.geoniuses.core.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IotStationConfig implements Serializable {
    private static final long serialVersionUID = 1L;

//
//    @Override
//    public int hashCode() {
//        int result = montortagCode.hashCode();
//        result = 31 * result + montortagName.hashCode();
//        return result;
//    }

    //主键id
//    @Id
    private String id;

    //监测点编码
    @JsonProperty("station_key")
    private String stationKey;

    //监测项编码
    @JsonProperty("tag_code")
    private String tagCode;

    //监测项名称
    @JsonProperty("tag_name")
    private String tagName;

//    //监测项类型编码
//    @Column(name = "montortag_code")
//    private String montortagCode;

//    //监测项类型名称
//    @Column(name = "montortag_name")
//    private String montortagName;

//    //变量名称
//    @Column(name = "variable_name")
//    private String variableName;
//
//    //最小正常值
//    @Column(name = "min_value")
//    private Double minValue;
//
//    //最大正常值
//    @Column(name = "max_value")
//    private Double maxValue;

    //量程最小值
    @JsonProperty("rangemin_value")
    private Double rangeminValue;

    //量程最大值
    @JsonProperty("rangemax_value")
    private Double rangemaxValue;

    //单位
    @JsonProperty("units")
    private String units;

    //排序号
    @JsonProperty("order_num")
    private Integer orderNum;

//    //最后更新时间
//    @Column(name = "save_date")
//    private Date saveDate;

//    //最后更新值
//    @Column(name = "tag_value")
//    private Double tagValue;

    //是否启用报警
    @JsonProperty("is_alarm")
    private String isAlarm;

    //一级预警始值
    @JsonProperty("level1_start")
    private Double level1Start;

    //一级预警终值
    @JsonProperty("level1_end")
    private Double level1End;

    //二级预警始值
    @JsonProperty("level2_start")
    private Double level2Start;

    //二级预警终值
    @JsonProperty("level2_end")
    private Double level2End;

    //三级预警始值
    @JsonProperty("level3_start")
    private Double level3Start;

    //三级预警终值
    @JsonProperty("level3_end")
    private Double level3End;

    //启用下行预警范围
    @JsonProperty("dolow_warn")
    private Integer dolowWarn;

    //下行一级预警始值
    @JsonProperty("low_level1_start")
    private Double lowLevel1Start;

    //下行一级预警终值
    @JsonProperty("low_level1_end")
    private Double lowLevel1End;


    //下行二级预警始值
    @JsonProperty("low_level2_start")
    private Double lowLevel2Start;

    //下行二级预警终值
    @JsonProperty("low_level2_end")
    private Double lowLevel2End;


    //下行三级预警始值
    @JsonProperty("low_level3_start")
    private Double lowLevel3Start;

    //下行三级预警终值
    @JsonProperty("low_level3_end")
    private Double lowLevel3End;

//    //添加时间
//    @Column(name = "crt_time")
//    private Date crtTime;

    //添加人姓名
    @JsonProperty("crt_user")
    private String crtUser;

    //租户id
    @JsonProperty("tenant_id")
    private String tenantId;

    /**
     * 监测点名称
     */
    @JsonProperty("station_name")
    private String stationName;

    /**
     * 大行业编码
     */
    @JsonProperty("big_industry_code")
    private String bigIndustryCode;

    /**
     * 大行业名称
     */
    @JsonProperty("big_industry_name")
    private String bigIndustryName;

    /**
     * 中行业编码
     */
    @JsonProperty("mid_industry_code")
    private String midIndustryCode;

    /**
     * 中行业名称
     */
    @JsonProperty("mid_industry_name")
    private String midIndustryName;

    /**
     * 小行业编码
     */
    @JsonProperty("min_industry_code")
    private String minIndustryCode;

    /**
     * 小行业名称
     */
    @JsonProperty("min_industry_name")
    private String minIndustryName;

    /**
     * 地理中心坐标X
     */
//    @JsonProperty("X")
    private Double x;

    /**
     * 地理中心坐标y
     */
//    @JsonProperty("Y")
    private Double y;

    /**
     * 高程
     */
//    @JsonProperty("Z")
    private Double z;

}
