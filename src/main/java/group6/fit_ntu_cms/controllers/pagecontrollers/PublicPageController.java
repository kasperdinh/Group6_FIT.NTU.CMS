package group6.fit_ntu_cms.controllers.page;

import group6.fit_ntu_cms.models.PageModel;
import group6.fit_ntu_cms.services.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PublicPageController {

  @Autowired
  private PageService pageService;

  @GetMapping("/{slug}")
  public String viewPage(@PathVariable String slug, Model model) {
    PageModel page = pageService.getPageBySlug(slug);
    if (page == null) {
      return "404";
    }
    model.addAttribute("page", page);
    return "page/public"; // hiển thị nội dung trang
  }
}