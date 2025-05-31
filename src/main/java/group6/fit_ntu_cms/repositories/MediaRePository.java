package group6.fit_ntu_cms.repositories;

import group6.fit_ntu_cms.models.MediaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRePository extends JpaRepository<MediaModel,Long> {
    List<MediaModel> findByFileUploadContainingIgnoreCase(String fileUpload);

    void deleteByFileUpload(String fileUpload);
}
