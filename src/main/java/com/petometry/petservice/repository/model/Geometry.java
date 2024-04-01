package com.petometry.petservice.repository.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Geometry {
    circle(0),
    triangle(3),
    rectangle(4);

    final int corners;

}
