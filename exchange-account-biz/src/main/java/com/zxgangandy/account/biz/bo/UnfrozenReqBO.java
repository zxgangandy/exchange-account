package com.zxgangandy.account.biz.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class UnfrozenReqBO {
    /**
     * 原冻结订单号
     */
    private Long orderId;

    /**
     * 用户号
     */
    private Long userId;

    /**
     * 解冻金额
     */
    private BigDecimal unfrozenAmount;

    /**
     * 币种
     */
    private String currency;

    /**
     * 解冻业务ID
     */
    private Long bizId;

    /**
     * 业务类型
     */
    private String bizType;

}
