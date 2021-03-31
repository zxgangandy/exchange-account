package com.zxgangandy.account.biz.exception;


import io.jingwei.base.utils.exception.IBizErrCode;

public enum AccountErrCode implements IBizErrCode {
    ACCOUNT_NOT_FOUND("12500", "account-not-found"),
    BALANCE_NOT_ENOUGH("12501", "balance-not-enough"),
    ORDER_DUPLICATE_FROZEN("12502", "order-duplicate-frozen"),
    FROZEN_RECORD_NOT_FOUND("12503", "frozen-record-not-found"),
    UNFROZEN_AMOUNT_INVALID("12504", "unfrozen-amount-invalid"),
    ORDER_DUPLICATE_UNFROZEN("12505", "order-duplicate-unfrozen"),
    ORDER_DUPLICATE_DEPOSIT("12506", "order-duplicate-deposit"),
    ORDER_DUPLICATE_WITHDRAW("12507", "order-duplicate-withdraw"),
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
