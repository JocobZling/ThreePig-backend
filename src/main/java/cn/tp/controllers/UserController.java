package cn.hs.controllers;

import cn.hs.entities.User;
import cn.hs.services.UserCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api")
public class UserController {

//    @Autowired
//    private UserCenterService userCenterService;
//
//    @GetMapping("/users/{userId}")
//    public ResponseEntity getUserById(@PathVariable Long userId) {
//        User result = userCenterService.findUserById(userId);
//        return ResponseEntity.ok(result);
//    }

}
