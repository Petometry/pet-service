package com.petometry.petservice.service;

import com.petometry.petservice.repository.model.Geometry;
import com.petometry.petservice.repository.model.Pet;
import com.petometry.petservice.repository.model.PetAppearance;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PetFactoryImpl implements PetFactory {

    private final Random random;

    @Value("${max-health:100.0}")
    private double maxHealth;

    @Value("${max-hunger:100.0}")
    private double maxHunger;

    public Pet createRandomPet() {

        Pet pet = createPet();
        Geometry[] shapes = Geometry.values();
        int shapeCount = shapes.length;
        Geometry geometry = shapes[random.nextInt(shapeCount)];
        pet.setAppearance(createPetAppearance(geometry));
        return pet;
    }

    @Override
    public Pet createSquare() {
        Pet pet = createPet();
        pet.setAppearance(createPetAppearance(Geometry.circle));
        return pet;
    }

    @Override
    public Pet createTriangle() {
        Pet pet = createPet();
        pet.setAppearance(createPetAppearance(Geometry.triangle));
        return pet;
    }

    @Override
    public Pet createRectangle() {
        Pet pet = createPet();
        pet.setAppearance(createPetAppearance(Geometry.rectangle));
        return pet;
    }

    private Pet createPet() {
        Pet pet = new Pet();
        pet.setHealth(maxHealth);
        pet.setHunger(maxHunger);
        pet.setLastSeen(LocalDateTime.now());
        return pet;
    }

    private PetAppearance createPetAppearance(Geometry geometry) {
        PetAppearance petAppearance = new PetAppearance();
        petAppearance.setGeometry(geometry);
        petAppearance.setColor(getRandomHexColor());
        return petAppearance;
    }

    private String getRandomHexColor() {
        StringBuilder color = new StringBuilder();
        color.append("#");

        while (color.length() <= 6) {
            color.append(Integer.toHexString(random.nextInt(16)));
        }
        return color.toString();
    }
}
