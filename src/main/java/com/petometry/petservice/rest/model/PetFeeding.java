package com.petometry.petservice.rest.model;

import com.petometry.petservice.repository.model.Geometry;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class PetFeeding implements Serializable {

    private Long petId;
    private Double amount;
    private Geometry foodType;
}
