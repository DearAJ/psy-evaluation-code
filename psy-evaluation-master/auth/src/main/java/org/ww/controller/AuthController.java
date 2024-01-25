package org.ww.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
public class AuthController {

    @RequestMapping("/checkUser")
    public Principal checkUser(Principal user) {
        return user;
    }

}
