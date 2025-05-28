package group6.fit_ntu_cms.services;

import group6.fit_ntu_cms.models.Role;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean registerUser(UsersModel user) {
        if (usersRepository.existsByEmail(user.getEmail())) {
            return false; // Email đã tồn tại
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedDate(LocalDateTime.now());
        user.setRole(Role.USER);
        usersRepository.save(user);
        return true;
    }

    public UsersModel findByUsername(String username) {
        return usersRepository.findByUsername(username).orElse(null);
    }
}