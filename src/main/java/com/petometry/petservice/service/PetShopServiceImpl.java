package com.petometry.petservice.service;

import com.petometry.petservice.repository.PetShopRepository;
import com.petometry.petservice.repository.model.Pet;
import com.petometry.petservice.repository.model.PetShop;
import com.petometry.petservice.rest.model.PetShopDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PetShopServiceImpl implements PetShopService {

    private final PetShopRepository petShopRepository;

    private final ModelMapper modelMapper;

    private final PetFactory petFactory;

    @Override
    public PetShopDto getPetShop(String userId) {

        Optional<PetShop> petShopOtional = petShopRepository.findByOwnerIdAndValidFor(userId, LocalDate.now());
        PetShop petShop;
        petShop = petShopOtional.orElseGet(() -> createPetShop(userId));
        petShopRepository.deleteByOwnerIdAndValidForLessThan(userId, LocalDate.now());

        return modelMapper.map(petShop, PetShopDto.class);
    }

    private PetShop createPetShop(String userId) {
        PetShop petShop = new PetShop();
        petShop.setOwnerId(userId);
        petShop.setValidFor(LocalDate.now());
        List<Pet> pets = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Pet pet = petFactory.createRandomPet();
            pet.setPetShop(petShop);
            pets.add(pet);
        }
        petShop.setPets(pets);
        return petShopRepository.save(petShop);
    }
}
