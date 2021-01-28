package com.zxgangandy.account.api.fallback;

import com.zxgangandy.account.api.AccountClient;
import com.zxgangandy.account.model.dto.FrozenAccountReqDTO;
import com.zxgangandy.account.model.dto.FrozenAccountRespDTO;
import com.zxgangandy.account.model.dto.UnfrozenReqDTO;
import com.zxgangandy.account.model.dto.UnfrozenRespDTO;
import io.jingwei.base.utils.model.R;
import org.springframework.stereotype.Service;


@Service
public class AccountClientFallback implements AccountClient {

    @Override
    public R<FrozenAccountRespDTO> frozenAccount(FrozenAccountReqDTO req) {
        return R.failed("circuit breaker");
    }

    @Override
    public R<UnfrozenRespDTO> unfrozenAccount(UnfrozenReqDTO req) {
        return R.failed("circuit breaker");
    }
}
