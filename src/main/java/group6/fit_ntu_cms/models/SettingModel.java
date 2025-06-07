package group6.fit_ntu_cms.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Text;

@Setter
@Getter
@Entity
@Table(name="setting")
public class SettingModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private String siteName;
    private String logoUrl;
    private String footerName;
    @Column(columnDefinition = "TEXT")
    private String footerText;
    private String footerFacebookUrl;
    private String footerTwitterUrl;
    private String footerYoutubeUrl;
    private String footerInstagramUrl;
    private String footerPhone;
    private String footerEmail;
}
