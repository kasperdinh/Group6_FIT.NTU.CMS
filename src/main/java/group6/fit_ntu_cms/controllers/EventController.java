package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.EventModel;
import group6.fit_ntu_cms.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class EventController {
  @Autowired
  private EventService eventService; // ✅ Đã thêm @Autowired


  @PostMapping("/events")
  public String addEvent(@ModelAttribute EventModel event) {
    eventService.createEvent(event); // ✅ Gọi đúng service đã khai báo
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

    return "Event/events"; // Trả về trang Event/events
  }


}
