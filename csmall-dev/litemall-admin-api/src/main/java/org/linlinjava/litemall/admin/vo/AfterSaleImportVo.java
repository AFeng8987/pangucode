package org.linlinjava.litemall.admin.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AfterSaleImportVo {

    @ExcelProperty("用户账号")
    private String userName;
    @ExcelProperty("订单号")
    private String orderSn;
    @ExcelProperty("退款单号")
    private String afterSaleSn;
    @ExcelProperty("订单状态")
    private String orderStatusName;
    @ExcelProperty("支付方式")
    private String payType;
    @ExcelProperty("支付单号")
    private String payId;
    @ExcelProperty("退款状态")
    private String statusName;
    @ExcelProperty("退款类型")
    private String serviceTypeName;
    @ExcelProperty("退款金额(元)")
    private BigDecimal amount;
    @ExcelProperty("退款申请时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private String addTime;
    @ExcelProperty("供货工厂")
    private String plantName;
    @ExcelProperty("上游退款单号")
    private String returnPayId;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public String getAfterSaleSn() {
        return afterSaleSn;
    }

    public void setAfterSaleSn(String afterSaleSn) {
        this.afterSaleSn = afterSaleSn;
    }

    public String getOrderStatusName() {
        return orderStatusName;
    }

    public void setOrderStatusName(String orderStatusName) {
        this.orderStatusName = orderStatusName;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getReturnPayId() {
        return returnPayId;
    }

    public void setReturnPayId(String returnPayId) {
        this.returnPayId = returnPayId;
    }




}
