package com.petometry.petservice.service;

import com.petometry.petservice.service.model.currency.CurrencyBalance;
import com.petometry.petservice.service.model.currency.CurrencyGeocoinsBalances;
import org.springframework.security.oauth2.jwt.Jwt;

public interface CurrencyService {

    CurrencyGeocoinsBalances getBalances(Jwt jwt, String userid);

    CurrencyBalance payServer(Jwt jwt, String userId, Double value);

    CurrencyBalance getPayedByServer(Jwt jwt, String userId, double value);


}
