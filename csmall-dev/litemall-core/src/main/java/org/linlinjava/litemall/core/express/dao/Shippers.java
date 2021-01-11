package org.linlinjava.litemall.core.express.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author kevin
 */
public class Shippers {

    @JsonProperty("ShipperCode")
    private String shipperCode;

    @JsonProperty("ShipperName")
    private String shipperName;

    public String getShipperCode() {
        return shipperCode;
    }

    public void setShipperCode(String shipperCode) {
        this.shipperCode = shipperCode;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }


}
