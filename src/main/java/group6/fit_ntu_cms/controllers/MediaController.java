package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.MediaModel;
import group6.fit_ntu_cms.models.UsersModel;
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
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @GetMapping
    public String showMedia(Model model, @RequestParam(value = "search", required = false) String search) {
        model.addAttribute("mediaFiles", mediaService.getAllMedia(search));
        model.addAttribute("search", search != null ? search : "");
        return "media/medias";
    }

    @PostMapping("/add")
    @Transactional
    public String addMedia(@RequestParam("file") MultipartFile file, HttpSession session, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/media";
        }

        String savedFilePath = null;
        try {
            String uploadDir = new File("src/main/resources/static/uploads/").getAbsolutePath();
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File destFile = new File(uploadDir, fileName);
            destFile.getParentFile().mkdirs();
            file.transferTo(destFile);

            savedFilePath = "/uploads/" + fileName;

            MediaModel media = new MediaModel();
            UsersModel user = (UsersModel) session.getAttribute("user");
            if (user == null) {
                throw new RuntimeException("User not logged in.");
            }
            mediaService.saveMedia(media,user,savedFilePath);

            redirectAttributes.addFlashAttribute("successMessage", "File uploaded successfully!");
        } catch (IOException e) {
            if (savedFilePath != null) {
                File savedFile = new File("src/main/resources/static" + savedFilePath);
                if (savedFile.exists()) {
                    savedFile.delete();
                }
            }
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload file: " + e.getMessage());
            return "redirect:/media";
        } catch (Exception e) {
            if (savedFilePath != null) {
                File savedFile = new File("src/main/resources/static" + savedFilePath);
                if (savedFile.exists()) {
                    savedFile.delete();
                }
            }
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to save media: " + e.getMessage());
            throw e; // Đảm bảo rollback giao dịch
        }
        return "redirect:/media";
    }

    @PostMapping("/delete/{id}")
    @Transactional
    public String deleteMedia(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            MediaModel media = mediaService.getMediaById(id)
                    .orElseThrow(() -> new RuntimeException("Media not found with ID: " + id));
            String filePath = "src/main/resources/static" + media.getFileUpload();
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            mediaService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "File deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete file: " + e.getMessage());
        }
        return "redirect:/media";
    }
}