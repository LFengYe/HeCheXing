/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean;

/**
 *
 * @author LFeng
 */
public class CustomerDevice {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "登录名", operate = "input")
    private String loginUserName;
    @FieldDescription(description = "设备ID", operate = "input")
    private String deviceID;
    @FieldDescription(description = "车辆品牌", operate = "input")
    private String carBrand;
    @FieldDescription(description = "车型", operate = "input")
    private String carType;
    @FieldDescription(description = "大架号", operate = "input")
    private String griderNum;
    @FieldDescription(description = "购买时间", operate = "input")
    private String buyTime;
    @FieldDescription(description = "购买金额", operate = "input")
    private String buyAmount;
    @FieldDescription(description = "车牌号码", operate = "input")
    private String carNo;
    @FieldDescription(description = "绑定时间", operate = "input")
    private String bandTime;
    @FieldDescription(description = "是否设防")
    private int isLocked;

    public String getLoginUserName() {
        return loginUserName;
    }

    public void setLoginUserName(String loginUserName) {
        this.loginUserName = loginUserName;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getGriderNum() {
        return griderNum;
    }

    public void setGriderNum(String griderNum) {
        this.griderNum = griderNum;
    }

    public String getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(String buyTime) {
        this.buyTime = buyTime;
    }

    public String getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(String buyAmount) {
        this.buyAmount = buyAmount;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getBandTime() {
        return bandTime;
    }

    public void setBandTime(String bandTime) {
        this.bandTime = bandTime;
    }
    
}
