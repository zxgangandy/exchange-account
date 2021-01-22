package com.zxgangandy.account.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class UnfrozenReqDTO {
    /**
     * 订单号
     */
    private Long orderId;
    /**
     * 用户号
     */
    /**
     * 用户号
     */
    @NotNull(message = "用户号不能为空")
    private Long userId;

    /**
     * 解冻金额
     */
    /**
     * 用户号
     */
    @NotNull(message = "解冻金额不能为空")
    private BigDecimal unfrozenAmount;

    /**
     * 币种
     */
    /**
     * 用户号
     */
    @NotEmpty(message = "币种不能为空")
    private String currency;
    /**
     * 业务类型
     */
    /**
     * 用户号
     */
    @NotEmpty(message = "业务类型不能为空")
    private String bizType;

    /**
     * 冻结备注
     */
    /**
     * 用户号
     */
    @NotEmpty(message = "备注不能为空")
    private String remark;

}
