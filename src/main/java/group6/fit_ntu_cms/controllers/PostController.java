package group6.fit_ntu_cms.controllers;

import group6.fit_ntu_cms.models.EventModel;
import group6.fit_ntu_cms.models.PostModel;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.services.PostService;
import group6.fit_ntu_cms.services.TittleService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private TittleService tittleService;
    @GetMapping("/posts")
    public String showPosts(@RequestParam(required = false) String status,
                            @RequestParam(required = false) Long category,
                            @RequestParam(required = false) String search,
                            Model model) {
        List<PostModel> posts = postService.filterPosts(status, category, search);
        model.addAttribute("posts", posts);
        model.addAttribute("post", new PostModel());
        model.addAttribute("allCategories", tittleService.getAllTittles());
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

        // Kiểm tra lỗi xác thực
        if (result.hasErrors()) {
            model.addAttribute("posts", postService.getAllPosts());
            model.addAttribute("post", post);
            model.addAttribute("errorMessage", "Vui lòng sửa các lỗi trong biểu mẫu.");
            return "post/posts";
        }

        // Xử lý tải lên tệp ảnh
        if (imageFile != null && !imageFile.isEmpty()) {
            String uploadDir = new File("src/main/resources/static/img/").getAbsolutePath();
            String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            File saveFile = new File(uploadDir, filename);
            saveFile.getParentFile().mkdirs();
            imageFile.transferTo(saveFile);
            post.setPostImage("/img/" + filename);
        }

        // Xử lý tải lên tệp tài liệu
        if (filePath != null && !filePath.isEmpty()) {
            String uploadDir = new File("src/main/resources/static/uploads/").getAbsolutePath();
            String filename = UUID.randomUUID() + "_" + filePath.getOriginalFilename();
            File saveFile = new File(uploadDir, filename);
            saveFile.getParentFile().mkdirs();
            filePath.transferTo(saveFile);
            post.setFilePath("/uploads/" + filename);
        }

        // Lưu sự kiện
        try {
            postService.savePost(post, user);
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
    public String removePost(@RequestParam(value = "postId", required = false) Long postId, Model model) {
        if (postId == null) {
            model.addAttribute("errorMessage", "Post ID is missing. Unable to delete the post.");
            return "redirect:/posts"; // Redirect back to posts page with an error
        }

        PostModel post = postService.getPostById(postId).orElse(null);
        if (post == null) {
            model.addAttribute("errorMessage", "Post not found with ID: " + postId);
            return "redirect:/posts";
        }

        try {
            // Delete associated image file
            if (post.getPostImage() != null) {
                String imagePath = new File("src/main/resources/static" + post.getPostImage()).getAbsolutePath();
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    imageFile.delete();
                }
            }

            // Delete associated document file
            if (post.getFilePath() != null) {
                String filePath = new File("src/main/resources/static" + post.getFilePath()).getAbsolutePath();
                File file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                }
            }

            // Delete the post from the database
            postService.deletePost(postId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error deleting post: " + e.getMessage());
            return "redirect:/posts";
        }

        return "redirect:/posts"; // Redirect to /posts, not /events
    }
    @PostMapping("/editPosts")
    public String editPost(
            @ModelAttribute PostModel post,
            HttpSession session,
            @RequestParam("imgFile") MultipartFile imageFile,
            @RequestParam(value = "existingImagePath", required = false) String existingImage,
            @RequestParam(value = "existingFilePath", required = false) String existingFilePath,
            @RequestParam("file") MultipartFile filePath) throws IOException {
        UsersModel user = (UsersModel) session.getAttribute("user");
        if (imageFile != null && !imageFile.isEmpty()) {
            String uploadDir = new File("src/main/resources/static/img/").getAbsolutePath();
            String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            File saveFile = new File(uploadDir, filename);
            saveFile.getParentFile().mkdirs();
            imageFile.transferTo(saveFile);

            if (existingImage != null && !existingImage.isEmpty()) {
                String oldImagePath = new File("src/main/resources/static" + existingImage).getAbsolutePath();
                File oldImageFile = new File(oldImagePath);
                if (oldImageFile.exists()) {
                    oldImageFile.delete();
                }
            }
            post.setPostImage("/img/" + filename);
        } else {
            post.setPostImage(existingImage);
        }

        if (filePath != null && !filePath.isEmpty()) {
            String uploadDir = new File("src/main/resources/static/uploads/").getAbsolutePath();
            String filename = UUID.randomUUID() + "_" + filePath.getOriginalFilename();
            File saveFile = new File(uploadDir, filename);
            saveFile.getParentFile().mkdirs();
            filePath.transferTo(saveFile);

            if (existingFilePath != null && !existingFilePath.isEmpty()) {
                String oldFilePath = new File("src/main/resources/static" + existingFilePath).getAbsolutePath();
                File oldFile = new File(oldFilePath);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }

            post.setFilePath("/uploads/" + filename);
        } else{
            post.setFilePath(existingFilePath);
        }

        postService.savePost(post, user);
        return "redirect:/posts";
    }
    @GetMapping("/posts/{id}")
    @ResponseBody
    public PostModel getPostById(@PathVariable Long id) {
        return postService.getPostById(id).orElseThrow(() -> new RuntimeException("Event not found"));
    }
}
