package com.zxgangandy.account.biz.support;

import com.zxgangandy.account.biz.bo.FrozenReqBO;
import com.zxgangandy.account.biz.bo.UnfrozenReqBO;
import com.zxgangandy.account.biz.entity.SpotAccount;
import com.zxgangandy.account.biz.entity.SpotAccountLog;

public class AccountSupport {
    public static SpotAccountLog createFrozenLog(SpotAccount account, FrozenReqBO reqBO) {
        return new SpotAccountLog()
                .setFromAccountId(account.getAccountId())
                .setToAccountId(account.getAccountId())
                .setFromUserId(reqBO.getUserId())
                .setToUserId(reqBO.getUserId())
                .setAmount(reqBO.getAmount())
                .setBizType(reqBO.getBizType())
                .setOrderId(reqBO.getOrderId())
                .setRemark(reqBO.getRemark());
    }

    public static SpotAccountLog createUnfrozenLog(SpotAccount account, SpotAccountLog accountLog, UnfrozenReqBO reqBO) {
        return new SpotAccountLog()
                .setFromAccountId(account.getAccountId())
                .setToAccountId(account.getAccountId())
                .setFromUserId(reqBO.getUserId())
                .setToUserId(reqBO.getUserId())
                .setAmount(accountLog.getAmount())
                .setBizType(reqBO.getBizType())
                .setOrderId(accountLog.getOrderId())
                .setRemark(reqBO.getRemark());
    }

}
