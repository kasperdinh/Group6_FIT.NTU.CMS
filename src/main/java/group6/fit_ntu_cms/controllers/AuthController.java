package group6.fit_ntu_cms.controllers;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    public HttpSession session;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping({"/login", "/signin"})
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model,
                        HttpSession session) {
        UsersModel user = userService.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
            return "auth/login";
        }

        session.setAttribute("user", user);
        session.setAttribute("role", user.getRole());

        model.addAttribute("success", "Đăng nhập thành công!");
        return "redirect:/dashboard";
    }

    @GetMapping({"/register", "/signup"})
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping({"/register", "/signup"})
    public String register(@ModelAttribute("user") UsersModel user, Model model) {
        boolean registered = userService.registerUser(user);
        if (!registered) {
            model.addAttribute("error", "Email đã được sử dụng");
            return "auth/register";
        }
        model.addAttribute("success", "Đăng ký thành công! Mời bạn đăng nhập.");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}
