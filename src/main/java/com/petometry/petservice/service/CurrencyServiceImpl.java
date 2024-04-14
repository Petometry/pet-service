package com.petometry.petservice.service;

import com.frameboter.service.HttpService;
import com.petometry.petservice.service.model.currency.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final HttpService httpService;

    @Value("${backend.domain.base}")
    private String baseDomain;

    @Value("${backend.domain.prefix}")
    private String urlPrefix;

    private static final String SERVICE_NAME = "currency.";

    @Override
    public CurrencyGeocoinBalance getGeoCoinBalance(Jwt jwt, String userid) {

        String url = urlPrefix + SERVICE_NAME + baseDomain + "/geocoins";
        return httpService.sendGetRequest(url, jwt, CurrencyGeocoinBalance.class);
    }

    @Override
    public CurrencyBalance payServer(Jwt jwt, String userId, Double value) {

        CurrencyTransaction currencyTransaction = new CurrencyTransaction();
        currencyTransaction.setCurrency(CurrencyType.GEOCOIN);
        currencyTransaction.setTarget("SERVER");
        currencyTransaction.setValue(value);
        currencyTransaction.setSource(userId);

        String url = urlPrefix + SERVICE_NAME + baseDomain + "/transactions";
        return httpService.sendPostRequest(url, jwt, currencyTransaction, CurrencyBalance.class);
    }

    @Override
    public CurrencyPetFoodBalances getPetfoodBalances(Jwt jwt) {

        String url = urlPrefix + SERVICE_NAME + baseDomain + "/petfoods";
        return httpService.sendGetRequest(url, jwt, CurrencyPetFoodBalances.class);
    }

    @Override
    public CurrencyPetFoodBalances updatePetFoodBalances(Jwt jwt, CurrencyPetFoodBalances currencyPetFoodBalances) {

        String url = urlPrefix + SERVICE_NAME + baseDomain + "/petfoods";
        return httpService.sendPutRequest(url, jwt, currencyPetFoodBalances, CurrencyPetFoodBalances.class);
    }


}
