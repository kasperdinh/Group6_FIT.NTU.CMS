package group6.fit_ntu_cms.controllers.publiccontrollers;

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

import java.util.Optional;

@Controller
public class PublicPostController {

    private final HttpSession httpSession;

    public PublicPostController(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MenuService menuService;

    @GetMapping("/post/{id}")
    public String getPostDetails(@PathVariable("id") Long postId, Model model) {
        PostModel post = postRepository.getReferenceById(postId);
        UsersModel user = (UsersModel) httpSession.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("post", post);
        model.addAttribute("menus", menuService.getAllMenus());
        return "public/post-detail";
    }
}

