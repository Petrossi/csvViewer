package com.csvParser.services;

import com.csvParser.models.User;

public interface UserService {
    public User findByEmail(String email);
    public User findById(Long id);

    public User getCurrentUser();
}
