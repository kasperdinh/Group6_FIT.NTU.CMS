package group6.fit_ntu_cms.repositories;

import group6.fit_ntu_cms.models.PageModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PageRepository extends JpaRepository<PageModel, Long> {
  Optional<PageModel> findBySlug(String slug);
}
