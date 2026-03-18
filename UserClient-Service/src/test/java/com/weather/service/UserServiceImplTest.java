package com.weather.service;

import com.weather.common.response.DataResult;
import com.weather.config.UserSecurityProperties;
import com.weather.entity.request.LoginRequest;
import com.weather.entity.request.RegisterRequest;
import com.weather.entity.respond.LoginRespond;
import com.weather.mapper.UserMapper;
import com.weather.mapper.redis.UserRedis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRedis userRedis;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        UserSecurityProperties properties = new UserSecurityProperties();
        properties.setTokenBytes(16);
        userService = new UserServiceImpl(userMapper, userRedis, passwordEncoder, properties);
    }

    @Test
    void shouldLoginAndSaveToken() {
        LoginRequest request = new LoginRequest();
        request.setUsername("tester");
        request.setPassword("secret");

        when(userMapper.getUid("tester")).thenReturn(1);
        when(userMapper.getEncryptedPassword(1)).thenReturn("encoded");
        when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);

        LoginRespond respond = userService.authUser(request);

        assertEquals(1, respond.getSuccess());
        verify(userRedis).saveToken(anyString(), anyString());
    }

    @Test
    void shouldRejectDuplicateRegistration() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("tester");
        request.setPassword("secret");
        when(userMapper.getUid("tester")).thenReturn(1);

        DataResult result = userService.insertUser(request);

        assertEquals(0, result.getSuccess());
    }
}
