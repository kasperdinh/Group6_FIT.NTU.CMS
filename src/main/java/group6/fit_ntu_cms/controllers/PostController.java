package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.*;
import group6.fit_ntu_cms.repositories.MediaRePository;
import group6.fit_ntu_cms.repositories.UsersRepository;
import group6.fit_ntu_cms.services.*;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
public class PostController {

    private final HttpSession httpSession;

    private final GlobalController globalController;

    public PostController(HttpSession httpSession, GlobalController globalController) {
        this.httpSession = httpSession;
        this.globalController = globalController;
    }

    @Autowired
    private PostService postService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private MediaService mediaService;
    @Autowired
    private PageService pageService;
    @Autowired
    private NotifyService notifyService;
    @Autowired
    private UsersRepository usersRepository;
    @GetMapping("/posts")
    public String showPosts(@RequestParam(required = false) String status,
                            @RequestParam(required = false) Long category,
                            @RequestParam(required = false) String search,
                            Model model) {
        UsersModel user = (UsersModel) httpSession.getAttribute("user");
        if (user == null) {
            return "redirect:/access-denied";
        } else if (globalController.isUserRole()) {
            return "redirect:/access-denied";
        }
        model.addAttribute("user", user);
        List<PostModel> posts = postService.filterPosts(status, category, search);
        model.addAttribute("posts", posts);
        model.addAttribute("post", new PostModel());
        model.addAttribute("allCategories", categoryService.getAllTittles());
        model.addAttribute("allPages",pageService.getAllPages());
        return "/post/posts";
    }

    @PostMapping("/post-add")
    public String addPost(
            @Valid @ModelAttribute("post") PostModel post,
            BindingResult result,
            @RequestParam("imgFile") MultipartFile imageFile,
            @RequestParam("file") MultipartFile filePath,
            HttpSession session,
            Model model) throws IOException {
        // Kiểm tra người dùng
        UsersModel user = (UsersModel) session.getAttribute("user");
        if (user == null) {
            model.addAttribute("posts", postService.getAllPosts());
            model.addAttribute("post", post);
            model.addAttribute("errorMessage", "Vui lòng đăng nhập để thêm bài viết.");
            return "post/posts";
        }
        Role role = (Role) httpSession.getAttribute("role");
        if (role == Role.USER) {
            return "redirect:/access-denied";
        }

        // Kiểm tra lỗi xác thực
        if (result.hasErrors()) {
            model.addAttribute("posts", postService.getAllPosts());
            model.addAttribute("post", post);
            model.addAttribute("errorMessage", "Vui lòng sửa các lỗi trong biểu mẫu.");
            return "post/posts";
        }

        // Xử lý tải lên tệp ảnh
        if (imageFile != null && !imageFile.isEmpty()) {

            String uploadDir = new File("src/main/resources/static/uploads/img/").getAbsolutePath();
            String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            File saveFile = new File(uploadDir, filename);
            saveFile.getParentFile().mkdirs();
            imageFile.transferTo(saveFile);
            String filePathStr = "/uploads/img/" + filename;
            post.setPostImage(filePathStr);
            MediaModel media = new MediaModel();
            mediaService.saveMedia(media,user,filePathStr);
        }

        // Xử lý tải lên tệp tài liệu
        if (filePath != null && !filePath.isEmpty()) {
            String uploadDir = new File("src/main/resources/static/uploads/files/").getAbsolutePath();
            String filename = UUID.randomUUID() + "_" + filePath.getOriginalFilename();
            File saveFile = new File(uploadDir, filename);
            saveFile.getParentFile().mkdirs();
            filePath.transferTo(saveFile);
            post.setFilePath("/uploads/files/" + filename);
            String filePathStr = "/uploads/files/" + filename;
            MediaModel media = new MediaModel();
            mediaService.saveMedia(media,user,filePathStr);
        }

        // Lưu sự kiện
        try {
            postService.savePost(post, user);
            String postName = post.getPostTitle();
            NotifyModel notify = new NotifyModel();
            notifyService.saveNotify(notify,user,"Bài viết vừa thêm của bạn: "+postName+" đang được phê duyệt");
            // Gửi thông báo cho tất cả ADMIN và MODERATOR
            List<UsersModel> admins = usersRepository.findByRole(Role.ADMIN);
            List<UsersModel> moderators = usersRepository.findByRole(Role.MODERATOR);
            admins.addAll(moderators); // Gộp danh sách ADMIN và MODERATOR
            notifyService.saveNotifyForUsers(admins, "Bạn có bài viết:" + postName + " cần được kiểm duyệt");
            model.addAttribute("successMessage", "Sự kiện đã được thêm thành công!");
        } catch (IllegalStateException e) {
            model.addAttribute("posts", postService.getAllPosts());
            model.addAttribute("post", post);
            model.addAttribute("errorMessage", e.getMessage());
            return "post/posts";
        }

        return "redirect:/posts";
    }

