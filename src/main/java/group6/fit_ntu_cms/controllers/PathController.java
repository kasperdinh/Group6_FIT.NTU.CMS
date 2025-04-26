package group6.fit_ntu_cms.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PathController {
  @GetMapping("/")
  public String index() {
    return "index";
  }

}