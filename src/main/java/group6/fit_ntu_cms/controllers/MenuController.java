package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.MenuModel;
import group6.fit_ntu_cms.models.Role;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.services.MenuService;
import group6.fit_ntu_cms.services.PageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/menus")
public class MenuController {

  private final HttpSession httpSession;

  private final GlobalController globalController;

  public MenuController(HttpSession httpSession, GlobalController globalController) {
    this.httpSession = httpSession;
    this.globalController = globalController;
  }

  @Autowired
  private MenuService menuService;

  @Autowired
  private PageService pageService;

  @GetMapping
  public String listMenus(Model model) {
    UsersModel user = (UsersModel) httpSession.getAttribute("user");
    if (user == null) {
      return "redirect:/access-denied";
    } else if (globalController.isUserRole()) {
      return "redirect:/access-denied";
    }
    model.addAttribute("user", user);
    model.addAttribute("menus", menuService.getAllMenus());
    model.addAttribute("pages", pageService.getAllPages());
    return "menu/menus";
  }

  @PostMapping("/create")
  public String createMenu(
      @ModelAttribute MenuModel menu,
      @RequestParam("pageId") Long pageId) {

    Role role = (Role) httpSession.getAttribute("role");
    if (role == Role.USER) {
      return "redirect:/access-denied";
    }
    var page = pageService.getPageById(pageId);
    menu.setPage(page);
    menu.setUrl("/" + page.getSlug());

    menuService.createMenu(menu);

    return "redirect:/menus";
  }

  @PostMapping("/delete/{id}")
  public String deleteMenu(@PathVariable Long id) {
    menuService.deleteMenu(id);
    return "redirect:/menus";
  }
}