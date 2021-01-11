package org.linlinjava.litemall.admin.vo;

import java.time.LocalDate;
import java.util.List;

public class OrderExportVo {

    private List<Integer> ids;
    private String orderSn;
    private String payOrderSn;
    private LocalDate orderAddStart;
    private LocalDate orderAddEnd;
    private LocalDate orderCloseStart;
    private LocalDate orderCloseEnd;
    private String type;
    private String afterSaleSn;
    private Integer refundStatus;
    private Integer serviceType;
    private String payId;
    private String allianceName;
    private String userName;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAfterSaleSn() {
        return afterSaleSn;
    }

    public void setAfterSaleSn(String afterSaleSn) {
        this.afterSaleSn = afterSaleSn;
    }

    public Integer getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(Integer refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Integer getServiceType() {
        return serviceType;
    }

    public void setServiceType(Integer serviceType) {
        this.serviceType = serviceType;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }
    public String getPayOrderSn() {
        return payOrderSn;
    }

    public void setPayOrderSn(String payOrderSn) {
        this.payOrderSn = payOrderSn;
    }

    public String getAllianceName() {
        return allianceName;
    }

    public void setAllianceName(String allianceName) {
        this.allianceName = allianceName;
    }

    public LocalDate getOrderAddEnd() {
        return orderAddEnd;
    }

    public void setOrderAddEnd(LocalDate orderAddEnd) {
        this.orderAddEnd = orderAddEnd;
    }


    public LocalDate getOrderAddStart() {
        return orderAddStart;
    }

    public void setOrderAddStart(LocalDate orderAddStart) {
        this.orderAddStart = orderAddStart;
    }
    public LocalDate getOrderCloseStart() {
        return orderCloseStart;
    }

    public void setOrderCloseStart(LocalDate orderCloseStart) {
        this.orderCloseStart = orderCloseStart;
    }

    public LocalDate getOrderCloseEnd() {
        return orderCloseEnd;
    }

    public void setOrderCloseEnd(LocalDate orderCloseEnd) {
        this.orderCloseEnd = orderCloseEnd;
    }

}
