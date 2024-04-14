package com.petometry.petservice.service;

import com.petometry.petservice.service.model.currency.CurrencyBalance;
import com.petometry.petservice.service.model.currency.CurrencyGeocoinBalance;
import com.petometry.petservice.service.model.currency.CurrencyPetFoodBalances;
import org.springframework.security.oauth2.jwt.Jwt;

public interface CurrencyService {

    CurrencyGeocoinBalance getGeoCoinBalance(Jwt jwt, String userid);

    CurrencyBalance payServer(Jwt jwt, String userId, Double value);

    CurrencyPetFoodBalances getPetfoodBalances(Jwt jwt);

    CurrencyPetFoodBalances updatePetFoodBalances(Jwt jwt, CurrencyPetFoodBalances currencyPetFoodBalances);

}
