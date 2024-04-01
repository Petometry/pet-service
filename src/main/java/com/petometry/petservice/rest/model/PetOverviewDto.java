package com.petometry.petservice.rest.model;

import lombok.Data;
import lombok.ToString;


/**
 * DTO for {@link com.petometry.petservice.repository.model.Pet}
 */
@Data
@ToString
public class PetOverviewDto {

    private Long id;

    private PetAppearanceDto appearance;

    private Double hunger;

}
