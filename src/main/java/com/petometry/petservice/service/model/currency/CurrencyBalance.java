package com.petometry.petservice.service.model.currency;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CurrencyBalance {

    private CurrencyType currency;

    private Double balance;
}
