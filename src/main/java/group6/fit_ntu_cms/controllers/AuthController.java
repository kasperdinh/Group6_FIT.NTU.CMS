package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.services.UsersService;
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

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/login" )
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        ModelMap model) {
        Optional<UsersModel> user = usersService.login(email, password);
        if(user.isPresent()) {
            model.addAttribute("user", user.get());
            return "redirect:/index";
        } else {
            model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu");
            return "redirect:/login";
        }
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String repeatPassword,
                           ModelMap model) {
        if(!repeatPassword.equals(password)) {
            model.addAttribute("error", "Mật khẩu không khớp");
            return "register";
        }
        UsersModel user = new UsersModel();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        boolean result = usersService.register(user);
        if(result) {
            model.addAttribute("success", "Đăng ký tài khoản thành công");
            return "redirect:/login";
        } else {
            model.addAttribute("error", "Email hoặc Username đã tồn tại");
            return "redirect:/register";
        }
    }
}
