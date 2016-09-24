package com.apress.gerber.weather.model;

/**
 * Created by Administrator on 2016/9/18.
 */
public class Province {
    private int id;
    private String ProvinceName;
    private String ProvinceCode;

    public int getId(){
        return id;
    }

    public String getProvinceName() {
        return ProvinceName;
    }

    public String getProvinceCode() {
        return ProvinceCode;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setProvinceCode(String provinceCode) {
        this.ProvinceCode = provinceCode;
    }

    public void setProvinceName(String provinceName) {
        this.ProvinceName = provinceName;
    }
}
