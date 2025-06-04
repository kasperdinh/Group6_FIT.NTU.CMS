package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.services.NotifyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/notifications")
public class NotifyController {

    @Autowired
    private NotifyService notifyService;

    @PostMapping("/clearAll")
    @ResponseBody
    public ResponseEntity<Void> clearAllNotifications(HttpSession session) {
        try {
            UsersModel user = (UsersModel) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            notifyService.clearAllNotifications(user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}