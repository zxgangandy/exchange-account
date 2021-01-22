package com.zxgangandy.account.biz.exception;


import io.jingwei.base.utils.exception.IBizErrCode;

public enum AccountErrCode implements IBizErrCode {
    ACCOUNT_NOT_FOUND("12500", "account-not-found"),
    FROZEN_ACCOUNT_FAILED("12501", "frozen-account-failed"),
    UNFROZEN_ACCOUNT_FAILED("12502", "unfrozen-account-failed"),
    BALANCE_NOT_ENOUGH("12503", "balance-not-enough"),
    FROZEN_RECORD_NOT_FOUND("12504", "frozen-record-not-found"),
    UPDATE_ORDER_FROZEN_FAILED("12505", "update-order-frozen-failed"),
    SYMBOL_BIGGER_THAN_MAX_VOLUME("12506", "symbol-bigger-than-max-volume"),
    LIMIT_ORDER_PRICE_INVALID("12507", "limit-order-price-invalid"),
    LIMIT_STOP_ORDER_PRICE_INVALID("12508", "limit-stop-order-price-invalid"),
    TRIGGER_PRICE_INVALID("12509", "trigger-price-invalid"),
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
