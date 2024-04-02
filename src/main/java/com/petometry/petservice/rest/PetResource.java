package com.petometry.petservice.rest;

import com.petometry.petservice.rest.model.PetDetailsDto;
import com.frameboter.rest.AbstractResource;
import com.petometry.petservice.rest.model.PetOverviewDto;
import com.petometry.petservice.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/pets")
public class PetResource extends AbstractResource {

    private final PetService petService;

    // @formatter:off
    @Operation(summary = "Retrieves pets", description = "Retrieves all the pets associated with the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "pets found successfully"),
            @ApiResponse(responseCode = "401", description = "User is not logged in via Keycloak", content = @Content)
    })
    @GetMapping()
    public List<PetOverviewDto> getPets(@AuthenticationPrincipal Jwt jwt) {
        // @formatter:on
        String userId = getUserId(jwt);
        log.info("getPets started for userId=" + userId);
        List<PetOverviewDto> pets = petService.getPets(userId);
        log.info("getPets finished for userId={} pets={}", getUserId(jwt), pets);
        return pets;
    }

    // @formatter:off
    @Operation(summary = "Buys a pet", description = "Puts a pet of the users petshop into their petstore. Max number of pets = 5")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "pet bought successfully"), @ApiResponse(responseCode = "401", description = "User is not logged in via Keycloak", content = @Content), @ApiResponse(responseCode = "403", description = "User has too many pets already", content = @Content)})
    @PostMapping("/{petId}")
    public PetDetailsDto buyPet(@AuthenticationPrincipal Jwt jwt, @PathVariable Long petId) {
        // @formatter:on
        String userId = getUserId(jwt);
        log.info("buyPet started for userId={} petId={}", userId, petId);
        PetDetailsDto pet = petService.buyPet(jwt, userId, petId);
        log.info("buyPet finished for userId={} pet={}", userId, pet);
        return pet;
    }

    // @formatter:off
    @Operation(summary = "Deletes a pet", description = "Deletes the pet with the given id but only if the curren user owns it")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "pet deleted successfully"), @ApiResponse(responseCode = "401", description = "User is not logged in via Keycloak", content = @Content), @ApiResponse(responseCode = "404", description = "Pet not found", content = @Content)})
    @DeleteMapping("/{petId}")
    public void deletePet(@AuthenticationPrincipal Jwt jwt, @PathVariable Long petId) {
        // @formatter:on
        String userId = getUserId(jwt);
        log.info("deletePet started for userId={} petId={}", userId, petId);
        petService.deletePet(userId, petId);
        log.info("deletePet finished for userId=" + getUserId(jwt));
    }
}
