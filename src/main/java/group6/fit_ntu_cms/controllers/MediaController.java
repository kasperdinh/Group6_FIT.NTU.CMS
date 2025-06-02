package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.EventModel;
import group6.fit_ntu_cms.models.MediaModel;
import group6.fit_ntu_cms.models.PostModel;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.repositories.EventRepository;
import group6.fit_ntu_cms.repositories.PostRepository;
import group6.fit_ntu_cms.services.MediaService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private EventRepository eventRepository;

    @GetMapping
    public String showMedia(Model model, @RequestParam(value = "search", required = false) String search, HttpSession session) {
        List<MediaModel> mediaFiles = mediaService.getAllMedia(search != null ? search : "");
        List<Map<String, Object>> mediaFilesWithStatus = mediaFiles.stream().map(media -> {
            Map<String, Object> mediaData = new HashMap<>();
            mediaData.put("media", media);
            File file = new File("src/main/resources/static" + media.getFileUpload());
            mediaData.put("exists", file.exists());
            return mediaData;
        }).collect(Collectors.toList());

        // Retrieve the logged-in user from the session
        UsersModel user = (UsersModel) session.getAttribute("user");
        if (user == null) {
            user = new UsersModel(); // Or redirect to login page if user is not logged in
        }

        model.addAttribute("user", user); // Add user to the model
        model.addAttribute("mediaFilesWithStatus", mediaFilesWithStatus);
        model.addAttribute("search", search != null ? search : "");
        return "media/medias";
    }
    @PostMapping("/add")
    @Transactional
    public String addMedia(@RequestParam("file") MultipartFile[] files, HttpSession session, RedirectAttributes redirectAttributes) {
        if (files == null || files.length == 0 || files[0].isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select at least one file to upload.");
            return "redirect:/media";
        }

        UsersModel user = (UsersModel) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please log in to upload files.");
            return "redirect:/media";
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            String savedFilePath = null;
            try {
                String uploadDir = new File("src/main/resources/static/uploads/files").getAbsolutePath();
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                File destFile = new File(uploadDir, fileName);
                destFile.getParentFile().mkdirs();
                file.transferTo(destFile);

                savedFilePath = "/uploads/files/" + fileName;

                MediaModel media = new MediaModel();
                mediaService.saveMedia(media,user,savedFilePath);
            } catch (IOException e) {
                if (savedFilePath != null) {
                    File savedFile = new File("src/main/resources/static" + savedFilePath);
                    if (savedFile.exists()) {
                        savedFile.delete();
                    }
                }
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload file: " + file.getOriginalFilename() + ". Error: " + e.getMessage());
                continue;
            }
        }

        redirectAttributes.addFlashAttribute("successMessage", "Files uploaded successfully!");
        return "redirect:/media";
    }

    @PostMapping("/delete/{id}")
    @Transactional
    public String deleteMedia(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            MediaModel media = mediaService.getMediaById(id)
                    .orElseThrow(() -> new RuntimeException("Media not found with ID: " + id));
            String filePath = media.getFileUpload(); // Giả sử fileUpload chứa đường dẫn tương đối (ví dụ: /uploads/filename.jpg)
            String absoluteFilePath = "src/main/resources/static" + filePath;
            File file = new File(absoluteFilePath);

            // Kiểm tra xem file có được sử dụng bởi Post hoặc Event không
            Optional<PostModel> post = postRepository.findByFilePath(filePath);
            Optional<PostModel> postImage = postRepository.findByPostImage(filePath); // Kiểm tra postImage
            Optional<EventModel> event = eventRepository.findByFilePath(filePath);
            Optional<EventModel> eventImage = eventRepository.findByEventImage(filePath); // Kiểm tra eventImage

            if (post.isPresent() || postImage.isPresent() || event.isPresent() || eventImage.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete file. It is being used by a Post or Event.");
                return "redirect:/media";
            }

            // Nếu file không được sử dụng, xóa file và bản ghi
            if (file.exists()) {
                if (!file.delete()) {
                    throw new RuntimeException("Failed to delete file from disk: " + filePath);
                }
            }
            mediaService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "File deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete file: " + e.getMessage());
        }
        return "redirect:/media";
    }
}