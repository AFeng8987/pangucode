/**
 * Copyright 2018 bejson.com
 */
package org.linlinjava.litemall.core.express.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Auto-generated: 2018-07-19 22:27:22
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class ShipperInfo {

    @JsonProperty("LogisticCode")
    private String logisticCode;
    @JsonProperty("EBusinessID")
    private String eBusinessID;
    @JsonProperty("Code")
    private String code;

    public String getLogisticCode() {
        return logisticCode;
    }

    public void setLogisticCode(String logisticCode) {
        this.logisticCode = logisticCode;
    }

    public String geteBusinessID() {
        return eBusinessID;
    }

    public void seteBusinessID(String eBusinessID) {
        this.eBusinessID = eBusinessID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Shippers> getShippers() {
        return shippers;
    }

    public void setShippers(List<Shippers> shippers) {
        this.shippers = shippers;
    }

    @JsonProperty("Success")
    private boolean success;
    @JsonProperty("Shippers")
    private List<Shippers> shippers;


    @Override
    public String toString() {
        return "ExpressInfo{" +
                "LogisticCode='" + logisticCode + '\'' +
                ", Shippers='" + shippers + '\'' +
                ", Code='" + code + '\'' +
                ", Success='" + success + '\'' +
                ", EBusinessID='" + eBusinessID + '\'' +
                '}';
    }
}