/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.xml.handler;

import com.cn.bean.xml.WeiXinMenuClickResult;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author LFeng
 */
public class WeiXinMenuClickResultHandler extends DefaultHandler {

    private WeiXinMenuClickResult result;
    //用来存放每次遍历后的元素名称(节点名称)  
    private String tagName;

    public WeiXinMenuClickResult getResult() {
        return result;
    }

    public void setResult(WeiXinMenuClickResult result) {
        this.result = result;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public void endDocument() throws SAXException {
        
    }

    @Override
    public void startDocument() throws SAXException {
        result = new WeiXinMenuClickResult();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        this.tagName = null;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        this.tagName = qName;
    }
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.tagName != null) {
            String data = new String(ch, start, length);
            if (this.tagName.compareTo("ToUserName") == 0) {
                this.result.setToUserName(data);
            }
            if (this.tagName.compareTo("FromUserName") == 0) {
                this.result.setFromUserName(data);
            }
            if (this.tagName.compareTo("CreateTime") == 0) {
                this.result.setCreateTime(data);
            }
            if (this.tagName.compareTo("MsgType") == 0) {
                this.result.setMsgType(data);
            }
            if (this.tagName.compareTo("Event") == 0) {
                this.result.setEvent(data);
            }
            if (this.tagName.compareTo("EventKey") == 0) {
                this.result.setEventKey(data);
            }
            if (this.tagName.compareTo("MenuId") == 0) {
                this.result.setMenuId(data);
            }
        }
    }
    
}
