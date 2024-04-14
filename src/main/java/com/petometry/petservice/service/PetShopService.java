package com.petometry.petservice.service;

import com.petometry.petservice.rest.model.PetShopDto;


public interface PetShopService {

    PetShopDto getPetShop(String userId);
}
