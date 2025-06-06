package group6.fit_ntu_cms.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "bai_viet")
public class PostModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_bai_viet")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_chuyen_muc")
    @JsonBackReference
    private CategoryModel category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    @JsonBackReference
    private UsersModel user;

    @NotBlank(message = "Post title cannot be empty")
    @Size(max = 100, message = "Post title must be less than 100 characters")
    @Column(name = "ten_bai_viet", length = 100)
    private String postTitle;

    @Size(max = 255, message = "Summary must be less than 255 characters")
    @Column(name = "tom_tat", length = 255)
    private String summary;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String content;

    @Size(max = 255, message = "File path must be less than 255 characters")
    @Column(name = "duong_dan_file", length = 255)
    private String filePath;
    @Size(max = 255, message = "File path must be less than 255 characters")
    @Column(name = "duong_dan_anh", length = 255,nullable = true)
    private String postImage;


    @Column(name = "ngay_tao")
    private LocalDateTime creationDate;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime updateDate;


    @Size(max = 100, message = "Status must be less than 100 characters")
    @Column(name = "trang_thai", length = 100)
    private String status;

    @ManyToOne
    @JoinColumn(name = "page_id")
    @JsonBackReference
    private PageModel page;
}