package com.petometry.petservice.service;

import com.petometry.petservice.repository.PetRepository;
import com.petometry.petservice.repository.PetShopRepository;
import com.petometry.petservice.repository.model.Geometry;
import com.petometry.petservice.repository.model.Pet;
import com.petometry.petservice.repository.model.PetAppearance;
import com.petometry.petservice.repository.model.PetShop;
import com.petometry.petservice.rest.model.PetDetailsDto;
import com.petometry.petservice.rest.model.PetOverviewDto;
import com.petometry.petservice.rest.model.PetShopDto;
import com.petometry.petservice.service.model.currency.CurrencyGeocoinsBalances;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;

    private final ModelMapper modelMapper;

    private final PetShopRepository petShopRepository;

    private final CurrencyService currencyService;

    @Override
    public List<PetOverviewDto> getPets(String userId) {

        List<Pet> pets = petRepository.findByOwnerId(userId);
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
        CurrencyGeocoinsBalances balances = currencyService.getBalances(jwt, userId);
        if (balances.getGeocoin() < 10) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(403));
        }
        pet.setOwnerId(userId);
        pet.setPetShop(null);
        Pet boughtPet = petRepository.save(pet);
//      Todo switch 10 with cost of pet once that is implemented
        currencyService.payServer(jwt, userId, 10.0);
        return modelMapper.map(boughtPet, PetDetailsDto.class);
    }

    @Override
    public void deletePet(String userId, Long petId) {

        Optional<Pet> petOptional = petRepository.findByIdAndOwnerId(petId, userId);
        if (petOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        Pet pet = petOptional.get();
        petRepository.delete(pet);
    }

    @Override
    public PetShopDto getPetShop(String userId) {

        Optional<PetShop> petShopOtional = petShopRepository.findByOwnerIdAndValidFor(userId, LocalDate.now());
        PetShop petShop;
        petShop = petShopOtional.orElseGet(() -> createPetShop(userId));
        petShopRepository.deleteByOwnerIdAndValidForLessThan(userId, LocalDate.now());

        return modelMapper.map(petShop, PetShopDto.class);
    }

    @Override
    public PetOverviewDto getPet(String userId, Long petId) {
        Optional<Pet> petOptional = petRepository.findByIdAndOwnerId(petId, userId);
        if (petOptional.isEmpty()){
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        Pet pet = petOptional.get();
        return modelMapper.map(pet, PetDetailsDto.class);
    }

    private PetShop createPetShop(String userId) {
        PetShop petShop = new PetShop();
        petShop.setOwnerId(userId);
        petShop.setValidFor(LocalDate.now());
        List<Pet> pets = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Pet pet = createRandomPet();
            pet.setPetShop(petShop);
            pets.add(pet);
        }
        petShop.setPets(pets);
        return petShopRepository.save(petShop);
    }

    private Pet createRandomPet() {
        final Pet pet = new Pet();
        pet.setAppearance(createPetShape());
        return pet;
    }

    private String getRandomHexColor() {
        StringBuilder color = new StringBuilder();
        color.append("#");
        Random random = new Random();
        while (color.length() <= 6) {
            color.append(Integer.toHexString(random.nextInt(16)));
        }
        return color.toString();
    }

    private PetAppearance createPetShape() {

        Geometry[] shapes = Geometry.values();
        int shapeCount = shapes.length;
        PetAppearance petAppearance = new PetAppearance();
        Geometry geometry = shapes[new Random().nextInt(shapeCount)];
        petAppearance.setGeometry(geometry);
        petAppearance.setColor(getRandomHexColor());
        return petAppearance;
    }
}
