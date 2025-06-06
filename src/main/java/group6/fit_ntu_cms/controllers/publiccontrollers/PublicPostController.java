package group6.fit_ntu_cms.controllers.publiccontrollers;

import group6.fit_ntu_cms.models.MenuModel;
import group6.fit_ntu_cms.models.PostModel;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.repositories.PostRepository;
import group6.fit_ntu_cms.services.MenuService;
import group6.fit_ntu_cms.services.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class  PublicPostController {

    private final HttpSession httpSession;

    public PublicPostController(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Autowired
    private PostService postService;

    @Autowired
    private MenuService menuService;

    @GetMapping("/{slug}/post/{id}")
    public String getPostDetails(
            @PathVariable("slug") String slug,
            @PathVariable("id") Long postId,
            Model model) {

        PostModel post = postService.getPostById(postId);
        UsersModel user = (UsersModel) httpSession.getAttribute("user");
        List<MenuModel> menus = menuService.getAllMenus();

        MenuModel currentMenu = menus.stream()
                .filter(m -> m.getPage().getSlug().equals(slug))
                .findFirst()
                .orElse(null);

        model.addAttribute("user", user);
        model.addAttribute("post", post);
        model.addAttribute("menus", menus);
        model.addAttribute("currentMenuName", currentMenu != null ? currentMenu.getName() : slug);
        model.addAttribute("currentMenuUrl", currentMenu != null ? currentMenu.getUrl() : "/" + slug);
        return "public/post-detail";
    }
}