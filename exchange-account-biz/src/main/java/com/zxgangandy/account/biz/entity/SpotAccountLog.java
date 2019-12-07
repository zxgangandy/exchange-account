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
 * 现货账户流水日志
 * </p>
 *
 * @author Andy
 * @since 2019-11-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SpotAccountLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 资金转出用户号
     */
    private Long fromUserId;

    /**
     * 资金转入用户号
     */
    private Long toUserId;

    /**
     * 资金转出账务号
     */
    private Long fromAccountId;

    /**
     * 资金转入账户号
     */
    private Long toAccountId;

    /**
     * 数量/金额
     */
    private BigDecimal amount;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updateAt;


}
