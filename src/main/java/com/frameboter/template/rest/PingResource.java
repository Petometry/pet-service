package com.frameboter.template.rest;

import com.frameboter.rest.AbstractResource;
import com.frameboter.template.rest.model.Ping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class PingResource extends AbstractResource {

    @PostMapping("/ping")
    Ping createPing(@AuthenticationPrincipal Jwt jwt) {

        return new Ping(getUserName(jwt), LocalDateTime.now());
    }
}
