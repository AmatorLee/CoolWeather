package com.lgq.coolweather.db;

/**
 * Created by DELL1 on 2016/7/29.
 */
public class Province {
    private String ProvinceName;
    private String ProvinceCode;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceCode() {
        return ProvinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        ProvinceCode = provinceCode;
    }

    public String getProvinceName() {
        return ProvinceName;
    }

    public void setProvinceName(String provinceName) {
        ProvinceName = provinceName;
    }
}
