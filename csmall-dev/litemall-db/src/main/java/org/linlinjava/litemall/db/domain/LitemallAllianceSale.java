package org.linlinjava.litemall.db.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class LitemallAllianceSale {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_sale.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_sale.alliance_name
     *
     * @mbg.generated
     */
    private String allianceName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_sale.alliance_userid
     *
     * @mbg.generated
     */
    private Integer allianceUserid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_sale.order_sn
     *
     * @mbg.generated
     */
    private String orderSn;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_sale.order_goods_id
     *
     * @mbg.generated
     */
    private Integer orderGoodsId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_sale.category_id
     *
     * @mbg.generated
     */
    private Integer categoryId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_sale.goods_name
     *
     * @mbg.generated
     */
    private String goodsName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_sale.goods_number
     *
     * @mbg.generated
     */
    private Integer goodsNumber;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_sale.goods_price
     *
     * @mbg.generated
     */
    private BigDecimal goodsPrice;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_sale.plant_cost_price
     *
     * @mbg.generated
     */
    private BigDecimal plantCostPrice;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_sale.coupon_price
     *
     * @mbg.generated
     */
    private BigDecimal couponPrice;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_sale.total_profit
     *
     * @mbg.generated
     */
    private BigDecimal totalProfit;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_sale.group_id
     *
     * @mbg.generated
     */
    private Integer groupId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_sale.plant_id
     *
     * @mbg.generated
     */
    private Integer plantId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_sale.add_time
     *
     * @mbg.generated
     */
    private LocalDateTime addTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_sale.order_close_time
     *
     * @mbg.generated
     */
    private LocalDateTime orderCloseTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column litemall_alliance_sale.order_add_time
     *
     * @mbg.generated
     */
    private LocalDateTime orderAddTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_alliance_sale.id
     *
     * @return the value of litemall_alliance_sale.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_alliance_sale.id
     *
     * @param id the value for litemall_alliance_sale.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_alliance_sale.alliance_name
     *
     * @return the value of litemall_alliance_sale.alliance_name
     *
     * @mbg.generated
     */
    public String getAllianceName() {
        return allianceName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_alliance_sale.alliance_name
     *
     * @param allianceName the value for litemall_alliance_sale.alliance_name
     *
     * @mbg.generated
     */
    public void setAllianceName(String allianceName) {
        this.allianceName = allianceName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_alliance_sale.alliance_userid
     *
     * @return the value of litemall_alliance_sale.alliance_userid
     *
     * @mbg.generated
     */
    public Integer getAllianceUserid() {
        return allianceUserid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_alliance_sale.alliance_userid
     *
     * @param allianceUserid the value for litemall_alliance_sale.alliance_userid
     *
     * @mbg.generated
     */
    public void setAllianceUserid(Integer allianceUserid) {
        this.allianceUserid = allianceUserid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_alliance_sale.order_sn
     *
     * @return the value of litemall_alliance_sale.order_sn
     *
     * @mbg.generated
     */
    public String getOrderSn() {
        return orderSn;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_alliance_sale.order_sn
     *
     * @param orderSn the value for litemall_alliance_sale.order_sn
     *
     * @mbg.generated
     */
    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_alliance_sale.order_goods_id
     *
     * @return the value of litemall_alliance_sale.order_goods_id
     *
     * @mbg.generated
     */
    public Integer getOrderGoodsId() {
        return orderGoodsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_alliance_sale.order_goods_id
     *
     * @param orderGoodsId the value for litemall_alliance_sale.order_goods_id
     *
     * @mbg.generated
     */
    public void setOrderGoodsId(Integer orderGoodsId) {
        this.orderGoodsId = orderGoodsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_alliance_sale.category_id
     *
     * @return the value of litemall_alliance_sale.category_id
     *
     * @mbg.generated
     */
    public Integer getCategoryId() {
        return categoryId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_alliance_sale.category_id
     *
     * @param categoryId the value for litemall_alliance_sale.category_id
     *
     * @mbg.generated
     */
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_alliance_sale.goods_name
     *
     * @return the value of litemall_alliance_sale.goods_name
     *
     * @mbg.generated
     */
    public String getGoodsName() {
        return goodsName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_alliance_sale.goods_name
     *
     * @param goodsName the value for litemall_alliance_sale.goods_name
     *
     * @mbg.generated
     */
    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_alliance_sale.goods_number
     *
     * @return the value of litemall_alliance_sale.goods_number
     *
     * @mbg.generated
     */
    public Integer getGoodsNumber() {
        return goodsNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_alliance_sale.goods_number
     *
     * @param goodsNumber the value for litemall_alliance_sale.goods_number
     *
     * @mbg.generated
     */
    public void setGoodsNumber(Integer goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_alliance_sale.goods_price
     *
     * @return the value of litemall_alliance_sale.goods_price
     *
     * @mbg.generated
     */
    public BigDecimal getGoodsPrice() {
        return goodsPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_alliance_sale.goods_price
     *
     * @param goodsPrice the value for litemall_alliance_sale.goods_price
     *
     * @mbg.generated
     */
    public void setGoodsPrice(BigDecimal goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_alliance_sale.plant_cost_price
     *
     * @return the value of litemall_alliance_sale.plant_cost_price
     *
     * @mbg.generated
     */
    public BigDecimal getPlantCostPrice() {
        return plantCostPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_alliance_sale.plant_cost_price
     *
     * @param plantCostPrice the value for litemall_alliance_sale.plant_cost_price
     *
     * @mbg.generated
     */
    public void setPlantCostPrice(BigDecimal plantCostPrice) {
        this.plantCostPrice = plantCostPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_alliance_sale.coupon_price
     *
     * @return the value of litemall_alliance_sale.coupon_price
     *
     * @mbg.generated
     */
    public BigDecimal getCouponPrice() {
        return couponPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_alliance_sale.coupon_price
     *
     * @param couponPrice the value for litemall_alliance_sale.coupon_price
     *
     * @mbg.generated
     */
    public void setCouponPrice(BigDecimal couponPrice) {
        this.couponPrice = couponPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_alliance_sale.total_profit
     *
     * @return the value of litemall_alliance_sale.total_profit
     *
     * @mbg.generated
     */
    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_alliance_sale.total_profit
     *
     * @param totalProfit the value for litemall_alliance_sale.total_profit
     *
     * @mbg.generated
     */
    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_alliance_sale.group_id
     *
     * @return the value of litemall_alliance_sale.group_id
     *
     * @mbg.generated
     */
    public Integer getGroupId() {
        return groupId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_alliance_sale.group_id
     *
     * @param groupId the value for litemall_alliance_sale.group_id
     *
     * @mbg.generated
     */
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_alliance_sale.plant_id
     *
     * @return the value of litemall_alliance_sale.plant_id
     *
     * @mbg.generated
     */
    public Integer getPlantId() {
        return plantId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_alliance_sale.plant_id
     *
     * @param plantId the value for litemall_alliance_sale.plant_id
     *
     * @mbg.generated
     */
    public void setPlantId(Integer plantId) {
        this.plantId = plantId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_alliance_sale.add_time
     *
     * @return the value of litemall_alliance_sale.add_time
     *
     * @mbg.generated
     */
    public LocalDateTime getAddTime() {
        return addTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_alliance_sale.add_time
     *
     * @param addTime the value for litemall_alliance_sale.add_time
     *
     * @mbg.generated
     */
    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_alliance_sale.order_close_time
     *
     * @return the value of litemall_alliance_sale.order_close_time
     *
     * @mbg.generated
     */
    public LocalDateTime getOrderCloseTime() {
        return orderCloseTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_alliance_sale.order_close_time
     *
     * @param orderCloseTime the value for litemall_alliance_sale.order_close_time
     *
     * @mbg.generated
     */
    public void setOrderCloseTime(LocalDateTime orderCloseTime) {
        this.orderCloseTime = orderCloseTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column litemall_alliance_sale.order_add_time
     *
     * @return the value of litemall_alliance_sale.order_add_time
     *
     * @mbg.generated
     */
    public LocalDateTime getOrderAddTime() {
        return orderAddTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column litemall_alliance_sale.order_add_time
     *
     * @param orderAddTime the value for litemall_alliance_sale.order_add_time
     *
     * @mbg.generated
     */
    public void setOrderAddTime(LocalDateTime orderAddTime) {
        this.orderAddTime = orderAddTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_sale
     *
     * @mbg.generated
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", allianceName=").append(allianceName);
        sb.append(", allianceUserid=").append(allianceUserid);
        sb.append(", orderSn=").append(orderSn);
        sb.append(", orderGoodsId=").append(orderGoodsId);
        sb.append(", categoryId=").append(categoryId);
        sb.append(", goodsName=").append(goodsName);
        sb.append(", goodsNumber=").append(goodsNumber);
        sb.append(", goodsPrice=").append(goodsPrice);
        sb.append(", plantCostPrice=").append(plantCostPrice);
        sb.append(", couponPrice=").append(couponPrice);
        sb.append(", totalProfit=").append(totalProfit);
        sb.append(", groupId=").append(groupId);
        sb.append(", plantId=").append(plantId);
        sb.append(", addTime=").append(addTime);
        sb.append(", orderCloseTime=").append(orderCloseTime);
        sb.append(", orderAddTime=").append(orderAddTime);
        sb.append("]");
        return sb.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_sale
     *
     * @mbg.generated
     */
    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        LitemallAllianceSale other = (LitemallAllianceSale) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getAllianceName() == null ? other.getAllianceName() == null : this.getAllianceName().equals(other.getAllianceName()))
            && (this.getAllianceUserid() == null ? other.getAllianceUserid() == null : this.getAllianceUserid().equals(other.getAllianceUserid()))
            && (this.getOrderSn() == null ? other.getOrderSn() == null : this.getOrderSn().equals(other.getOrderSn()))
            && (this.getOrderGoodsId() == null ? other.getOrderGoodsId() == null : this.getOrderGoodsId().equals(other.getOrderGoodsId()))
            && (this.getCategoryId() == null ? other.getCategoryId() == null : this.getCategoryId().equals(other.getCategoryId()))
            && (this.getGoodsName() == null ? other.getGoodsName() == null : this.getGoodsName().equals(other.getGoodsName()))
            && (this.getGoodsNumber() == null ? other.getGoodsNumber() == null : this.getGoodsNumber().equals(other.getGoodsNumber()))
            && (this.getGoodsPrice() == null ? other.getGoodsPrice() == null : this.getGoodsPrice().equals(other.getGoodsPrice()))
            && (this.getPlantCostPrice() == null ? other.getPlantCostPrice() == null : this.getPlantCostPrice().equals(other.getPlantCostPrice()))
            && (this.getCouponPrice() == null ? other.getCouponPrice() == null : this.getCouponPrice().equals(other.getCouponPrice()))
            && (this.getTotalProfit() == null ? other.getTotalProfit() == null : this.getTotalProfit().equals(other.getTotalProfit()))
            && (this.getGroupId() == null ? other.getGroupId() == null : this.getGroupId().equals(other.getGroupId()))
            && (this.getPlantId() == null ? other.getPlantId() == null : this.getPlantId().equals(other.getPlantId()))
            && (this.getAddTime() == null ? other.getAddTime() == null : this.getAddTime().equals(other.getAddTime()))
            && (this.getOrderCloseTime() == null ? other.getOrderCloseTime() == null : this.getOrderCloseTime().equals(other.getOrderCloseTime()))
            && (this.getOrderAddTime() == null ? other.getOrderAddTime() == null : this.getOrderAddTime().equals(other.getOrderAddTime()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_sale
     *
     * @mbg.generated
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getAllianceName() == null) ? 0 : getAllianceName().hashCode());
        result = prime * result + ((getAllianceUserid() == null) ? 0 : getAllianceUserid().hashCode());
        result = prime * result + ((getOrderSn() == null) ? 0 : getOrderSn().hashCode());
        result = prime * result + ((getOrderGoodsId() == null) ? 0 : getOrderGoodsId().hashCode());
        result = prime * result + ((getCategoryId() == null) ? 0 : getCategoryId().hashCode());
        result = prime * result + ((getGoodsName() == null) ? 0 : getGoodsName().hashCode());
        result = prime * result + ((getGoodsNumber() == null) ? 0 : getGoodsNumber().hashCode());
        result = prime * result + ((getGoodsPrice() == null) ? 0 : getGoodsPrice().hashCode());
        result = prime * result + ((getPlantCostPrice() == null) ? 0 : getPlantCostPrice().hashCode());
        result = prime * result + ((getCouponPrice() == null) ? 0 : getCouponPrice().hashCode());
        result = prime * result + ((getTotalProfit() == null) ? 0 : getTotalProfit().hashCode());
        result = prime * result + ((getGroupId() == null) ? 0 : getGroupId().hashCode());
        result = prime * result + ((getPlantId() == null) ? 0 : getPlantId().hashCode());
        result = prime * result + ((getAddTime() == null) ? 0 : getAddTime().hashCode());
        result = prime * result + ((getOrderCloseTime() == null) ? 0 : getOrderCloseTime().hashCode());
        result = prime * result + ((getOrderAddTime() == null) ? 0 : getOrderAddTime().hashCode());
        return result;
    }

    /**
     * This enum was generated by MyBatis Generator.
     * This enum corresponds to the database table litemall_alliance_sale
     *
     * @mbg.generated
     */
    public enum Column {
        id("id", "id", "INTEGER", false),
        allianceName("alliance_name", "allianceName", "VARCHAR", false),
        allianceUserid("alliance_userid", "allianceUserid", "INTEGER", false),
        orderSn("order_sn", "orderSn", "VARCHAR", false),
        orderGoodsId("order_goods_id", "orderGoodsId", "INTEGER", false),
        categoryId("category_id", "categoryId", "INTEGER", false),
        goodsName("goods_name", "goodsName", "VARCHAR", false),
        goodsNumber("goods_number", "goodsNumber", "INTEGER", false),
        goodsPrice("goods_price", "goodsPrice", "DECIMAL", false),
        plantCostPrice("plant_cost_price", "plantCostPrice", "DECIMAL", false),
        couponPrice("coupon_price", "couponPrice", "DECIMAL", false),
        totalProfit("total_profit", "totalProfit", "DECIMAL", false),
        groupId("group_id", "groupId", "INTEGER", false),
        plantId("plant_id", "plantId", "INTEGER", false),
        addTime("add_time", "addTime", "TIMESTAMP", false),
        orderCloseTime("order_close_time", "orderCloseTime", "TIMESTAMP", false),
        orderAddTime("order_add_time", "orderAddTime", "TIMESTAMP", false);

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table litemall_alliance_sale
         *
         * @mbg.generated
         */
        private static final String BEGINNING_DELIMITER = "`";

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table litemall_alliance_sale
         *
         * @mbg.generated
         */
        private static final String ENDING_DELIMITER = "`";

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table litemall_alliance_sale
         *
         * @mbg.generated
         */
        private final String column;

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table litemall_alliance_sale
         *
         * @mbg.generated
         */
        private final boolean isColumnNameDelimited;

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table litemall_alliance_sale
         *
         * @mbg.generated
         */
        private final String javaProperty;

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table litemall_alliance_sale
         *
         * @mbg.generated
         */
        private final String jdbcType;

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_alliance_sale
         *
         * @mbg.generated
         */
        public String value() {
            return this.column;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_alliance_sale
         *
         * @mbg.generated
         */
        public String getValue() {
            return this.column;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_alliance_sale
         *
         * @mbg.generated
         */
        public String getJavaProperty() {
            return this.javaProperty;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_alliance_sale
         *
         * @mbg.generated
         */
        public String getJdbcType() {
            return this.jdbcType;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_alliance_sale
         *
         * @mbg.generated
         */
        Column(String column, String javaProperty, String jdbcType, boolean isColumnNameDelimited) {
            this.column = column;
            this.javaProperty = javaProperty;
            this.jdbcType = jdbcType;
            this.isColumnNameDelimited = isColumnNameDelimited;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_alliance_sale
         *
         * @mbg.generated
         */
        public String desc() {
            return this.getEscapedColumnName() + " DESC";
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_alliance_sale
         *
         * @mbg.generated
         */
        public String asc() {
            return this.getEscapedColumnName() + " ASC";
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_alliance_sale
         *
         * @mbg.generated
         */
        public static Column[] excludes(Column ... excludes) {
            ArrayList<Column> columns = new ArrayList<>(Arrays.asList(Column.values()));
            if (excludes != null && excludes.length > 0) {
                columns.removeAll(new ArrayList<>(Arrays.asList(excludes)));
            }
            return columns.toArray(new Column[]{});
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_alliance_sale
         *
         * @mbg.generated
         */
        public String getEscapedColumnName() {
            if (this.isColumnNameDelimited) {
                return new StringBuilder().append(BEGINNING_DELIMITER).append(this.column).append(ENDING_DELIMITER).toString();
            } else {
                return this.column;
            }
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_alliance_sale
         *
         * @mbg.generated
         */
        public String getAliasedEscapedColumnName() {
            return this.getEscapedColumnName();
        }
    }
}