package com.petometry.petservice.service.model.currency;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class CurrencyPetFoodBalances implements Serializable {

    @NotNull
    @Min(value = 0, message = "Circle can not be negative")
    private Double circle;

    @NotNull
    @Min(value = 0, message = "Triangle can not be negative")
    private Double triangle;

    @NotNull
    @Min(value = 0, message = "Rectangle can not be negative")
    private Double rectangle;
}