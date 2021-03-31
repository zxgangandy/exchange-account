package com.zxgangandy.account.biz.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 现货账户入金表
 * </p>
 *
 * @author Andy
 * @since 2021-03-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SpotAccountTrade implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户号
     */
    private Long userId;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * in:入金， out：出金
     */
    private String tradeType;

    /**
     * 币种账号
     */
    private Long accountId;

    /**
     * 币种
     */
    private String currency;

    /**
     * 交易前余额
     */
    private BigDecimal beforeBalance;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 交易后余额
     */
    private BigDecimal afterBalance;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
