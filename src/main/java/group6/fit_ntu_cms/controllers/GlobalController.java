package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.MenuModel;
import group6.fit_ntu_cms.models.PageModel;
import group6.fit_ntu_cms.models.Role;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.services.MenuService;
import group6.fit_ntu_cms.services.PageService;
import jakarta.servlet.http.HttpSession;
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


  private final HttpSession httpSession;

  public GlobalController(HttpSession httpSession) {
    this.httpSession = httpSession;
  }

  public boolean isUserRole() {
    Role role = (Role) httpSession.getAttribute("role");
    return role == Role.USER;
  }

  @GetMapping({"/", "/home", "/index"})
  public String home(Model model) {
    PageModel defaultPage = pageService.getDefaultPage();
    if (defaultPage != null) {
      UsersModel user = (UsersModel) httpSession.getAttribute("user");
      model.addAttribute("user", user);
      model.addAttribute("page", defaultPage);
      return "redirect:/trang-chu";
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
