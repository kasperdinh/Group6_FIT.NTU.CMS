package group6.fit_ntu_cms.controllers.pagecontrollers;

import group6.fit_ntu_cms.models.PageModel;
import group6.fit_ntu_cms.services.MenuService;
import group6.fit_ntu_cms.services.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class PublicPageController {

  @Autowired
  private PageService pageService;

  @Autowired
  private MenuService menuService;

  @GetMapping("/{slug}")
  public String viewPage(@PathVariable String slug, Model model) {
      Optional<PageModel> page = pageService.getPageBySlug(slug);
      if (page.isEmpty()) {
          model.addAttribute("errorMessage", "Không tìm thấy trang có đường dẫn (slug) tương ứng.");
          return "404"; // Redirect to a custom error page
      }
      model.addAttribute("page", page.get());
      model.addAttribute("menus", menuService.getAllMenus());
      return "page/public";
  }
}