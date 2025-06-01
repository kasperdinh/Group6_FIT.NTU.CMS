package group6.fit_ntu_cms.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
  @JoinColumn(name = "user_id")
  private UsersModel user; // Remove @NotNull

  @NotBlank(message = "Tên sự kiện không được để trống")
  @Size(max = 100, message = "Tên sự kiện không được vượt quá 100 ký tự")
  private String eventName;

  @NotBlank(message = "Tóm tắt không được để trống")
  @Size(max = 255, message = "Tóm tắt không được vượt quá 255 ký tự")
  private String summary;

  private String eventDescription;

  @NotBlank(message = "Địa điểm không được để trống")
  private String eventLocation;

  private String filePath;

  private String eventImage;

  private LocalDateTime createDate; // Remove @NotNull

  @NotNull(message = "Thời gian bắt đầu không được để trống")
  @Column(name = "begin_date")
  private LocalDateTime beginDate;

  @NotNull(message = "Thời gian kết thúc không được để trống")
  @Column(name = "finish_date")
  private LocalDateTime finishDate;
}