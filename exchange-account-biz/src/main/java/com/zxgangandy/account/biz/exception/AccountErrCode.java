package com.zxgangandy.account.biz.exception;


import io.jingwei.base.utils.exception.IBizErrCode;

public enum AccountErrCode implements IBizErrCode {
    ACCOUNT_NOT_FOUND("12500", "account-not-found"),
    BALANCE_NOT_ENOUGH("12501", "balance-not-enough"),
    FROZEN_RECORD_NOT_FOUND("12502", "frozen-record-not-found"),
    UNFROZEN_AMOUNT_INVALID("12503", "unfrozen-amount-invalid")
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
