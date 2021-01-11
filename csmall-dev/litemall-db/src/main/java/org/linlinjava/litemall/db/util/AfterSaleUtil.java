package org.linlinjava.litemall.db.util;

import org.linlinjava.litemall.db.domain.LitemallOrder;

import java.util.ArrayList;
import java.util.List;

/*
 * 售后流程：申请售后－》审核－》退款－》退款成功
 * 售后状态：
 * 0.未申请
 * 1.退货审核通过
 * 2.退货审核驳回
 * 3.退款审核通过 （退款中）
 * 4.退款驳回
 * 5.退款成功
 * 6.退款失败
 * 7.用户取消
 * 11.待收货申请售后（待商家同意退货中）
 * 12.确认收货申请售后（待商家同意退货中）
 */
public class AfterSaleUtil {

    /*
     *0.未申请
     */
    public static final int STATUS_NOT_APPLIED = 0;
    /*
     *允许退货
     */
    public static final int STATUS_RETURN_ALLOWED = 1;
    /*
     *拒绝退货
     */
    public static final int STATUS_REJECT_RETURN = 2;
    /*
     *允许退款（退款中）
     */
    public static final int STATUS_REFUND_ALLOWED = 3;
    /*
     *拒绝退款
     */
    public static final int STATUS_REFUSE_REFUND = 4;
    /*
     *退款成功
     */
    public static final int STATUS_REFUND_SUCCESSFUL = 5;
    /*
     *退款失败
     */
    public static final int STATUS_REFUND_FAILED = 6;
    /*
     *用户取消售后
     */
    public static final int STATUS_CANCEL = 7;
    /*
     *未确认收货申请
     */
    public static final int STATUS_GOODS_NOT_RECEIVED = 11;
    /*
     *已确认收货申请
     */
    public static final int STATUS_GOODS_RECEIVED = 12;


}
