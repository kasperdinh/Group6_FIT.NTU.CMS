  package group6.fit_ntu_cms.controllers;

  import org.springframework.stereotype.Controller;
  import org.springframework.web.bind.annotation.GetMapping;

  @Controller
  public class PathController {
    @GetMapping({"/", "/index"})
    public String index() {
      return "index";
    }

    @GetMapping({"/register", "/signup"})
    public String register() {
      return "register";
    }

    @GetMapping({"/login", "/signin"})
    public String login() {
      return "login";
    }
  }