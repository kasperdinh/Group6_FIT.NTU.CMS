package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.Role;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.services.UsersService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import java.util.Optional;

@Controller
public class AuthController {
    @Autowired
    private UsersService usersService;


    @PostMapping({"/login", "signin"})
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        ModelMap model,
                        HttpSession session) {
        Optional<UsersModel> user = usersService.login(email, password);
        if(user.isPresent()) {
            session.setAttribute("username", user.get().getUsername());
            session.setAttribute("role", user.get().getRole().name());
            model.addAttribute("success","Login successfully!");
            return "index";
        } else {
            model.addAttribute("error", "Incorrect email ỏ password!");
            return "login";
        }
    }

    @PostMapping({"/register", "signup"})
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String repeatPassword,
                           ModelMap model) {
        if(!repeatPassword.equals(password)) {
            model.addAttribute("error", "Password do not match.");
            return "register";
        }
        UsersModel user = new UsersModel();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setCreatedDate(LocalDateTime.now());
        user.setRole(Role.USER);

        boolean result = usersService.register(user);
        if(result) {
            model.addAttribute("success", "Registration successfully!");
            return "login";
        } else {
            return "register";
        }
    }

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, ModelMap model) {
        Optional<UsersModel> userOpt = usersService.findByEmail(email);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Email not found");
            return "forgot-password";
        }

        UsersModel user = userOpt.get();
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        usersService.save(user);

        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        sendResetEmail(email, resetLink);

        model.addAttribute("success", "Successfully! Please check your email to reset your password.");
        return "forgot-password";
    }

    private void sendResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password reset request");
        message.setText("Click the link to reset your password: " + resetLink);
        mailSender.send(message);
    }

    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam("token") String token, ModelMap model) {
        Optional<UsersModel> userOpt = usersService.findByResetToken(token);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Token invalid.");
            return "reset-password";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                       @RequestParam String password,
                                       @RequestParam String confirmPassword,
                                       ModelMap model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Password do not match.");
            model.addAttribute("token", token);
            return "reset-password";
        }

        Optional<UsersModel> userOpt = usersService.findByResetToken(token);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Token invalid.");
            return "reset-password";
        }

        UsersModel user = userOpt.get();
        user.setPassword(password);
        user.setResetToken(null);
        usersService.save(user);

        model.addAttribute("success", "Password reset successfully. You can now log in.");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}
