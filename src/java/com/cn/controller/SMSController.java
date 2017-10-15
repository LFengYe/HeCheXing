/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.cn.util.Units;

/**
 *
 * @author LFeng
 */
public class SMSController {

    String smsServerUrl = "http://139.224.13.203:8088/sms.aspx";
    String smsServerUrlGBK = "http://139.224.13.203:8088/smsGBK.aspx";
    String smsStatusUrl = "http://139.224.13.203:8088/statusApi.aspx";
    String userid = "474";
    String account = "瘦身版必到牌=临沂赛特";
    String password = "lyst1031";

    public String sendSMSMessage(String mobile, String content) {
        String sendBody = "action=send&userid=" + userid + "&account=" + account + "&password=" + password
                + "&mobile=" + mobile + "&content=" + content + "&sendTime=&extno=";
        return Units.requestWithNoHeaderKey(smsServerUrlGBK, sendBody);
    }
    
    public String sendSMSMessageWithUTF(String mobile, String content) {
        String sendBody = "action=send&userid=" + userid + "&account=" + account + "&password=" + password
                + "&mobile=" + mobile + "&content=" + content + "&sendTime=&extno=";
        return Units.requestWithPost(smsServerUrl, sendBody);
    }
    
    public String getSendStatus() {
        String sendBody = "action=query&userid=" + userid + "&account=" + account + "&password=" + password;
        return Units.requestWithNoHeaderKey(smsStatusUrl, sendBody);
    }
}
