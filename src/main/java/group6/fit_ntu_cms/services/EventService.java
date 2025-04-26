package group6.fit_ntu_cms.services;

import group6.fit_ntu_cms.models.EventModel;
import group6.fit_ntu_cms.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
  @Autowired
  private EventRepository eventRepository;

  public List<EventModel> getAllEvents() {
    return eventRepository.findAll();
  }
  // Hàm tạo event mới
  public EventModel createEvent(EventModel event) {
    event.setEventId(generateNextEventId());
    return eventRepository.save(event);
  }
  // hàm sinh id mới nhất khi thêm mới event
  public String generateNextEventId() {
    List<EventModel> allEvents = eventRepository.findAll();
    if (allEvents.isEmpty()) {
      return "EVT001";
    }
    // Lấy eventId có số lớn nhất
    String maxId = allEvents.stream()
            .map(EventModel::getEventId)
            .max(String::compareTo)
            .orElse("EVT000");

    // Tách phần số ra khỏi "EVT"
    int numericPart = Integer.parseInt(maxId.substring(3));
    numericPart++; // Tăng lên 1

    // Trả lại dạng EVT###
    return String.format("EVT%03d", numericPart);
  }

}
