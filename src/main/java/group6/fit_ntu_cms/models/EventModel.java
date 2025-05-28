package group6.fit_ntu_cms.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "events")
public class EventModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long eventId;
  @ManyToOne
  @JoinColumn(name ="user_id")
  private UsersModel user;
  private String eventName;
  private String summary;
  private String eventDescription;
  private String eventLocation;
  private String filePath;
  private String eventImage;
  private LocalDateTime createDate;
  private LocalDateTime beginDate;
  private LocalDateTime finishDate;
}