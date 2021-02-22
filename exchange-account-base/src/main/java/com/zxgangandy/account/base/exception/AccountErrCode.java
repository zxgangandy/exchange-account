package com.zxgangandy.account.base.exception;


import io.jingwei.base.utils.exception.IBizErrCode;

public enum AccountErrCode implements IBizErrCode {
    ACCOUNT_NOT_FOUND("12500", "account-not-found"),
    DUPLICATE_FROZEN_ACCOUNT("12501", "duplicate-frozen-account"),
    DUPLICATE_UNFROZEN_ACCOUNT("12502", "duplicate-unfrozen-account"),
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
