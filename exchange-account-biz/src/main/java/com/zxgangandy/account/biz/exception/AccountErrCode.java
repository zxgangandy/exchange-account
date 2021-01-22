package com.zxgangandy.account.biz.exception;


import io.jingwei.base.utils.exception.IBizErrCode;

public enum AccountErrCode implements IBizErrCode {
    ACCOUNT_NOT_FOUND("12500", "account-not-found"),
    FROZEN_ACCOUNT_FAILED("12501", "frozen-account-failed"),
    UNFROZEN_ACCOUNT_FAILED("12502", "unfrozen-account-failed"),
    BALANCE_NOT_ENOUGH("12503", "balance-not-enough"),
    FROZEN_RECORD_NOT_FOUND("12504", "frozen-record-not-found"),
    UPDATE_ORDER_FROZEN_FAILED("12505", "update-order-frozen-failed"),
    UNFROZEN_AMOUNT_INVALID("12506", "unfrozen-amount-invalid"),
    SAVE_FROZEN_LOG_FAILED("12507", "save-frozen-log-failed"),
    SAVE_UNFROZEN_LOG_FAILED("12508", "save-frozen-log-failed"),
    SAVE_ORDER_FROZEN_FAILED("12509", "save-order-frozen-failed"),
    ORDER_VOLUME_INVALID("12510", "order-volume-invalid"),
    ORDER_TYPE_NOT_SUPPORT("12511", "order-type-not-support"),
    PLACE_ORDER_FAILED("12512", "place-order-failed")
    ;

    /**
     * 枚举编码
     */
    private String code;

    /**
     * 描述说明
     */
    private String desc;

    AccountErrCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String getMsg() {
        return getClass().getName() + '.' + name();
    }
}
