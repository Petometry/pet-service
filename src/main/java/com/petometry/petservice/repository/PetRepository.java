package com.petometry.petservice.repository;

import com.petometry.petservice.repository.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByOwnerId(String ownerId);

    long countByOwnerId(String ownerId);

    Optional<Pet> findByIdAndOwnerId(Long id, String ownerId);

    Optional<Pet> findByIdAndPetShop_OwnerIdAndPetShop_ValidFor(Long id, String ownerId, LocalDate validFor);
}