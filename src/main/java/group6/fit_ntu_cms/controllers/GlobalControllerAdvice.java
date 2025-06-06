package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.NotifyModel;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.services.NotifyService;
import group6.fit_ntu_cms.services.SettingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private NotifyService notifyService;
    @Autowired
    private SettingService settingService;

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpSession session) {
        UsersModel user = (UsersModel) session.getAttribute("user");
        if (user != null) {
            List<NotifyModel> notifications = notifyService.getNotificationsByUser(user);
            List<NotifyModel> unreadNotifications = notifyService.getUnreadNotificationsByUser(user);
            if (notifications != null && !notifications.isEmpty()) {
                notifications.sort((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()));
            }
            model.addAttribute("notifications", notifications != null ? notifications : new ArrayList<>());
            model.addAttribute("unreadNotifications", unreadNotifications != null ? unreadNotifications : new ArrayList<>());
            System.out.println("Notifications added to model: " + notifications.size());
            System.out.println("Unread notifications added to model: " + unreadNotifications.size());
        } else {
            model.addAttribute("notifications", new ArrayList<>());
            model.addAttribute("unreadNotifications", new ArrayList<>());
        }
        model.addAttribute("setting",settingService.getSetting());
    }
}