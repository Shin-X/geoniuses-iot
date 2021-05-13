package com.geoniuses.mqtt.service.impl;



import com.geoniuses.mqtt.service.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public boolean checkValid(String username, String password) {
        if (StringUtils.isEmpty(username)) {
            return false;
        }
        if (StringUtils.isEmpty(password)) {
            return false;
        }
        return true;
    }
}
