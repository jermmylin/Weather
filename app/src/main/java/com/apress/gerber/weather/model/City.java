package com.apress.gerber.weather.model;

/**
 * Created by Administrator on 2016/9/18.
 */
public class City {
    private int id;
    private String CityName;
    private String CityCode;
    private int provinceId;

    public int getId(){
        return id;
    }

    public String getCityName() {
        return CityName;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public String getCityCode() {
        return CityCode;
    }

    public void setCityCode(String cityCode) {
        this.CityCode = cityCode;
    }

    public void setCityName(String cityName) {
        this.CityName = cityName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
