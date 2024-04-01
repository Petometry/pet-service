package com.petometry.petservice.rest.model;

import com.petometry.petservice.repository.model.PetAppearance;
import com.petometry.petservice.repository.model.Geometry;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link PetAppearance}
 */
@Data
public class PetAppearanceDto implements Serializable {

    Geometry geometry;

    private String color;
}