package com.zxgangandy.account.api.in;

import com.zxgangandy.account.api.in.fallback.AccountClientFallback;
import com.zxgangandy.account.model.dto.FrozenAccountReqDTO;
import com.zxgangandy.account.model.dto.FrozenAccountRespDTO;
import com.zxgangandy.account.model.dto.UnfrozenReqDTO;
import com.zxgangandy.account.model.dto.UnfrozenRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "exchange-account", fallback = AccountClientFallback.class)
public interface AccountClient {

    @PostMapping("/v1/account/frozen")
    ResponseEntity<FrozenAccountRespDTO> frozenAccount(FrozenAccountReqDTO req);

    @PostMapping("/v1/account/unfrozen")
    ResponseEntity<UnfrozenRespDTO> unfrozenAccount(UnfrozenReqDTO req);
}
