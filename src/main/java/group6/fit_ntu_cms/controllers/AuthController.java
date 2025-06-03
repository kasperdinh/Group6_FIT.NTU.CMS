package group6.fit_ntu_cms.controllers;
import group6.fit_ntu_cms.models.Role;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.repositories.UsersRepository;
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

import java.time.Duration;
import java.time.LocalDateTime;

@Controller
public class AuthController {
    public HttpSession session;

    @Autowired
    private UserService userService;

    @Autowired
    private UsersRepository usersRepository;

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

        if (user.getRole() == Role.USER) {
            return "redirect:/";
        } else {
            return "redirect:/dashboard";
        }
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

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {return "auth/forgot-password";}

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        userService.generateAndSendOtp(email);
        model.addAttribute("email", email);
        return "auth/enter-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("email") String email,
                            @RequestParam("otp") String otp,
                            Model model) {
        UsersModel user = usersRepository.findByEmail(email);
        if (user != null && user.getOtp().equals(otp) &&
                Duration.between(user.getOtpRequestedTime(), LocalDateTime.now()).toMinutes() <= 10) {
            model.addAttribute("email", email);
            return "auth/reset-password";
        } else {
            model.addAttribute("error", "Invalid or expired OTP");
            return "auth/enter-otp";
        }
    }

    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam("email") String email, Model model) {
        UsersModel user = usersRepository.findByEmail(email);
        if (user != null) {
            user.setOtp(null);
            user.setOtpRequestedTime(null);
            usersRepository.save(user);
            userService.generateAndSendOtp(email);
            model.addAttribute("email", email);
            model.addAttribute("success", "Mã OTP mới đã được gửi.");
            return "auth/enter-otp";
        } else {
            model.addAttribute("error", "Không tìm thấy địa chỉ email.");
            return "auth/forgot-password";
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("email") String email,
                                @RequestParam("password") String password,
                                Model model) {
        UsersModel user = usersRepository.findByEmail(email);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(password));
            user.setOtp(null);
            user.setOtpRequestedTime(null);
            usersRepository.save(user);
            return "redirect:/login?resetSuccess";
        } else {
            model.addAttribute("error", "Không tìm thấy người dùng");
            return "auth/reset-password";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}
