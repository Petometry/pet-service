package com.petometry.petservice.service;

import com.petometry.petservice.repository.model.Pet;

public interface PetFactory {
    Pet createRandomPet();
    Pet createSquare();
    Pet createTriangle();
    Pet createRectangle();

}
