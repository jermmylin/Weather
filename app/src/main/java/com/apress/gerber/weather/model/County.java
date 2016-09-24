package com.apress.gerber.weather.model;

/**
 * Created by Administrator on 2016/9/18.
 */
public class County {
    private int id;
    private String CountyName;
    private String CountyCode;
    private int cityId;

    public int getId() {
        return id;
    }

    public int getCityId() {
        return cityId;
    }

    public String getCountyCode() {
        return CountyCode;
    }

    public String getCountyName() {
        return CountyName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setCountyCode(String countyCode) {
        this.CountyCode = countyCode;
    }

    public void setCountyName(String countyName) {
        this.CountyName = countyName;
    }
}
