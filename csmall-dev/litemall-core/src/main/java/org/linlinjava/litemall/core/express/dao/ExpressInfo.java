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
public class ExpressInfo {

    @JsonProperty("LogisticCode")
    private String logisticCode;
    @JsonProperty("ShipperCode")
    private String shipperCode;
    @JsonProperty("Traces")
    private List<Traces> traces;
    @JsonProperty("State")
    private String state;
    @JsonProperty("EBusinessID")
    private String eBusinessID;
    @JsonProperty("Success")
    private boolean success;
    @JsonProperty("Location")
    private String location;
    @JsonProperty("StateEx")
    private String stateEx;

    @JsonProperty("ShipperName")
    private String shipperName;

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }




    public String getLogisticCode() {
        return logisticCode;
    }

    public void setLogisticCode(String logisticCode) {
        this.logisticCode = logisticCode;
    }

    public String getShipperCode() {
        return shipperCode;
    }

    public void setShipperCode(String shipperCode) {
        this.shipperCode = shipperCode;
    }

    public List<Traces> getTraces() {
        return traces;
    }

    public void setTraces(List<Traces> traces) {
        this.traces = traces;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String geteBusinessID() {
        return eBusinessID;
    }

    public void seteBusinessID(String eBusinessID) {
        this.eBusinessID = eBusinessID;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStateEx() {
        return stateEx;
    }

    public void setStateEx(String stateEx) {
        this.stateEx = stateEx;
    }





    @Override
    public String toString() {
        return "ExpressInfo{" +
                "LogisticCode='" + logisticCode + '\'' +
                ", ShipperCode='" + shipperCode + '\'' +
                ", Traces=" + traces +
                ", State='" + state + '\'' +
                ", EBusinessID='" + eBusinessID + '\'' +
                ", Success=" + success +
                ", Location=" + location +
                ", StateEx='" + stateEx + '\'' +
                '}';
    }
}