package cn.tp.controllers;

import cn.tp.entities.User;
import cn.tp.services.UserCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api")
public class UserController {

    @GetMapping("/users")
    public ResponseEntity getUserById() {
        return ResponseEntity.ok("123");
    }

}
