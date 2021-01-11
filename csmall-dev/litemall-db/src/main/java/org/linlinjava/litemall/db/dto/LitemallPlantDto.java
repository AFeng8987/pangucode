package org.linlinjava.litemall.db.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class LitemallPlantDto {
    private Integer id;

    private String plantName;

    private String plantContacts;

    private String plantPhone;

    private String PlantAddress;

    private String sendAddress;

    private String sendPhone;

    private String sendContacts;

    private String receiveContacts;

    private String receivePhone;

    private String receiveAddress;

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private LocalDateTime addTime;

    private int type;

    private int inventory;

    private int sales;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getPlantContacts() {
        return plantContacts;
    }

    public void setPlantContacts(String plantContacts) {
        this.plantContacts = plantContacts;
    }

    public String getPlantPhone() {
        return plantPhone;
    }

    public void setPlantPhone(String plantPhone) {
        this.plantPhone = plantPhone;
    }

    public String getPlantAddress() {
        return PlantAddress;
    }

    public void setPlantAddress(String plantAddress) {
        PlantAddress = plantAddress;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public LocalDateTime getAddTime() {
        return addTime;
    }

    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }

    public String getSendAddress() {
        return sendAddress;
    }

    public void setSendAddress(String sendAddress) {
        this.sendAddress = sendAddress;
    }

    public String getSendPhone() {
        return sendPhone;
    }

    public void setSendPhone(String sendPhone) {
        this.sendPhone = sendPhone;
    }

    public String getSendContacts() {
        return sendContacts;
    }

    public void setSendContacts(String sendContacts) {
        this.sendContacts = sendContacts;
    }

    public String getReceiveContacts() {
        return receiveContacts;
    }

    public void setReceiveContacts(String receiveContacts) {
        this.receiveContacts = receiveContacts;
    }

    public String getReceivePhone() {
        return receivePhone;
    }

    public void setReceivePhone(String receivePhone) {
        this.receivePhone = receivePhone;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }
}
