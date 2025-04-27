package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.EventModel;
import group6.fit_ntu_cms.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
public class EventController {

  @Autowired
  private EventService eventService;

  @PostMapping("/events")
  public String addEvent(
          @ModelAttribute EventModel event,
          @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

    if (imageFile != null && !imageFile.isEmpty()) {
      // Lấy đường dẫn tuyệt đối thực sự của dự án
      String uploadDir = new File("src/main/resources/static/img/").getAbsolutePath();

      String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
      File saveFile = new File(uploadDir, filename);

      saveFile.getParentFile().mkdirs(); // Tạo thư mục nếu chưa có
      imageFile.transferTo(saveFile);

      event.setEventImage("/img/" + filename); // Đường link để truy cập ảnh
    }

    eventService.saveEvent(event);
    return "redirect:/events";
  }

  @GetMapping("/events")
  public String getAllEvents(Model model) {
    List<EventModel> events = eventService.getAllEvents();
    model.addAttribute("events", events);

    // Tạo một event mới với eventId tự sinh
    EventModel newEvent = new EventModel();
    newEvent.setEventId(eventService.generateNextEventId());
    model.addAttribute("event", newEvent);

    return "Event/events";
  }
  @PostMapping("/deleteEvent")
  public String removeEvent(@RequestParam("eventId") String eventId) {
    // Tìm sự kiện theo eventId
    EventModel event = eventService.getAllEvents().stream()
            .filter(e -> e.getEventId().equals(eventId))
            .findFirst()
            .orElse(null);

    if (event != null) {
      // Xóa file ảnh nếu có
      if (event.getEventImage() != null) {
        String imagePath = new File("src/main/resources/static" + event.getEventImage()).getAbsolutePath();
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
          imageFile.delete();
        }
      }
      // Xóa sự kiện
      eventService.deleteEvent(event);
    }
    return "redirect:/events";
  }
}
