package org.linlinjava.litemall.db.dto;

import java.time.LocalDateTime;

public class LitemallAllianceGroupDto {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_group.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_group.group_name
     *
     * @mbg.generated
     */
    private String groupName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_group.group_address
     *
     * @mbg.generated
     */
    private String groupAddress;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_group.profits_pro
     *
     * @mbg.generated
     */
    private String profitsPro;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_group.add_time
     *
     * @mbg.generated
     */
    private LocalDateTime addTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_group.deleted
     *
     * @mbg.generated
     */
    private Boolean deleted;

    private String sales;

    private String profit;

    private String statisticalTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupAddress() {
        return groupAddress;
    }

    public void setGroupAddress(String groupAddress) {
        this.groupAddress = groupAddress;
    }

    public String getProfitsPro() {
        return profitsPro;
    }

    public void setProfitsPro(String profitsPro) {
        this.profitsPro = profitsPro;
    }

    public LocalDateTime getAddTime() {
        return addTime;
    }

    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getSales() {
        return sales;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getStatisticalTime() {
        return statisticalTime;
    }

    public void setStatisticalTime(String statisticalTime) {
        this.statisticalTime = statisticalTime;
    }
}