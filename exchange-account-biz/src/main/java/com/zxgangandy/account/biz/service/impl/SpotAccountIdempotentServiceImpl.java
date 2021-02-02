package com.zxgangandy.account.biz.service.impl;

import com.zxgangandy.account.biz.entity.SpotAccountIdempotent;
import com.zxgangandy.account.biz.mapper.SpotAccountIdempotentMapper;
import com.zxgangandy.account.biz.service.ISpotAccountIdempotentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jingwei.base.utils.exception.SysErr;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * <p>
 * 现货账号幂等表 服务实现类
 * </p>
 *
 * @author Andy
 * @since 2021-01-29
 */
@Service
@Slf4j
public class SpotAccountIdempotentServiceImpl extends ServiceImpl<SpotAccountIdempotentMapper, SpotAccountIdempotent> implements ISpotAccountIdempotentService {

    @Override
    public boolean checkIdempotent(String bizId) {
        Optional<SpotAccountIdempotent> opt = lambdaQuery()
                .eq(SpotAccountIdempotent::getBizId, bizId)
                .oneOpt();
        boolean idempotent = opt.isPresent();

        if (!idempotent) {
            try {
                if (!save(new SpotAccountIdempotent().setBizId(bizId))) {
                    log.error("save idempotent bizId={} failed", bizId);
                    throw new SysErr();
                }
            } catch (DuplicateKeyException ex) {
                return true;
            }
        }

        return idempotent;
    }
}
