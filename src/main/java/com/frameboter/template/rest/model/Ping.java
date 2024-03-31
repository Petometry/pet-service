package com.frameboter.template.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Ping {

    private String userName;

    private LocalDateTime time;
}
