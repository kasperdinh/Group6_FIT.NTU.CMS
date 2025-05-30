package group6.fit_ntu_cms.repositories;

import group6.fit_ntu_cms.models.TittleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TittleRepository extends JpaRepository<TittleModel, Long> {
}