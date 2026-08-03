package com.auca.library.service;

import java.util.UUID;

import com.auca.library.dao.UserDao;
import com.auca.library.domain.User;

public class UserService {

    private final UserDao userDao;
    private final LocationService locationService;

    public UserService(UserDao userDao, LocationService locationService) {
        this.userDao = userDao;
        this.locationService = locationService;
    }

    public String getProvinceNameByPersonId(UUID personId) {
        User user = userDao.findById(personId);
        if (user == null) {
            throw new IllegalArgumentException("No user found for id: " + personId);
        }
        if (user.getVillage() == null) {
            throw new IllegalStateException("User has no village assigned");
        }
        return locationService.getProvinceName(user.getVillage());
    }

    public boolean authenticate(String username, String rawPassword) {
        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            return false;
        }

        User user = userDao.findByUserName(username);

        if (user == null) {
            return false;
        }

        return user.getPassword().equals(rawPassword);
    }
}