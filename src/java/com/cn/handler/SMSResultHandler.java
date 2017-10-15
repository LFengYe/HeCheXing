/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.handler;

import com.cn.bean.SMSResult;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author LFeng
 */
public class SMSResultHandler extends DefaultHandler{
    //构建Student对象  
    private SMSResult result;  
    //用来存放每次遍历后的元素名称(节点名称)  
    private String tagName; 

    public SMSResult getResult() {
        return result;
    }

    public void setResult(SMSResult result) {
        this.result = result;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public void startDocument() throws SAXException {
        result = new SMSResult();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        this.tagName = qName;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        this.tagName = null;
    }

    @Override
    public void endDocument() throws SAXException {
        
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.tagName != null) {
            String data = new String(ch, start, length);
            if (this.tagName.compareTo("returnstatus") == 0) {
                this.result.setStatus(data);
            }
            if (this.tagName.compareTo("message") == 0) {
                this.result.setMessage(data);
            }
            if (this.tagName.compareTo("remainpoint") == 0) {
                this.result.setRemainPoint(Integer.valueOf(data));
            }
            if (this.tagName.compareTo("taskID") == 0) {
                this.result.setTaskID(Integer.valueOf(data));
            }
            if (this.tagName.compareTo("successCounts") == 0) {
                this.result.setSuccessCounts(Integer.valueOf(data));
            }
        }
    }
}
