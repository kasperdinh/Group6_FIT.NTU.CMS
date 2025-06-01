package group6.fit_ntu_cms.services;

import group6.fit_ntu_cms.models.MediaModel;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.repositories.MediaRePository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MediaService {
    @Autowired
    MediaRePository mediaRePository;
    public MediaModel saveMedia(MediaModel media, UsersModel user, String filePath){
        media.setUser(user);
        media.setUploadDate(LocalDateTime.now());
        media.setFileUpload(filePath);
        return mediaRePository.save(media);
    }
    public void deleteMedia(String fileUpload){
         mediaRePository.deleteByFileUpload(fileUpload);
    }
    public void deleteById(Long id){
        mediaRePository.deleteById(id);
    }
    public List<MediaModel> getAllMedia(String search) {
        if (search != null && !search.isEmpty()) {
            return mediaRePository.findByFileUploadContainingIgnoreCase(search);
        }
        return mediaRePository.findAll();
    }
    public Optional<MediaModel> getMediaById(Long id){
        return mediaRePository.findById(id);
    }
}
