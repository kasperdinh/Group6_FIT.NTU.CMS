package group6.fit_ntu_cms.services;

import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersServiceImpl implements UsersService{

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public Optional<UsersModel> login(String email, String password) {
        Optional<UsersModel> userOpt = usersRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            UsersModel user = userOpt.get();
            if (user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean register(UsersModel user) {
        if(usersRepository.findByUsername(user.getUsername().trim()).isPresent() ||
        usersRepository.findByEmail(user.getEmail().trim()).isPresent()){
            return false;
        }
        usersRepository.save(user);
        return true;
    }
}
