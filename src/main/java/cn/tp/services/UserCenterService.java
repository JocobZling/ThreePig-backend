package cn.tp.services;

import cn.tp.entities.User;
import cn.tp.exceptions.BusinessException;
import cn.tp.repositories.UserRepository;
import cn.tp.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;


@Service
public class UserCenterService {

    private final UserRepository userRepository;

    @Autowired
    public UserCenterService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public HashMap<String, Object> findUserByEmailAndPasswordOrThrow(String email, String password) throws BusinessException, UnsupportedEncodingException {
        User user = userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new BusinessException("账户或密码错误"));
        if (user != null) {
            return new HashMap<String, Object>() {{
                put("token", JwtUtil.build(user));
                put("user", user.hidePassword());
            }};
        } else {
            return null;
        }
    }

    public User isRegisterUser(User info) {
        User user = new User();
        user.setEmail(info.getEmail());
        user.setPassword(info.getPassword());
        user.setName(info.getName());
        return userRepository.save(user);
    }

    public Boolean isUserExits(String email) {
        List<User> user = userRepository.findByEmail(email);
        return user.size() != 0;
    }


}
