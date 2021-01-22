package com.zxgangandy.account.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class FrozenAccountReqDTO {
    /**
     * 订单号
     */
    @NotNull(message = "订单号不能为空")
    private Long orderId;
    /**
     * 用户号
     */
    @NotNull(message = "用户号不能为空")
    private Long userId;
    /**
     * 币种
     */
    @NotEmpty(message = "币种不能为空")
    private String currency;
    /**
     * 业务类型
     */
    @NotEmpty(message = "业务类型不能为空")
    private String bizType;
    /**
     * 冻结数量（金额）
     */
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;
    /**
     * 冻结备注
     */
    @NotEmpty(message = "备注不能为空")
    private String remark;

}
