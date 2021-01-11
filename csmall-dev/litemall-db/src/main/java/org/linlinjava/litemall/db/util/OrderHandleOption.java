package org.linlinjava.litemall.db.util;

public class OrderHandleOption {
    private boolean cancel = false;      // 取消操作
    private boolean pay = false;         // 支付操作
    private boolean confirm = false;    // 确认收货操作
    private boolean rebuy = false;        // 再次购买
    private boolean logistics = false;        // 查物流

    public boolean isLogistics() {
        return logistics;
    }
    public void setLogistics(boolean logistics) {
        this.logistics = logistics;
    }
    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isPay() {
        return pay;
    }

    public void setPay(boolean pay) {
        this.pay = pay;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public boolean isRebuy() {
        return rebuy;
    }

    public void setRebuy(boolean rebuy) {
        this.rebuy = rebuy;
    }


}
