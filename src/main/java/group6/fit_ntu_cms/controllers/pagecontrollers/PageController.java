package group6.fit_ntu_cms.controllers.page;

import group6.fit_ntu_cms.models.PageModel;
import group6.fit_ntu_cms.services.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pages")
public class PageController {

  @Autowired
  private PageService pageService;

  @GetMapping
  public String listPages(Model model) {
    model.addAttribute("pages", pageService.getAllPages());
    return "page/pages";
  }

  @GetMapping("/create")
  public String showCreateForm() {
    return "page/create";
  }

  @PostMapping("/create")
  public String createPage(@RequestParam String title, @RequestParam String content) {
    pageService.createPage(title, content);
    return "redirect:/pages";
  }

  @GetMapping("/{id}/edit")
  public String showEditForm(@PathVariable Long id, Model model) {
    PageModel page = pageService.getAllPages()
        .stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    if (page == null) return "redirect:/pages";
    model.addAttribute("page", page);
    return "page/edit";
  }

  @PostMapping("/{id}/edit")
  public String updatePage(@PathVariable Long id, @RequestParam String title, @RequestParam String content) {
    pageService.updatePage(id, title, content);
    return "redirect:/pages";
  }
}