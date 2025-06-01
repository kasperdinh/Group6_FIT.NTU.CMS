package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.Role;
import group6.fit_ntu_cms.models.TittleModel;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.services.TittleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tittles")
public class TittleController {

    private final HttpSession httpSession;

    private final GlobalController globalController;

    public TittleController(HttpSession httpSession, GlobalController globalController) {
        this.httpSession = httpSession;
        this.globalController = globalController;
    }

    @Autowired
    private TittleService tittleService;

    @GetMapping
    public String getAllTittles(Model model, @RequestParam(value = "search", required = false) String search,
                                @RequestParam(value = "page", defaultValue = "1") int page,
                                HttpSession session) {

        if (globalController.isUserRole()) {
            return "redirect:/access-denied";
        }
        UsersModel user = (UsersModel) httpSession.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }
        int pageSize = 20; // Số mục trên mỗi trang, tương tự WordPress
        List<TittleModel> tittles = tittleService.getTittles(search, page, pageSize);
        int totalItems = tittleService.countTittles(search);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        model.addAttribute("tittles", tittles);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("tittle", new TittleModel());
        return "tittle/tittles"; // Tệp HTML Thymeleaf
    }

    @PostMapping("/add")
    public String addTittle(@ModelAttribute TittleModel tittle, Model model, HttpSession session) {
        try {
            tittleService.saveTittle(tittle);
            model.addAttribute("successMessage", "Thêm danh mục thành công!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi thêm danh mục: " + e.getMessage());
        }
        return "redirect:/tittles";
    }

    @GetMapping("/edit/{id}")
    public String editTittle(@PathVariable Long id, Model model) {
        Optional<TittleModel> tittle = tittleService.getTittleById(id);
        if (tittle.isPresent()) {
            model.addAttribute("tittle", tittle.get());
            return "tittle/edit-tittle"; // Form chỉnh sửa
        } else {
            return "redirect:/tittles?error=NotFound";
        }
    }

    @PostMapping("/update")
    public String updateTittle(@ModelAttribute TittleModel tittle, Model model) {
        try {
            tittleService.saveTittle(tittle);
            model.addAttribute("successMessage", "Cập nhật danh mục thành công!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi cập nhật danh mục: " + e.getMessage());
        }
        return "redirect:/tittles";
    }

    @PostMapping("/delete/{id}")
    public String deleteTittle(@PathVariable Long id, Model model) {
        try {
            tittleService.deleteTittle(id);
            model.addAttribute("successMessage", "Xóa danh mục thành công!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi xóa danh mục: " + e.getMessage());
        }
        return "redirect:/tittles";
    }
}