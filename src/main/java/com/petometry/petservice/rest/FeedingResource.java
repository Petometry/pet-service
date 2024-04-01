package com.petometry.petservice.rest;

import com.frameboter.rest.AbstractResource;
import com.petometry.petservice.rest.model.PetFeeding;
import com.petometry.petservice.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/feedings")
public class FeedingResource extends AbstractResource {

    private final PetService petService;


    // @formatter:off
    @Operation(summary = "Feeds a pet", description = "Feeds a pet for the given amount")
    @ApiResponses(value =
            {@ApiResponse(responseCode = "200", description = "pet bought successfully"),
                    @ApiResponse(responseCode = "401", description = "User is not logged in via Keycloak", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Pet is already full", content = @Content)})
    @PostMapping()
    public PetFeeding createFeeding(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid PetFeeding petFeeding) {
        // @formatter:on
        String userId = getUserId(jwt);
        log.info("createFeeding started for userId={} petFeeding={}", userId, petFeeding);
        PetFeeding feeding = petService.feedPet(jwt,userId, petFeeding);
        log.info("createFeeding finished for userId={} feeding={}", userId, feeding);
        return feeding;
    }

}
