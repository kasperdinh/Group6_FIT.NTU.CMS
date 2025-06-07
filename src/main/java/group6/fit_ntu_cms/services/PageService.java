package group6.fit_ntu_cms.services;

import group6.fit_ntu_cms.repositories.PageRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import group6.fit_ntu_cms.models.PageModel;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

@Service
public class PageService {

  @Autowired
  private PageRepository pageRepository;

  public PageModel getPageById(Long id) {
    return pageRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy trang với ID: " + id));
  }

  public PageModel createPage(String title, String content) {
    PageModel page = new PageModel();
    page.setTitle(title);
    page.setSlug(generateSlug(title));
    page.setContent(content);
    return pageRepository.save(page);
  }

  public List<PageModel> getAllPages() {
    return pageRepository.findAll();
  }

  private String generateSlug(String title) {
    String normalized = Normalizer.normalize(title, Normalizer.Form.NFD)
        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    return normalized.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
  }

  public Optional<PageModel> getPageBySlug(String slug) {
    return pageRepository.findBySlug(slug);
  }

  public void deletePage(Long id) {
    PageModel page = pageRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy trang để xóa."));
    if (page.isDefault()) {
      throw new IllegalStateException("Không thể xóa trang được đặt làm mặc định.");
    }
    pageRepository.deleteById(id);
  }

  public PageModel updatePage(Long id, String title, String content) {
    PageModel page = pageRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy trang để cập nhật."));
    page.setTitle(title);
    page.setSlug(generateSlug(title));
    page.setContent(content);
    return pageRepository.save(page);
  }

  public void setDefaultPage(Long id) {
      List<PageModel> pages = pageRepository.findAll();
      for (PageModel page : pages) {
          if (page.isDefault()) {
              page.setDefault(false); // Unset the current default page
              pageRepository.save(page);
          }
      }
      PageModel newDefaultPage = pageRepository.findById(id)
          .orElseThrow(() -> new RuntimeException("Không tìm thấy trang để đặt làm mặc định."));
      newDefaultPage.setDefault(true); // Set the new default page
      pageRepository.save(newDefaultPage);
  }

  public PageModel getDefaultPage() {
    return pageRepository.findAll()
        .stream()
        .filter(PageModel::isDefault)
        .findFirst()
        .orElse(null);
  }

  public boolean clearDefaultPage() {
    List<PageModel> defaultPages = pageRepository.findAll()
            .stream()
            .filter(PageModel::isDefault)
            .toList();

    if (defaultPages.isEmpty()) return false;

    defaultPages.forEach(p -> {
      p.setDefault(false);
      pageRepository.save(p);
    });

    return true;
  }
}