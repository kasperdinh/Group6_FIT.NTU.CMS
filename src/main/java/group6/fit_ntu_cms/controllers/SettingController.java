package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.MediaModel;
import group6.fit_ntu_cms.models.SettingModel;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.services.MediaService;
import group6.fit_ntu_cms.services.SettingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/setting")
public class SettingController {
    private final GlobalController globalController;
    private final HttpSession httpSession;

    public SettingController(HttpSession httpSession, GlobalController globalController) {
        this.globalController = globalController;
        this.httpSession = httpSession;
    }

    @Autowired
    private SettingService settingService;
    @Autowired
    private MediaService mediaService;
    @GetMapping
    public String showSettingPage(Model model) {
        UsersModel user = (UsersModel) httpSession.getAttribute("user");
        if (user == null) {
            return "redirect:/access-denied";
        } else if (globalController.isUserRole()) {
            return "redirect:/access-denied";
        }
        SettingModel setting = settingService.getSetting();
        model.addAttribute("setting", setting);
        model.addAttribute("user", user);
        return "/setting"; // Trả về setting.html
    }
    @PostMapping
    public String updateSetting(
            @RequestParam("siteName") String siteName,
            @RequestParam(value = "logo", required = false) MultipartFile logoFile,
            @RequestParam(value = "existingLogo", required = false) String existingLogo,
            HttpSession session,
            Model model) throws IOException {

        UsersModel user = (UsersModel) session.getAttribute("user");
        if (user == null) {
            model.addAttribute("errorMessage", "User not logged in.");
            return "redirect:/login";
        }

        // Lấy bản ghi cài đặt hiện tại
        SettingModel setting = settingService.getSetting();

        // Cập nhật siteName
        setting.setSiteName(siteName);

        // Xử lý upload logo
        if (logoFile != null && !logoFile.isEmpty()) {
            // Đường dẫn lưu file
            String uploadDir = new File("src/main/resources/static/uploads/img/").getAbsolutePath();
            String filename = UUID.randomUUID() + "_" + logoFile.getOriginalFilename();
            File saveFile = new File(uploadDir, filename);
            saveFile.getParentFile().mkdirs();
            logoFile.transferTo(saveFile);

            // Tạo đường dẫn động để lưu vào database
            String logoPath = "/uploads/img/" + filename;
            MediaModel media = new MediaModel();
            mediaService.saveMedia(media,user,logoPath);
            setting.setLogoUrl(logoPath);

            // Xóa logo cũ nếu tồn tại
            if (existingLogo != null && !existingLogo.isEmpty()) {
                String oldLogoPath = new File("src/main/resources/static" + existingLogo).getAbsolutePath();
                File oldLogoFile = new File(oldLogoPath);
                if (oldLogoFile.exists()) {
                    oldLogoFile.delete();
                }
            }
        } else {
            // Giữ nguyên logo hiện tại nếu không upload logo mới
            setting.setLogoUrl(existingLogo);
        }

        // Lưu cài đặt
        try {
            settingService.updateSetting(setting);
            model.addAttribute("successMessage", "Settings updated successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to update settings.");
            return "redirect:/dashboard";
        }

        return "redirect:/dashboard"; // Chuyển hướng về dashboard sau khi lưu
    }
}