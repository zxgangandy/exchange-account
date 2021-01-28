package com.zxgangandy.account.biz.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class FrozenReqBO {
    /**
     * 订单号
     */
    private Long orderId;
    /**
     * 用户号
     */
    private Long userId;
    /**
     * 币种
     */
    private String currency;
    /**
     * 业务类型
     */
    private String bizType;
    /**
     * 冻结数量（金额）
     */
    private BigDecimal amount;

}
