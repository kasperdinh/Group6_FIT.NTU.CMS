package group6.fit_ntu_cms.controllers;
import group6.fit_ntu_cms.models.EventModel;
import group6.fit_ntu_cms.models.Role;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.services.EventService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Controller
public class EventController {
  private final HttpSession httpSession;
  private final GlobalController globalController;

  public EventController(HttpSession httpSession, GlobalController globalController) {
    this.httpSession = httpSession;
    this.globalController = globalController;
  }

  @Autowired
  private EventService eventService;

  @GetMapping("/events")
  public String getAllEvents(Model model, HttpSession session) {
    UsersModel user = (UsersModel) httpSession.getAttribute("user");
    if (user == null) {
      return "redirect:/access-denied";
    } else if (globalController.isUserRole()) {
      return "redirect:/access-denied";
    }
    model.addAttribute("user", user);
    model.addAttribute("events", eventService.getAllEvents());
    model.addAttribute("event", new EventModel());
    return "event/events";
  }

  @PostMapping("/events")
  public String addEvent(
          @Valid @ModelAttribute("event") EventModel event,
          BindingResult result,
          @RequestParam("imageFile") MultipartFile imageFile,
          @RequestParam("file") MultipartFile filePath,
          HttpSession session,
          Model model) throws IOException {
    // Kiểm tra người dùng
    UsersModel user = (UsersModel) session.getAttribute("user");
    if (user == null) {
      model.addAttribute("events", eventService.getAllEvents());
      model.addAttribute("event", event);
      model.addAttribute("errorMessage", "Vui lòng đăng nhập để thêm sự kiện.");
      return "event/events";
    }
    // Kiểm tra lỗi xác thực
    if (result.hasErrors()) {
      model.addAttribute("events", eventService.getAllEvents());
      model.addAttribute("event", event);
      model.addAttribute("errorMessage", "Vui lòng sửa các lỗi trong biểu mẫu.");
      return "event/events";
    }

    // Xử lý tải lên tệp ảnh
    if (imageFile != null && !imageFile.isEmpty()) {
      String uploadDir = new File("src/main/resources/static/uploads/img/").getAbsolutePath();
      String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
      File saveFile = new File(uploadDir, filename);
      saveFile.getParentFile().mkdirs();
      imageFile.transferTo(saveFile);
      event.setEventImage("/uploads/img/" + filename);
    }

    // Xử lý tải lên tệp tài liệu
    if (filePath != null && !filePath.isEmpty()) {
      String uploadDir = new File("src/main/resources/static/uploads/files/").getAbsolutePath();
      String filename = UUID.randomUUID() + "_" + filePath.getOriginalFilename();
      File saveFile = new File(uploadDir, filename);
      saveFile.getParentFile().mkdirs();
      filePath.transferTo(saveFile);
      event.setFilePath("/uploads/files/" + filename);
    }

    // Lưu sự kiện
    try {
      eventService.saveEvent(event, session);
      model.addAttribute("successMessage", "Sự kiện đã được thêm thành công!");
    } catch (IllegalStateException e) {
      model.addAttribute("events", eventService.getAllEvents());
      model.addAttribute("event", event);
      model.addAttribute("errorMessage", e.getMessage());
      return "event/events";
    }

    return "redirect:/events";
  }

  @PostMapping("/editEvents")
  public String editEvent(
          @ModelAttribute EventModel event,
          HttpSession session,
          @RequestParam("imageFile") MultipartFile imageFile,
          @RequestParam(value = "existingImage", required = false) String existingImage,
          @RequestParam(value = "existingFilePath", required = false) String existingFilePath,
          @RequestParam("file") MultipartFile filePath) throws IOException {

    if (imageFile != null && !imageFile.isEmpty()) {
      String uploadDir = new File("src/main/resources/static/uploads/img/").getAbsolutePath();
      String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
      File saveFile = new File(uploadDir, filename);
      saveFile.getParentFile().mkdirs();
      imageFile.transferTo(saveFile);

      if (existingImage != null && !existingImage.isEmpty()) {
        String oldImagePath = new File("src/main/resources/static" + existingImage).getAbsolutePath();
        File oldImageFile = new File(oldImagePath);
        if (oldImageFile.exists()) {
          oldImageFile.delete();
        }
      }
      event.setEventImage("/uploads/img/" + filename);
    } else {
      event.setEventImage(existingImage);
    }

    if (filePath != null && !filePath.isEmpty()) {
      String uploadDir = new File("src/main/resources/static/uploads/files/").getAbsolutePath();
      String filename = UUID.randomUUID() + "_" + filePath.getOriginalFilename();
      File saveFile = new File(uploadDir, filename);
      saveFile.getParentFile().mkdirs();
      filePath.transferTo(saveFile);

      if (existingFilePath != null && !existingFilePath.isEmpty()) {
        String oldFilePath = new File("src/main/resources/static" + existingFilePath).getAbsolutePath();
        File oldFile = new File(oldFilePath);
        if (oldFile.exists()) {
          oldFile.delete();
        }
      }

      event.setFilePath("/uploads/files/" + filename);
    } else{
      event.setFilePath(existingFilePath);
    }

    eventService.saveEvent(event, session);
    return "redirect:/events";
  }

  @PostMapping("/deleteEvent")
  public String removeEvent(@RequestParam("eventId") Long eventId) {
    EventModel event = eventService.getEventById(eventId);
    if (event != null) {
      if (event.getEventImage() != null) {
        String imagePath = new File("src/main/resources/static" + event.getEventImage()).getAbsolutePath();
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
          imageFile.delete();
        }
      }
      if (event.getFilePath() != null) {
        String filePath = new File("src/main/resources/static" + event.getFilePath()).getAbsolutePath();
        File file = new File(filePath);
        if (file.exists()) {
          file.delete();
        }
      }
      eventService.deleteEvent(eventId);
    }
    return "redirect:/events";
  }

  @GetMapping("/events/{id}")
  public String getEventById(@PathVariable Long id, Model model, HttpSession session) {
    UsersModel user = (UsersModel) session.getAttribute("user");
    if (user == null || globalController.isUserRole()) {
      return "redirect:/access-denied";
    }
    EventModel event = eventService.getEventById(id);
    if (event == null) {
      model.addAttribute("errorMessage", "Sự kiện không tồn tại.");
      model.addAttribute("events", eventService.getAllEvents());
      model.addAttribute("event", new EventModel());
      return "event/events";
    }
    model.addAttribute("user", user);
    model.addAttribute("event", event);
    return "event/event-detail";
  }
  @GetMapping("/api/events/{id}")
  @ResponseBody
  public EventModel getEventByIdJson(@PathVariable Long id, HttpSession session) {
    UsersModel user = (UsersModel) session.getAttribute("user");
    if (user == null || globalController.isUserRole()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không có quyền truy cập");
    }
    EventModel event = eventService.getEventById(id);
    if (event == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sự kiện không tồn tại");
    }
    return event;
  }
}