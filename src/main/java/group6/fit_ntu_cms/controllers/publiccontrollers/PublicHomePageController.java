package group6.fit_ntu_cms.controllers.publiccontrollers;

import group6.fit_ntu_cms.models.PostModel;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.services.MenuService;
import group6.fit_ntu_cms.services.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PublicHomePageController {

    private final HttpSession httpSession;

    public PublicHomePageController(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Autowired
    private PostService postService;

    @Autowired
    private MenuService menuService;

    @GetMapping("/trang-chu")
    public String publicHomePage(Model model) {
        List<PostModel> newsList = postService.getAllPosts();
        List<PostModel> subNewsList = new ArrayList<>();

        if (newsList.size() > 1) {
            subNewsList = newsList.subList(1, Math.min(5, newsList.size()));
        }

        model.addAttribute("firstNews", newsList.get(0));
        model.addAttribute("subNewsList", subNewsList);
        UsersModel user = (UsersModel) httpSession.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("menus", menuService.getAllMenus());

        return "public/home-page";
    }
}
