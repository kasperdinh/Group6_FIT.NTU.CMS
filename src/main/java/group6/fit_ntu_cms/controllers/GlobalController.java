package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.MenuModel;
import group6.fit_ntu_cms.models.PageModel;
import group6.fit_ntu_cms.services.MenuService;
import group6.fit_ntu_cms.services.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class GlobalController {

  @Autowired
  private PageService pageService;

  @Autowired
  private MenuService menuService;

  @GetMapping({"/", "/home", "/index"})
  public String home(Model model) {
    PageModel defaultPage = pageService.getDefaultPage();
    if (defaultPage != null) {
      model.addAttribute("page", defaultPage);
      return "page/public";
    }
    return "index";
  }

  @GetMapping("/access-denied")
  public String accessDenied() {
    return "access-denied";
  }

  @ModelAttribute("menus")
  public List<MenuModel> addMenusToModel() {
    return menuService.getAllMenus();
  }
}
