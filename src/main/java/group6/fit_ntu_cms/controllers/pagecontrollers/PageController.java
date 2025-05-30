package group6.fit_ntu_cms.controllers.pagecontrollers;

import group6.fit_ntu_cms.models.PageModel;
import group6.fit_ntu_cms.services.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

  @GetMapping("edit/{id}")
  public String showEditForm(@PathVariable Long id, Model model) {
    PageModel page = pageService.getAllPages()
        .stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    if (page == null) return "redirect:/pages";
    model.addAttribute("page", page);
    return "page/edit";
  }

  @PostMapping("/edit/{id}")
  public String updatePage(@PathVariable Long id, @RequestParam String title, @RequestParam String content) {
    pageService.updatePage(id, title, content);
    return "redirect:/pages";
  }

  @GetMapping("/delete/{id}")
  public String deletePage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
      pageService.deletePage(id);
      redirectAttributes.addFlashAttribute("success", "Trang đã được xóa thành công.");
    } catch (IllegalStateException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/pages";
  }

  @GetMapping("/set-default/{id}")
  public String setDefaultPage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
      try {
          pageService.setDefaultPage(id);
          redirectAttributes.addFlashAttribute("success", "Trang đã được đặt làm mặc định.");
      } catch (RuntimeException e) {
          redirectAttributes.addFlashAttribute("error", e.getMessage());
      }
      return "redirect:/pages";
  }
}