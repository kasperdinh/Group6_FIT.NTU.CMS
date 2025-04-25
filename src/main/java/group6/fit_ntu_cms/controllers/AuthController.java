package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.services.UsersService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {
    @Autowired
    private UsersService usersService;

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/index")
    public String showIndex() {
        return "index";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        ModelMap model) {
        Optional<UsersModel> user = usersService.login(email, password);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "redirect:index";
        } else {
            model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu");
            return "redirect:index";
        }
    }
}
