package group6.fit_ntu_cms.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Tittle")
public class TittleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaChuyenMuc")
    private Long tittleId;

    @NotBlank(message = "Tên chuyên mục không được để trống")
    @Size(max = 100, message = "Tên chuyên mục phải dưới 100 ký tự")
    @Column(name = "TenChuyenMuc", length = 100)
    private String titleName;

    // One-to-Many relationship with BaiViet
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostModel> posts;
}