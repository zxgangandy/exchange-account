package com.zxgangandy.account.biz.support;

import com.zxgangandy.account.biz.bo.FrozenReqBO;
import com.zxgangandy.account.biz.bo.UnfrozenReqBO;
import com.zxgangandy.account.biz.entity.SpotAccount;
import com.zxgangandy.account.biz.entity.SpotAccountFrozen;
import com.zxgangandy.account.biz.entity.SpotAccountLog;

public class AccountSupport {

    public static SpotAccountFrozen createOrderFrozen(SpotAccount account, FrozenReqBO reqBO) {
        return new SpotAccountFrozen()
                .setUserId(reqBO.getUserId())
                .setOrderId(reqBO.getOrderId())
                .setBizType(reqBO.getBizType())
                .setCurrency(reqBO.getCurrency())
                .setOriginFrozen(reqBO.getAmount())
                .setLeftFrozen(reqBO.getAmount())
                .setAccountId(account.getAccountId());
    }
    public static SpotAccountLog createFrozenLog(SpotAccount account, FrozenReqBO reqBO) {
        return new SpotAccountLog()
                .setFromAccountId(account.getAccountId())
                .setToAccountId(account.getAccountId())
                .setFromUserId(reqBO.getUserId())
                .setToUserId(reqBO.getUserId())
                .setAmount(reqBO.getAmount())
                .setBizType(reqBO.getBizType())
                .setOrderId(reqBO.getOrderId())
                .setCurrency(reqBO.getCurrency());
    }

    public static SpotAccountLog createUnfrozenLog(SpotAccount account, UnfrozenReqBO reqBO) {
        return new SpotAccountLog()
                .setFromAccountId(account.getAccountId())
                .setToAccountId(account.getAccountId())
                .setFromUserId(reqBO.getUserId())
                .setToUserId(reqBO.getUserId())
                .setCurrency(reqBO.getCurrency())
                .setAmount(reqBO.getUnfrozenAmount())
                .setBizType(reqBO.getBizType())
                .setOrderId(reqBO.getOrderId());
    }

}
