package group6.fit_ntu_cms.models;

import jakarta.persistence.*;
import jdk.jfr.Name;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter

@Entity
@Table(name = "users")
public class UsersModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "otp")
    private String otp;

    @Column(name = "otp_requested_time")
    private LocalDateTime otpRequestedTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

}
