package group6.fit_ntu_cms.services;

import group6.fit_ntu_cms.models.PostModel;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.repositories.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    // Create or Update a Post
    @Transactional
    public PostModel savePost(PostModel post, UsersModel user) {
        if (post.getPostId() == null) { // New post
            post.setCreationDate(LocalDateTime.now());
            post.setUpdateDate(LocalDateTime.now());
            post.setStatus("Pending"); // Default status for new posts
        }
        post.setStatus(post.getStatus());
        post.setUpdateDate(LocalDateTime.now());
        post.setUser(user); // Set the user who created/updated the post
        return postRepository.save(post);
    }
    public List<PostModel> filterPosts(String status, Long category, String search) {
        List<PostModel> posts = postRepository.findAll();

        // Lọc theo status
        if (status != null && !status.isEmpty()) {
            posts = posts.stream()
                    .filter(post -> post.getStatus() != null && post.getStatus().equals(status))
                    .collect(Collectors.toList());
        }

        // Lọc theo category
        if (category != null) {
            posts = posts.stream()
                    .filter(post -> post.getCategory() != null && post.getCategory().getTittleId().equals(category))
                    .collect(Collectors.toList());
        }

        // Lọc theo search (tìm kiếm trong tiêu đề)
        if (search != null && !search.isEmpty()) {
            posts = posts.stream()
                    .filter(post -> post.getPostTitle() != null && post.getPostTitle().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return posts;
    }

    // Read a Post by ID
    public Optional<PostModel> getPostById(Long id) {
        return postRepository.findById(id);
    }

    // Read all Posts
    public List<PostModel> getAllPosts() {
        return postRepository.findAll();
    }

    public List<PostModel> getPostsByPageId(Long pageId) {
        return postRepository.findByPageId(pageId);
    }

    // Delete a Post by ID
    @Transactional
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}