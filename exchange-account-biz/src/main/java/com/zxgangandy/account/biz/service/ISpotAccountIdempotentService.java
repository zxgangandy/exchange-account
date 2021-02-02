package com.zxgangandy.account.biz.service;

import com.zxgangandy.account.biz.entity.SpotAccountIdempotent;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 现货账号幂等表 服务类
 * </p>
 *
 * @author Andy
 * @since 2021-01-29
 */
public interface ISpotAccountIdempotentService extends IService<SpotAccountIdempotent> {

    boolean checkIdempotent(String bizId);

}
