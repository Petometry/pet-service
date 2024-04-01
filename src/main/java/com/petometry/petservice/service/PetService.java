package com.petometry.petservice.service;

import com.petometry.petservice.rest.model.PetShopDto;
import com.petometry.petservice.rest.model.PetDetailsDto;
import com.petometry.petservice.rest.model.PetOverviewDto;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface PetService {
    List<PetOverviewDto> getPets(String userId);

    PetDetailsDto buyPet(Jwt jwt, String userId, Long petId);

    void deletePet(String userId, Long petId);

    PetShopDto getPetShop(String userId);
}
