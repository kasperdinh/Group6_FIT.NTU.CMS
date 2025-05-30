package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.Role;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
  private final HttpSession httpSession;

  public DashboardController(HttpSession httpSession) {
    this.httpSession = httpSession;
  }

  @GetMapping("/dashboard")
  public String dashboard() {
    Role role = (Role) httpSession.getAttribute("role");
    if (role != Role.ADMIN) {
      return "redirect:/login";
    }
    return "dashboard";
  }
}