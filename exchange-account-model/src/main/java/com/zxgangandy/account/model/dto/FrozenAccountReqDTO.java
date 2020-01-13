package com.zxgangandy.account.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class FrozenAccountReqDTO {
    /**
     * 订单号
     */
    @NotNull
    private Long orderId;
    /**
     * 用户号
     */
    @NotNull
    private Long userId;
    /**
     * 币种
     */
    @NotNull
    private String currency;
    /**
     * 业务类型
     */
    @NotNull
    private String bizType;
    /**
     * 冻结数量（金额）
     */
    @NotNull
    private BigDecimal amount;
    /**
     * 冻结备注
     */
    @NotNull
    private String remark;

}
