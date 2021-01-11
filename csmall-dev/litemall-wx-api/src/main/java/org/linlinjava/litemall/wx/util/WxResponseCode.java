package org.linlinjava.litemall.wx.util;

public class WxResponseCode {
    public static final Integer AUTH_INVALID_ACCOUNT = 700;
    public static final Integer AUTH_CAPTCHA_UNSUPPORT = 701;
    public static final Integer AUTH_CAPTCHA_FREQUENCY = 702;
    public static final Integer AUTH_CAPTCHA_UNMATCH = 703;
    public static final Integer AUTH_NAME_REGISTERED = 704;
    public static final Integer AUTH_MOBILE_REGISTERED = 705;
    public static final Integer AUTH_MOBILE_UNREGISTERED = 706;
    public static final Integer AUTH_INVALID_MOBILE = 707;
    public static final Integer AUTH_OPENID_UNACCESS = 708;
    public static final Integer AUTH_OPENID_BINDED = 709;

    public static final Integer GOODS_UNSHELVE = 710;
    public static final Integer GOODS_NO_STOCK = 711;
    public static final Integer GOODS_UNKNOWN = 712;
    public static final Integer GOODS_INVALID = 713;

    public static final Integer ORDER_UNKNOWN = 720;
    public static final Integer ORDER_INVALID = 721;
    public static final Integer ORDER_CHECKOUT_FAIL = 722;
    public static final Integer ORDER_CANCEL_FAIL = 723;
    public static final Integer ORDER_PAY_FAIL = 724;
    // 订单当前状态下不支持用户的操作，例如商品未发货状态用户执行确认收货是不可能的。
    public static final Integer ORDER_INVALID_OPERATION = 725;
    public static final Integer ORDER_COMMENTED = 726;
    public static final Integer ORDER_COMMENT_EXPIRED = 727;

    public static final Integer GROUPON_EXPIRED = 730;
    public static final Integer GROUPON_OFFLINE = 731;
    public static final Integer GROUPON_FULL = 732;
    public static final Integer GROUPON_JOIN = 733;

    public static final int COUPON_EXCEED_LIMIT = 740;
    public static final int COUPON_RECEIVE_FAIL= 741;
    public static final int COUPON_CODE_INVALID= 742;

    public static final int AFTERSALE_UNALLOWED = 750;
    public static final int AFTERSALE_INVALID_AMOUNT = 751;
    public static final int AFTERSALE_INVALID_STATUS = 752;

    public static final Integer OPERATION_TIMEOUT = 500;

    /**
     * 不支持该银行卡
     */
    public static final int BANK_CARD_NUMBER_INVALID = 801;

    /**
     * 该银行卡号已存在
     */
    public static final int BANK_CARD_NUMBER_ALREADY_EXISTS = 802;

    /**
     * 银行卡前后两次不一致
     */
    public static final int BANK_CARD_NUMBER_DISSIMILARITY = 804;

    /**
     * BINDING_IDENTITY_INFORMATION 用户身份信息为空，为绑定
     */
    public static final int BINDING_IDENTITY_INFORMATION = 803;

    /**
     * 绑卡已认证，请不要重复认证
     */
    public static final int BANK_CARD_REPEAT_AUTHENTICATION = 805;

    /**
     * 请先获取验证码
     */
    public static final int GET_AUTHENTICATION_CODE = 806;

    /**
     * 验签失败
     */
    public static final int FAILURE_VERIFY_SIGNATURE = 807;

    /**
     * 用户身份信息未认证
     */
    public static final int USER_IDENTITY_INFO_AUTHENTICATED = 808;

    /**
     * 银行卡卡号长度不够或者超出
     */
    public static final int BANK_CARD_LENGTH_MISTAKE = 809;
    /**
     * 支付中，请等待支付结果，不要重复支付
     */
    public static final int BANK_NOT_DUPLICATE_PAYMENT = 810;
    /**
     * 支付失败
     */
    public static final int PAYMENT_FAIL = 811;


    /*
     * USER_NAME_EXIST 用户账号已存在
     */
    public static final Integer USER_NAME_EXIST = 901;
    /*
     * USER_NAME_NO_EXIST 用户账号不存在
     */
    public static final Integer USER_NAME_NO_EXIST = 902;

    /*
     * USER_INVALID_PASSWORD 用户无效密码-903
     */
    public static final Integer USER_INVALID_PASSWORD = 903;

    /**
     * 提现金额不能大于当前账户余额
     */
    public static final int CASH_AMOUNT_CANNOT_EXCESS_BALANCE = 904;

    /**
     * 该业务功能不支持
     */
    public static final int NO_RIGHT_TO_OPERATE_THIS_FUNCTION = 905;

    /**
     ** 用户账号被禁用-906
     */
    public static final Integer USER_NAME_PROHIBITION_OF_USE = 906;
    /**
     * 提现金额小于提现最低金额
     */
    public static final int CASH_WITHDRAWAL_IS_NO_LESS_THAN = 907;

    /**
     * 字符长度超出限制
     */
    public static final int CHARACTER_LENGTH_EXCEEDING_LIMIT = 920;



    /**
     * 验证码请求太频繁-10401
     */
    public static final int CODE_REQUESTS_ARE_FREQUENT= 10401;

    /**
     * 参数校验失败
     */
    public static final int CUSTOMIZE_FAIL_CODE = 10500;

    /**
     * 支付密码校验失败-10601
     */
    public static final int AUTH_INVALID_PAYPD= 10601;

    /**
     * 与旧密码一致-10602
     */
    public static final int SAME_AS_THE_OLD_PASSWORD= 10602;

    /**
     * 升级版本
     */
    public static final int APP_VERSION_TO_UPDATE= 10603;

    /**
     * 版本已是最新的
     */
    public static final int APP_VERSION_IS_THE_LATEST= 10604;

}
