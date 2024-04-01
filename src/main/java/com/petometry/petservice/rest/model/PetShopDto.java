package com.petometry.petservice.rest.model;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link com.petometry.petservice.repository.model.PetShop}
 */
@Data
@ToString
public class PetShopDto implements Serializable {
    Long id;
    @Size(min = 6, max = 6)
    List<PetOverviewDto> pets;
    String ownerId;
    LocalDate validFor;
}