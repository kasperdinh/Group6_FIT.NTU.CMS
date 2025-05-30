package group6.fit_ntu_cms.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "menus")
public class MenuModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @ManyToOne
  @JoinColumn(name = "page_id", nullable = false)
  private PageModel page;

  @Column(name = "menu_order")
  private int order;

  @Column(name = "url")
  private String url;
}