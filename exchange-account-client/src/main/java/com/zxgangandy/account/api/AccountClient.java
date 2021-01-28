package com.zxgangandy.account.api;

import com.zxgangandy.account.api.fallback.AccountClientFallback;
import com.zxgangandy.account.model.dto.FrozenAccountReqDTO;
import com.zxgangandy.account.model.dto.FrozenAccountRespDTO;
import com.zxgangandy.account.model.dto.UnfrozenReqDTO;
import com.zxgangandy.account.model.dto.UnfrozenRespDTO;
import io.jingwei.base.utils.model.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "exchange-account", fallback = AccountClientFallback.class)
public interface AccountClient {

    @PostMapping("/v1/account/frozen")
    R<FrozenAccountRespDTO> frozenAccount(FrozenAccountReqDTO req);

    @PostMapping("/v1/account/unfrozen")
    R<UnfrozenRespDTO> unfrozenAccount(UnfrozenReqDTO req);
}
