package cn.tp.controllers;

import cn.tp.entities.User;
import cn.tp.entities.vo.UserEmailAndPassword;
import cn.tp.exceptions.BusinessException;
import cn.tp.services.UserCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;


@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    private final UserCenterService userCenterService;

    @Autowired
    public UserController(UserCenterService userCenterService) {
        this.userCenterService = userCenterService;
    }

    @PostMapping("/login")
    public ResponseEntity<HashMap<String, Object>> getUserByEmailAndPassword(@RequestBody UserEmailAndPassword userInfo) throws BusinessException, UnsupportedEncodingException {
        return ResponseEntity.ok(userCenterService.findUserByEmailAndPasswordOrThrow(userInfo.getEmail(), userInfo.getPassword()));
    }

    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody User info) {
        Boolean isUserExits = userCenterService.isUserExits(info.getEmail());
        if (isUserExits) {
            return new ResponseEntity<>("账户已存在", HttpStatus.BAD_REQUEST);
        }
        User result = userCenterService.isRegisterUser(info);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<String> getUser() {
        return ResponseEntity.ok("123");
    }

}
