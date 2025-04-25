package group6.fit_ntu_cms.services;

import group6.fit_ntu_cms.models.UsersModel;

import java.util.Optional;

public interface UsersService {
    Optional<UsersModel> login(String email, String password);
}
