package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.EventModel;
import group6.fit_ntu_cms.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
public class EventController {

  @Autowired
  private EventService eventService;

  @GetMapping("/events")
  public String getAllEvents(Model model) {
    List<EventModel> events = eventService.getAllEvents();
    model.addAttribute("events", events);
    model.addAttribute("event", new EventModel());
    return "event/events";
  }

  @PostMapping("/events")
  public String addEvent(
          @ModelAttribute EventModel event,
          BindingResult result,
          @RequestParam("imageFile") MultipartFile imageFile,
          @RequestParam("filePath") MultipartFile filePath,
          Model model) throws IOException {
    if (result.hasErrors()) {
      model.addAttribute("events", eventService.getAllEvents());
      return "event/events";
    }

    if (imageFile != null && !imageFile.isEmpty()) {
      String uploadDir = new File("src/main/resources/static/img/").getAbsolutePath();
      String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
      File saveFile = new File(uploadDir, filename);
      saveFile.getParentFile().mkdirs();
      imageFile.transferTo(saveFile);
      event.setEventImage("/img/" + filename);
    }

    if (filePath != null && !filePath.isEmpty()) {
      String uploadDir = new File("src/main/resources/static/uploads/").getAbsolutePath();
      String filename = UUID.randomUUID() + "_" + filePath.getOriginalFilename();
      File saveFile = new File(uploadDir, filename);
      saveFile.getParentFile().mkdirs();
      filePath.transferTo(saveFile);
      event.setFilePath("/uploads/" + filename);
    }

    eventService.saveEvent(event);
    return "redirect:/events";
  }

  @PostMapping("/editEvents")
  public String editEvent(
          @ModelAttribute EventModel event,
          BindingResult result,
          @RequestParam("imageFile") MultipartFile imageFile,
          @RequestParam(value = "existingImage", required = false) String existingImage,
          @RequestParam("filePath") MultipartFile filePath) throws IOException {
    if (result.hasErrors()) {
      return "event/events";
    }

    if (imageFile != null && !imageFile.isEmpty()) {
      String uploadDir = new File("src/main/resources/static/img/").getAbsolutePath();
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

      event.setEventImage("/img/" + filename);
    } else {
      event.setEventImage(existingImage);
    }

    if (filePath != null && !filePath.isEmpty()) {
      String uploadDir = new File("src/main/resources/static/uploads/").getAbsolutePath();
      String filename = UUID.randomUUID() + "_" + filePath.getOriginalFilename();
      File saveFile = new File(uploadDir, filename);
      saveFile.getParentFile().mkdirs();
      filePath.transferTo(saveFile);

      if (event.getFilePath() != null) {
        String oldFilePath = new File("src/main/resources/static" + event.getFilePath()).getAbsolutePath();
        File oldFile = new File(oldFilePath);
        if (oldFile.exists()) {
          oldFile.delete();
        }
      }

      event.setFilePath("/uploads/" + filename);
    }

    eventService.saveEvent(event);
    return "redirect:/events";
  }

  @PostMapping("/deleteEvent")
  public String removeEvent(@RequestParam("eventId") Long eventId) {
    EventModel event = eventService.getEventById(eventId).orElse(null);
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
  @ResponseBody
  public EventModel getEventById(@PathVariable Long id) {
    return eventService.getEventById(id).orElseThrow(() -> new RuntimeException("Event not found"));
  }
}