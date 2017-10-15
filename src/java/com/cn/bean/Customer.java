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
public class Customer {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "登录名", operate = "input")
    private String loginUserName;
    @FieldDescription(description = "登录密码", operate = "input")
    private String loginPassword;
    @FieldDescription(description = "用户姓名", operate = "input")
    private String customerName;
    @FieldDescription(description = "用户身份证", operate = "input")
    private String customerCardInfo;
    @FieldDescription(description = "用户地址", operate = "input")
    private String customerAddress;
    @FieldDescription(description = "定位数据接口地址", operate = "input")
    private String customerGPSServerUrl;
    @FieldDescription(description = "GPS数据库IP地址", operate = "input")
    private String customerGPSDatabaseIP;
    @FieldDescription(description = "GPS数据库端口", operate = "input")
    private String customerGPSDatabasePort;

    public String getLoginUserName() {
        return loginUserName;
    }

    public void setLoginUserName(String loginUserName) {
        this.loginUserName = loginUserName;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerCardInfo() {
        return customerCardInfo;
    }

    public void setCustomerCardInfo(String customerCardInfo) {
        this.customerCardInfo = customerCardInfo;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerGPSServerUrl() {
        return customerGPSServerUrl;
    }

    public void setCustomerGPSServerUrl(String customerGPSServerUrl) {
        this.customerGPSServerUrl = customerGPSServerUrl;
    }

    public String getCustomerGPSDatabaseIP() {
        return customerGPSDatabaseIP;
    }

    public void setCustomerGPSDatabaseIP(String customerGPSDatabaseIP) {
        this.customerGPSDatabaseIP = customerGPSDatabaseIP;
    }

    public String getCustomerGPSDatabasePort() {
        return customerGPSDatabasePort;
    }

    public void setCustomerGPSDatabasePort(String customerGPSDatabasePort) {
        this.customerGPSDatabasePort = customerGPSDatabasePort;
    }
    
}
