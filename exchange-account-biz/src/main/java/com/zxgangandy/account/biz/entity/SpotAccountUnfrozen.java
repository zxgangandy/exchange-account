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
 * 现货账户
 * </p>
 *
 * @author Andy
 * @since 2021-01-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SpotAccountUnfrozen implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 业务唯一id
     */
    private Long bizId;

    /**
     * 冻结业务类型
     */
    private String bizType;

    /**
     * 用户号
     */
    private Long userId;

    /**
     * 冻结源订单号
     */
    private Long orderId;

    /**
     * 币种账号号
     */
    private Long accountId;

    /**
     * 币种
     */
    private String currency;

    /**
     * 订单初始冻结持仓金额
     */
    private BigDecimal originFrozen;

    /**
     * 解冻后剩余定金金额
     */
    private BigDecimal leftFrozen;

    /**
     * 解冻金额
     */
    private BigDecimal unfrozen;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
