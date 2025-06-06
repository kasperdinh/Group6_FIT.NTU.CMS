package group6.fit_ntu_cms.repositories;

import group6.fit_ntu_cms.models.Role;
import group6.fit_ntu_cms.models.UsersModel;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<UsersModel, Long> {
    boolean existsByEmail(String email);

    Optional<UsersModel> findByUsername(String username);
    List<UsersModel> findByRole(Role role);
    UsersModel findByEmail(String email);
    Page<UsersModel> findAll(Pageable pageable);
    Page<UsersModel> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email, Pageable pageable);
}
