package group6.fit_ntu_cms.repositories;

import group6.fit_ntu_cms.models.UsersModel;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<UsersModel, Long> {
    boolean existsByEmail(String email);

    Optional<UsersModel> findByUsername(String username);

    UsersModel findByEmail(String email);
    Page<UsersModel> findAll(Pageable pageable);
}
