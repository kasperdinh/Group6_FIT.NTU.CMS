package group6.fit_ntu_cms.repositories;

import group6.fit_ntu_cms.models.UsersModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<UsersModel, Long> {
    Optional<UsersModel> findByEmail(String email);
    Optional<UsersModel> findByUsername(String username);
    Optional<UsersModel> findByResetToken(String resetToken);
}
