package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.Role;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.repositories.UsersRepository;
import group6.fit_ntu_cms.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final HttpSession httpSession;

    private final GlobalController globalController;

    public UserController(HttpSession httpSession, GlobalController globalController) {
        this.httpSession = httpSession;
        this.globalController = globalController;
    }

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(ModelMap model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "7") int size,
                            @RequestParam(required = false) String keyword) {
        Page<UsersModel> userPage;
        UsersModel user = (UsersModel) httpSession.getAttribute("user");
        if (user == null) {
            return "redirect:/access-denied";
        } else if (globalController.isUserRole()) {
            return "redirect:/access-denied";
        }

        if (keyword != null && !keyword.isEmpty()) {
            userPage = userService.searchUsers(keyword, PageRequest.of(page, size));
        } else {
            userPage = userService.findAllUsers(PageRequest.of(page, size));
        }
        model.addAttribute("user", user);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "user/users";
    }

    @GetMapping("/view/{id}")
    public String viewUser(@PathVariable Long id, ModelMap model, RedirectAttributes redirectAttributes) {
        UsersModel user = userService.findById(id);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng.");
            return "redirect:/users";
        }
        model.addAttribute("user", user);
        return "user/view";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, ModelMap model, RedirectAttributes redirectAttributes) {
        UsersModel user = userService.findById(id);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng.");
            return "redirect:/users";
        }
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "user/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("user") UsersModel updatedUser,
                             RedirectAttributes redirectAttributes) {
        UsersModel user = userService.findById(id);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại!");
            return "redirect:/users";
        }

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());
        userService.save(user);

        redirectAttributes.addFlashAttribute("success", "Cập nhật người dùng thành công!");
        return "redirect:/users";
    }

    @GetMapping("/create")
    public String showCreateForm(ModelMap model) {
        Role role = (Role) httpSession.getAttribute("role");
        if (role == Role.USER) {
            return "redirect:/access-denied";
        }
        model.addAttribute("user", new UsersModel());
        model.addAttribute("roles", Role.values());
        return "user/create";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") UsersModel user, RedirectAttributes redirectAttributes) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setCreatedDate(LocalDateTime.now());
        userService.save(user);
        redirectAttributes.addFlashAttribute("success", "Tạo mới người dùng thành công!");
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        UsersModel user = userService.findById(id);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại!");
        } else {
            userService.delete(user);
            redirectAttributes.addFlashAttribute("success", "Đã xóa người dùng!");
        }
        return "redirect:/users";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute UsersModel updatedUser, HttpSession session, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        UsersModel currentUser = (UsersModel) session.getAttribute("user");
        boolean changed = false;
        if (!currentUser.getEmail().equals(updatedUser.getEmail())) {
            currentUser.setEmail(updatedUser.getEmail());
            changed = true;
        }
        String newPassword = updatedUser.getPassword();

        if (newPassword != null && !newPassword.isBlank()) {
            if (!passwordEncoder.matches(newPassword, currentUser.getPassword())) {
                currentUser.setPassword(passwordEncoder.encode(newPassword));
                changed = true;
            }
        }
        if (changed) {
            userService.save(currentUser);
            session.setAttribute("user", currentUser);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
        }
        else {
            redirectAttributes.addFlashAttribute("warning", "Bạn chưa thay đổi thông tin nào.");
        }
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isBlank()) {
            referer = "/dashboard";
        }

        return "redirect:" + referer;
    }
}

