package group6.fit_ntu_cms.controllers.publiccontrollers;

import group6.fit_ntu_cms.models.PageModel;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.services.MenuService;
import group6.fit_ntu_cms.services.PageService;
import group6.fit_ntu_cms.services.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class PublicPageController {

    private final HttpSession httpSession;

    public PublicPageController(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Autowired
    private PageService pageService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private PostService postService;

    @GetMapping("/{slug}")
    public String viewPage(@PathVariable String slug, Model model) {
        Optional<PageModel> page = pageService.getPageBySlug(slug);
        if (page.isEmpty()) {
            model.addAttribute("errorMessage", "Không tìm thấy trang có đường dẫn (slug) tương ứng.");
            return "404"; // Redirect to a custom error page
        }

        UsersModel user = (UsersModel) httpSession.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("page", page.get());
        model.addAttribute("menus", menuService.getAllMenus());
        model.addAttribute("posts", postService.getPostsByPageId(page.get().getId()));
        return "public/public";
    }
}