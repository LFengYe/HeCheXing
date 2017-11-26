/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.xml;

/**
 *
 * @author LFeng
 */
public class SMSResult {
    private String status;
    private String message;
    private int remainPoint;
    private int taskID;
    private int successCounts;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRemainPoint() {
        return remainPoint;
    }

    public void setRemainPoint(int remainPoint) {
        this.remainPoint = remainPoint;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getSuccessCounts() {
        return successCounts;
    }

    public void setSuccessCounts(int successCounts) {
        this.successCounts = successCounts;
    }
    
}