    @PostMapping("/deletePost")
    @Transactional
    public String removePost(@RequestParam(value = "postId", required = false) Long postId, Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        if (postId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Post ID is missing. Unable to delete the post.");
            return "redirect:/posts";
        }

        PostModel post = postService.getPostById(postId);
        if (post == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Post not found with ID: " + postId);
            return "redirect:/posts";
        }

        try {
            // Delete associated image file
            if (post.getPostImage() != null) {
                String imagePath = new File("src/main/resources/static" + post.getPostImage().replaceFirst("^/", "")).getAbsolutePath();
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    if (imageFile.delete()) {
                        System.out.println("Deleted image file: " + imagePath);
                    } else {
                        System.out.println("Failed to delete image file: " + imagePath);
                    }
                } else {
                    System.out.println("Image file does not exist: " + imagePath);
                }
                mediaService.deleteMedia(post.getPostImage());
            }

            // Delete associated document file
            if (post.getFilePath() != null) {
                String filePath = new File("src/main/resources/static" + post.getFilePath().replaceFirst("^/", "")).getAbsolutePath();
                File file = new File(filePath);
                if (file.exists()) {
                    if (file.delete()) {
                        System.out.println("Deleted file: " + filePath);
                    } else {
                        System.out.println("Failed to delete file: " + filePath);
                    }
                } else {
                    System.out.println("File does not exist: " + filePath);
                }
                mediaService.deleteMedia(post.getFilePath());
            }

            // Delete the post from the database
            UsersModel user = (UsersModel) session.getAttribute("user");
            NotifyModel notify = new NotifyModel();
            notifyService.saveNotify(notify,user,"Bạn đã xóa bài viết: "+post.getPostTitle()+" thành công");
            postService.deletePost(postId);
            redirectAttributes.addFlashAttribute("successMessage", "Post deleted successfully.");
        } catch (Exception e) {
            System.err.println("Error deleting post with ID " + postId + ": " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting post: " + e.getMessage());
        }

        return "redirect:/posts";
    }
    @PostMapping("/editPosts")
    public String editPost(
            @ModelAttribute PostModel post,
            HttpSession session,
            @RequestParam("imgFile") MultipartFile imageFile,
            @RequestParam(value = "existingImagePath", required = false) String existingImage,
            @RequestParam(value = "existingFilePath", required = false) String existingFilePath,
            @RequestParam("file") MultipartFile filePath, Model model) throws IOException {
        UsersModel user = (UsersModel) session.getAttribute("user");
        if (imageFile != null && !imageFile.isEmpty()) {
            String uploadDir = new File("src/main/resources/static/uploads/img/").getAbsolutePath();
            String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            File saveFile = new File(uploadDir, filename);
            saveFile.getParentFile().mkdirs();
            imageFile.transferTo(saveFile);
            MediaModel media = new MediaModel();
            String file = "/uploads/img/" + filename;
            mediaService.saveMedia(media,user,file);

            if (existingImage != null && !existingImage.isEmpty()) {
                String oldImagePath = new File("src/main/resources/static" + existingImage).getAbsolutePath();
                File oldImageFile = new File(oldImagePath);
                if (oldImageFile.exists()) {
                    oldImageFile.delete();
                }
            }
            post.setPostImage(file);
        } else {
            post.setPostImage(existingImage);
        }

        if (filePath != null && !filePath.isEmpty()) {
            String uploadDir = new File("src/main/resources/static/uploads/files/").getAbsolutePath();
            String filename = UUID.randomUUID() + "_" + filePath.getOriginalFilename();
            File saveFile = new File(uploadDir, filename);
            saveFile.getParentFile().mkdirs();
            filePath.transferTo(saveFile);
            MediaModel media = new MediaModel();
            String file = "/uploads/files/" + filename;
            mediaService.saveMedia(media,user,file);

            if (existingFilePath != null && !existingFilePath.isEmpty()) {
                String oldFilePath = new File("src/main/resources/static" + existingFilePath).getAbsolutePath();
                File oldFile = new File(oldFilePath);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }

            post.setFilePath(file);
        } else{
            post.setFilePath(existingFilePath);
        }

        model.addAttribute("post", post);
        postService.savePost(post, user);
        return "redirect:/posts";
    }
    @GetMapping("/posts/{id}")
    @ResponseBody
    public ResponseEntity<PostModel> getPostById(@PathVariable Long id) {
        System.out.println("Fetching post with ID: " + id);
        try {
            PostModel post = postService.getPostById(id);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            System.err.println("Error fetching post with ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("/postsDetail/{id}")
    public String showPostDetail(@PathVariable Long id, Model model, HttpSession session) {
        PostModel post = postService.getPostById(id);

        // Retrieve the logged-in user from the session
        UsersModel user = (UsersModel) session.getAttribute("user");
        String userRole = user.getRole().name();
        if (user == null) {
            user = new UsersModel(); // Fallback: create a new user object if not logged in
            // Alternatively, redirect to login page: return "redirect:/login";
        }

        model.addAttribute("post", post);
        model.addAttribute("user", user);
        model.addAttribute("userRole",userRole);
        return "post/post-detail";
    }
    @PostMapping("/{id}/approve")
    public String approvePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            PostModel post = postService.getPostById(id);
            post.setStatus("Approved");
            UsersModel user = post.getUser();
            NotifyModel notify = new NotifyModel();
            notifyService.saveNotify(notify,user,"Bài viết của bạn: " +post.getPostTitle()+" đã được duyệt");
            postService.savePost(post,user);
            redirectAttributes.addFlashAttribute("message", "Post approved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to approve post: " + e.getMessage());
        }
        return "redirect:/posts";
    }

    // Deny a post
    @PostMapping("/{id}/deny")
    public String denyPost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            PostModel post = postService.getPostById(id);
            post.setStatus("Denied");
            UsersModel user = post.getUser();
            NotifyModel notify = new NotifyModel();
            notifyService.saveNotify(notify,user,"Bài viết của bạn: " +post.getPostTitle()+" không được phê duệt");
            postService.savePost(post,user);
            redirectAttributes.addFlashAttribute("message", "Post denied successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to deny post: " + e.getMessage());
        }
        return "redirect:/posts" ;
    }
}
