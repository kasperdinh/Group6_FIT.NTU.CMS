package group6.fit_ntu_cms.services;

import group6.fit_ntu_cms.models.UsersModel;

import java.util.Optional;

public interface UsersService {
    Optional<UsersModel> login(String email, String password);
    boolean register(UsersModel user);
    Optional<UsersModel> findByEmail(String email);
    Optional<UsersModel> findByResetToken(String token);
    void save(UsersModel user);
}
