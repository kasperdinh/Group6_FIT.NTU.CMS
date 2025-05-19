package group6.fit_ntu_cms.services;

import group6.fit_ntu_cms.models.EventModel;
import group6.fit_ntu_cms.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
  @Autowired
  private EventRepository eventRepository;

  public List<EventModel> getAllEvents() {
    return eventRepository.findAll();
  }

  public void saveEvent(EventModel event) {
    event.setCreateDate(LocalDateTime.now());
    eventRepository.save(event);
  }

  public void deleteEvent(Long eventId) {
    eventRepository.deleteById(eventId);
  }

  public Optional<EventModel> getEventById(Long eventId) {
    return eventRepository.findById(eventId);
  }
}