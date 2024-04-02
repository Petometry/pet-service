package com.petometry.petservice.rest;

import com.frameboter.rest.AbstractResource;
import com.petometry.petservice.rest.model.PetShopDto;
import com.petometry.petservice.service.PetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/petshops")
public class PetShopResource extends AbstractResource {

    private final PetService petService;

    // @formatter:off
    @GetMapping()
    public PetShopDto getPetShop(@AuthenticationPrincipal Jwt jwt){
        // @formatter:on
        String userId = getUserId(jwt);
        log.info("getPetShop started for userId={}", userId);
        PetShopDto petShop = petService.getPetShop(userId);
        log.info("getPetShop finished for userId={} petShop={}", userId, petShop);
        return petShop;
    }
}
