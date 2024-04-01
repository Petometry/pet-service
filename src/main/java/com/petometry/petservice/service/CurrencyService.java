package com.petometry.petservice.service;

import com.petometry.petservice.service.model.currency.CurrencyBalance;
import com.petometry.petservice.service.model.currency.CurrencyBalances;
import org.springframework.security.oauth2.jwt.Jwt;

public interface CurrencyService {

    CurrencyBalances getBalances(Jwt jwt, String userid);

    CurrencyBalance payServer(Jwt jwt, String userId, Long value);
}
