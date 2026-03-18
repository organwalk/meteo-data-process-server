package com.weather.service;

import com.weather.common.response.DataResult;
import com.weather.config.UserSecurityProperties;
import com.weather.entity.request.LoginRequest;
import com.weather.entity.request.RegisterRequest;
import com.weather.entity.respond.LoginRespond;
import com.weather.mapper.UserMapper;
import com.weather.mapper.redis.UserRedis;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRedis userRedis;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserSecurityProperties properties;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public LoginRespond authUser(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        Integer uid = userMapper.getUid(username);
        if (uid == null) {
            return LoginRespond.not_found();
        }

        String realPassword = userMapper.getEncryptedPassword(uid);
        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), realPassword)) {
            return LoginRespond.not_found();
        }

        String token = generateToken();
        userRedis.saveToken(username, token);
        return LoginRespond.ok(username, token);
    }

    @Override
    public DataResult insertUser(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        Integer uid = userMapper.getUid(username);
        if (uid != null) {
            return DataResult.fail("鐢ㄦ埛" + username + "宸插瓨鍦?");
        }
        userMapper.insertUser(username, bCryptPasswordEncoder.encode(registerRequest.getPassword()));
        return DataResult.success("鐢ㄦ埛" + username + "娉ㄥ唽鎴愬姛");
    }

    @Override
    public DataResult logout(String username) {
        if (!userRedis.checkUserStatus(username)) {
            return DataResult.fail("鐢ㄦ埛灏氭湭鐧诲綍锛屾棤娉曢攢姣佷护鐗?");
        }
        userRedis.voidAccessToken(username);
        return DataResult.success("宸叉垚鍔熼攢姣佺敤鎴?" + username + "鐨勪护鐗?");
    }

    @Override
    public String getAccessToken(String username) {
        return userRedis.getAccessToken(username);
    }

    private String generateToken() {
        byte[] randomBytes = new byte[properties.getTokenBytes()];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
