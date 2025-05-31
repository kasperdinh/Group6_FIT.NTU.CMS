package group6.fit_ntu_cms.repositories;

import group6.fit_ntu_cms.models.TittleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TittleRepository extends JpaRepository<TittleModel, Long> {
    Page<TittleModel> findByTitleNameContainingIgnoreCase(String titleName, Pageable pageable);
    long countByTitleNameContainingIgnoreCase(String titleName);
}