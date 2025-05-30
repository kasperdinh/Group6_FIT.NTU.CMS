package group6.fit_ntu_cms.repositories;

import group6.fit_ntu_cms.models.MenuModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<MenuModel, Long> {
  List<MenuModel> findAllByOrderByOrderAsc();
}