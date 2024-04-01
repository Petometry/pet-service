package com.petometry.petservice.rest.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * DTO for {@link com.petometry.petservice.repository.model.Pet}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class PetDetailsDto extends PetOverviewDto {

}
