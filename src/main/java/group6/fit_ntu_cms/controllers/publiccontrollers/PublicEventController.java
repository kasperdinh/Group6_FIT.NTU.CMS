package group6.fit_ntu_cms.controllers.publiccontrollers;

import group6.fit_ntu_cms.models.EventModel;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.repositories.EventRepository;
import group6.fit_ntu_cms.repositories.UsersRepository;
import group6.fit_ntu_cms.services.MenuService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class PublicEventController {
    private final HttpSession httpSession;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MenuService menuService;

    public PublicEventController(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @GetMapping("/event/{id}")
    public String showEventDetail(@PathVariable Long id, Model model) {
        EventModel event = eventRepository.getReferenceById(id);
        UsersModel user = (UsersModel) httpSession.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("event", event);
        model.addAttribute("menus", menuService.getAllMenus());
        return "public/event-detail";
    }
}
