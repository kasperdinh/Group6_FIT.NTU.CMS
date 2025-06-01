package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.Role;
import group6.fit_ntu_cms.models.UsersModel;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
  private final HttpSession httpSession;

  private final GlobalController globalController;

  public DashboardController(HttpSession httpSession, GlobalController globalController) {
      this.httpSession = httpSession;
      this.globalController = globalController;
  }

  @GetMapping("/dashboard")
  public String dashboard(Model model) {
    UsersModel user = (UsersModel) httpSession.getAttribute("user");
    if (user == null) {
      return "redirect:/access-denied";
    } else if (globalController.isUserRole()) {
      return "redirect:/access-denied";
    }
    model.addAttribute("user", user);
    return "dashboard";
  }
}