package group6.fit_ntu_cms.services;

import group6.fit_ntu_cms.models.CategoryModel;
import group6.fit_ntu_cms.repositories.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Create or Update a Tittle (Category)
    @Transactional
    public CategoryModel saveTittle(CategoryModel tittle) {
        return categoryRepository.save(tittle);
    }

    // Read a Tittle by ID
    public Optional<CategoryModel> getTittleById(Long id) {
        return categoryRepository.findById(id);
    }

    // Read all Tittles
    public List<CategoryModel> getAllTittles() {
        return categoryRepository.findAll();
    }

    // Delete a Tittle by ID
    @Transactional
    public void deleteTittle(Long id) {
        categoryRepository.deleteById(id);
    }
    public List<CategoryModel> getTittles(String search, int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        if (search != null && !search.isEmpty()) {
            return categoryRepository.findByTitleNameContainingIgnoreCase(search, pageRequest).getContent();
        }
        return categoryRepository.findAll(pageRequest).getContent();
    }

    public int countTittles(String search) {
        if (search != null && !search.isEmpty()) {
            return (int) categoryRepository.countByTitleNameContainingIgnoreCase(search);
        }
        return (int) categoryRepository.count();
    }
}