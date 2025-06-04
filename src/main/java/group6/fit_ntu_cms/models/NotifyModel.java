package group6.fit_ntu_cms.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

@Entity
@Table(name = "Notify")
public class NotifyModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notifyId;

    private boolean status;

    @Column(name = "content", length = 255)
    @Size(max = 255, message = "Notification content must be less than 255 characters")
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private UsersModel user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
