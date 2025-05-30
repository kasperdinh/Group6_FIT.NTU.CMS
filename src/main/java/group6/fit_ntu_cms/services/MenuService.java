package group6.fit_ntu_cms.services;

import group6.fit_ntu_cms.models.MenuModel;
import group6.fit_ntu_cms.repositories.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

  @Autowired
  private MenuRepository menuRepository;

  public List<MenuModel> getAllMenus() {
    return menuRepository.findAllByOrderByOrderAsc();
  }

  public void createMenu(MenuModel menu) {
    menuRepository.save(menu);
  }

  public void deleteMenu(Long id) {
    menuRepository.deleteById(id);
  }
}