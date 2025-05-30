package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.Role;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private UsersRepository userRepository;

    @GetMapping
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size,
            ModelMap model
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UsersModel> userPage = userRepository.findAll(pageable);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());

        return "user/users";
    }

    @GetMapping("/view/{id}")
    public String viewUser(@PathVariable Long id, ModelMap model, RedirectAttributes redirectAttributes) {
        UsersModel user = userRepository.findById(id).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng.");
            return "redirect:/users";
        }
        model.addAttribute("user", user);
        return "user/view";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, ModelMap model, RedirectAttributes redirectAttributes) {
        UsersModel user = userRepository.findById(id).orElse(null);
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
        UsersModel user = userRepository.findById(id).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại!");
            return "redirect:/users";
        }

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Cập nhật người dùng thành công!");
        return "redirect:/users";
    }

    @GetMapping("/create")
    public String showCreateForm(ModelMap model) {
        model.addAttribute("user", new UsersModel());
        model.addAttribute("roles", Role.values());
        return "user/create";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") UsersModel user, RedirectAttributes redirectAttributes) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setCreatedDate(LocalDateTime.now());
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Tạo mới người dùng thành công!");
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        UsersModel user = userRepository.findById(id).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại!");
        } else {
            userRepository.delete(user);
            redirectAttributes.addFlashAttribute("success", "Đã xóa người dùng!");
        }
        return "redirect:/users";
    }
}
