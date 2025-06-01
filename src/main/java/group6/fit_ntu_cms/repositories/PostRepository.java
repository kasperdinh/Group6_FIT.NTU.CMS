package group6.fit_ntu_cms.repositories;

import group6.fit_ntu_cms.models.PostModel;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostModel, Long> {
    Optional<PostModel> findByFilePath(String filePath);
    Optional<PostModel> findByPostImage(@Size(max = 255, message = "File path must be less than 255 characters") String postImage);
}