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
@Table(name = "posts")
public class PostModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaBaiViet")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaChuyenMuc")
    @JsonBackReference
    private CategoryModel category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiDung", nullable = false)
    @JsonBackReference
    private UsersModel user;

    @NotBlank(message = "Post title cannot be empty")
    @Size(max = 100, message = "Post title must be less than 100 characters")
    @Column(name = "TenBaiViet", length = 100)
    private String postTitle;

    @Size(max = 255, message = "Summary must be less than 255 characters")
    @Column(name = "TomTat", length = 255)
    private String summary;

    @Column(name = "NoiDung", columnDefinition = "TEXT")
    private String content;

    @Size(max = 255, message = "File path must be less than 255 characters")
    @Column(name = "DuongDanFile", length = 255)
    private String filePath;
    @Size(max = 255, message = "File path must be less than 255 characters")
    @Column(name = "DuongDanAnh", length = 255,nullable = true)
    private String postImage;


    @Column(name = "NgayTao")
    private LocalDateTime creationDate;

    @Column(name = "NgayCapNhat")
    private LocalDateTime updateDate;


    @Size(max = 100, message = "Status must be less than 100 characters")
    @Column(name = "TrangThai", length = 100)
    private String status;

    @ManyToOne
    @JoinColumn(name = "page_id")
    @JsonBackReference
    private PageModel page;
}