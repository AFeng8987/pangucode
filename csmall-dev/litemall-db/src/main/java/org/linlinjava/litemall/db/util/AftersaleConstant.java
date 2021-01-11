package org.linlinjava.litemall.db.util;

public class AftersaleConstant {
    public static final int STATUS_INIT = 0;
    //后台允许退货
    public static final int STATUS_REQUEST = 1;
    //后台拒绝退货
    public static final int STATUS_REFUSE = 2;
    //同意退款，退款中
    public static final int STATUS_REFUND = 3;
    //拒绝退款
    public static final int STATUS_REJECT = 4;
    //退款成功
    public static final int STATUS_SUCCESS = 5;
    //取消退款
    public static final int STATUS_CANCEL = 6;

    //待收货申请
    public static final int STATUS_WAIT_RECEIVE= 11;

    //已收货申请
    public static final int STATUS_RECEIVE= 12;


    public static final Integer TYPE_GOODS_MISS = 0;
    public static final Integer TYPE_GOODS_NEEDLESS = 1;
    public static final Integer TYPE_GOODS_REQUIRED = 2;
}
