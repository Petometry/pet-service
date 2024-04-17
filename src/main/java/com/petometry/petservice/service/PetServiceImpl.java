package com.petometry.petservice.service;

import com.petometry.petservice.repository.PetRepository;
import com.petometry.petservice.repository.model.Pet;
import com.petometry.petservice.rest.model.PetDetailsDto;
import com.petometry.petservice.rest.model.PetFeeding;
import com.petometry.petservice.rest.model.PetOverviewDto;
import com.petometry.petservice.service.model.currency.CurrencyGeocoinBalance;
import com.petometry.petservice.service.model.currency.CurrencyPetFoodBalances;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;

    private final ModelMapper modelMapper;

    private final CurrencyService currencyService;

    @Value("${max-health:100.0}")
    private double maxHealth;

    @Value("${max-hunger:100.0}")
    private double maxHunger;

    @Override
    public List<PetOverviewDto> getPets(String userId) {

        List<Pet> pets = petRepository.findByOwnerId(userId).stream().map(this::checkPetHunger).filter(Objects::nonNull).toList();
        return modelMapper.map(pets, new TypeToken<List<PetOverviewDto>>() {}.getType());
    }

    @Override
    public PetDetailsDto buyPet(Jwt jwt, String userId, Long petId) {

        // todo: switch 5 for dynamic pet slots
        if (petRepository.countByOwnerId(userId) >= 5) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(403));
        }

        final Optional<Pet> petOptional = petRepository.findByIdAndPetShop_OwnerIdAndPetShop_ValidFor(petId, userId, LocalDate.now());
        if (petOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        final Pet pet = petOptional.get();
//        Todo switch 10 with cost of pet once that is implemented
        CurrencyGeocoinBalance balances = currencyService.getGeoCoinBalance(jwt, userId);
        if (balances.getGeocoin() < 10) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(403));
        }
        pet.setOwnerId(userId);
        pet.setPetShop(null);
        pet.setLastSeen(LocalDateTime.now());
        Pet boughtPet = petRepository.save(pet);
//      Todo switch 10 with cost of pet once that is implemented
        currencyService.payServer(jwt, userId, 10.0);
        return modelMapper.map(boughtPet, PetDetailsDto.class);
    }

    @Override
    public void deletePet(String userId, Long petId) {

        Pet pet = getCheckedPet(userId, petId);
        petRepository.delete(pet);
    }

    @Override
    public PetOverviewDto getPet(String userId, Long petId) {
        Pet pet = getCheckedPet(userId, petId);
        return modelMapper.map(pet, PetDetailsDto.class);
    }

    @Override
    public PetFeeding feedPet(Jwt jwt, String userId, PetFeeding petFeeding) {
        Pet pet = getCheckedPet(userId, petFeeding.getPetId());
        if (pet.getHunger() == maxHunger){
            PetFeeding actualFeeding = SerializationUtils.clone(petFeeding);
            actualFeeding.setAmount(0.0);
            return actualFeeding;
        }

        CurrencyPetFoodBalances petfoodBalances = currencyService.getPetfoodBalances(jwt);
        Double availablePetFood;
        switch (pet.getAppearance().getGeometry()){
            case circle -> availablePetFood = petfoodBalances.getCircle();
            case triangle -> availablePetFood = petfoodBalances.getTriangle();
            case rectangle -> availablePetFood = petfoodBalances.getRectangle();
            default -> throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }

        if (availablePetFood < petFeeding.getAmount()){
            throw new ResponseStatusException(HttpStatusCode.valueOf(403));
        }

        Double depletedHunger = maxHunger - pet.getHunger();
        Double actualFeedingAmount;
        if(depletedHunger <= petFeeding.getAmount()){
            actualFeedingAmount = depletedHunger;
        }else {
            actualFeedingAmount = petFeeding.getAmount();
        }

        CurrencyPetFoodBalances updatedPetfoodBalances = SerializationUtils.clone(petfoodBalances);
        switch (pet.getAppearance().getGeometry()){
            case circle -> updatedPetfoodBalances.setCircle(availablePetFood - actualFeedingAmount);
            case triangle -> updatedPetfoodBalances.setTriangle(availablePetFood - actualFeedingAmount);
            case rectangle -> updatedPetfoodBalances.setRectangle(availablePetFood - actualFeedingAmount);
            default -> throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        currencyService.updatePetFoodBalances(jwt, updatedPetfoodBalances);

        pet.setLastSeen(LocalDateTime.now());
        pet.setHunger(pet.getHunger() + actualFeedingAmount);
        petRepository.save(pet);

        PetFeeding actualPetFeeding = SerializationUtils.clone(petFeeding);
        actualPetFeeding.setAmount(actualFeedingAmount);
        return actualPetFeeding;
    }

    private Pet getCheckedPet(String userId, Long petId) {
        Optional<Pet> petOptional = petRepository.findByIdAndOwnerId(petId, userId);
        if (petOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        Pet pet = checkPetHunger(petOptional.get());
        if (pet == null){
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        return pet;
    }

    private Pet checkPetHunger(Pet pet) {
        // * - to make math easier to understand
        double hoursSinceLastSeen = LocalDateTime.now().until(pet.getLastSeen(), ChronoUnit.MILLIS) / -3_600_000.000000000000;
        // -8.3333 per hour = 200 per 24h = 100 food every 12 hours
        Double hungerLossSinceLastSeen = hoursSinceLastSeen * 8.3333;
        pet.setHunger(pet.getHunger() - hungerLossSinceLastSeen);
        pet.setLastSeen(LocalDateTime.now());
        if (pet.getHunger() >= 0.00){
            return petRepository.save(pet);
        }
        return checkPetHealth(pet);
    }

    private Pet checkPetHealth(Pet pet) {
        if (pet.getHunger() < 0){
            // Multiply by -0.3333 so that the resultign value is positive and if pet goes from 100% hunger to dead it takes 48h
            double lostHealthByHunger = pet.getHunger() * -0.3333;
            pet.setHealth(pet.getHealth() - lostHealthByHunger);
            pet.setHunger(0.0);
        }

        if (pet.getHealth()<= 0.00){
            petRepository.delete(pet);
            return null;
        }
        return petRepository.save(pet);
    }
}
