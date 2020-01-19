package com.zxgangandy.account.base.exception;

import com.zxgangandy.base.utils.exception.IBizErrCode;

public enum AccountErrCode implements IBizErrCode {
    ACCOUNT_NOT_FOUND("12000", "account-not-found"),
    FROZEN_ACCOUNT_FAILED("12001", "frozen-account-failed"),
    BIZ_TYPE_NOT_FOUND("12002", "biz-type-not-found"),
    UNFROZEN_ACCOUNT_FAILED("12001", "unfrozen-account-failed"),
    LEVEL_FEE_RATE_NOT_FOUND("12003", "level-fee-rate-not-found"),
    SYMBOL_LOWER_THAN_MIN_VOLUME("12004", "symbol-lower-than-min-volume"),
    SYMBOL_BIGGER_THAN_MAX_VOLUME("12005", "symbol-bigger-than-max-volume"),
    LIMIT_ORDER_PRICE_INVALID("12006", "limit-order-price-invalid"),
    LIMIT_STOP_ORDER_PRICE_INVALID("12007", "limit-stop-order-price-invalid"),
    TRIGGER_PRICE_INVALID("12008", "trigger-price-invalid"),
    ORDER_VOLUME_INVALID("12009", "order-volume-invalid"),
    ORDER_TYPE_NOT_SUPPORT("12010", "order-type-not-support"),
    PLACE_ORDER_FAILED("12015", "place-order-failed")
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

    public String getMSg() {
        return getClass().getName() + '.' + name();
    }
}
