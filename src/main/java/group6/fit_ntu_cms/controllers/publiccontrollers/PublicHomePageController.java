package group6.fit_ntu_cms.controllers.publiccontrollers;

import group6.fit_ntu_cms.models.PostModel;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.services.EventService;
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
    private EventService eventService;

    @Autowired
    private MenuService menuService;

    @GetMapping("/trang-chu")
    public String publicHomePage(Model model) {
        List<PostModel> allPosts = postService.getAllPosts();

        List<PostModel> approvedPosts = allPosts.stream()
                .filter(post -> "Approved".equalsIgnoreCase(post.getStatus()))
                .toList();

        if (approvedPosts.isEmpty()) {
            model.addAttribute("firstNews", null);
            model.addAttribute("subNewsList", new ArrayList<>());
        } else {
            model.addAttribute("firstNews", approvedPosts.get(0));

            List<PostModel> subNewsList = new ArrayList<>();
            if (approvedPosts.size() > 1) {
                subNewsList = approvedPosts.subList(1, Math.min(8, approvedPosts.size()));
            }
            model.addAttribute("subNewsList", subNewsList);
        }

        UsersModel user = (UsersModel) httpSession.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("events", eventService.getAllEvents());
        model.addAttribute("menus", menuService.getAllMenus());

        return "public/home-page";
    }

}
