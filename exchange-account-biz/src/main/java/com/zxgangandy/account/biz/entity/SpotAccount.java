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
 * @since 2021-01-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SpotAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 币种账户id
     */
    private Long accountId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 币种
     */
    private String currency;

    /**
     * 余额持仓量
     */
    private BigDecimal balance;

    /**
     * 冻结持仓量
     */
    private BigDecimal frozen;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
