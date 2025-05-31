package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.CategoryModel;
import group6.fit_ntu_cms.services.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tittles")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String getAllTittles(Model model, @RequestParam(value = "search", required = false) String search,
                                @RequestParam(value = "page", defaultValue = "1") int page,
                                HttpSession session) {
        int pageSize = 20; // Số mục trên mỗi trang, tương tự WordPress
        List<CategoryModel> tittles = categoryService.getTittles(search, page, pageSize);
        int totalItems = categoryService.countTittles(search);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        model.addAttribute("tittles", tittles);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("tittle", new CategoryModel());
        return "tittle/tittles"; // Tệp HTML Thymeleaf
    }

    @PostMapping("/add")
    public String addTittle(@ModelAttribute CategoryModel tittle, Model model, HttpSession session) {
        try {
            categoryService.saveTittle(tittle);
            model.addAttribute("successMessage", "Thêm danh mục thành công!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi thêm danh mục: " + e.getMessage());
        }
        return "redirect:/tittles";
    }

    @GetMapping("/edit/{id}")
    public String editTittle(@PathVariable Long id, Model model) {
        Optional<CategoryModel> tittle = categoryService.getTittleById(id);
        if (tittle.isPresent()) {
            model.addAttribute("tittle", tittle.get());
            return "tittle/edit-tittle"; // Form chỉnh sửa
        } else {
            return "redirect:/tittles?error=NotFound";
        }
    }

    @PostMapping("/update")
    public String updateTittle(@ModelAttribute CategoryModel tittle, Model model) {
        try {
            categoryService.saveTittle(tittle);
            model.addAttribute("successMessage", "Cập nhật danh mục thành công!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi cập nhật danh mục: " + e.getMessage());
        }
        return "redirect:/tittles";
    }

    @PostMapping("/delete/{id}")
    public String deleteTittle(@PathVariable Long id, Model model) {
        try {
            categoryService.deleteTittle(id);
            model.addAttribute("successMessage", "Xóa danh mục thành công!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi xóa danh mục: " + e.getMessage());
        }
        return "redirect:/tittles";
    }
}