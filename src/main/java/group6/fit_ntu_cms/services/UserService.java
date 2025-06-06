package group6.fit_ntu_cms.services;

import group6.fit_ntu_cms.models.Role;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.repositories.UsersRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    public boolean registerUser(UsersModel user) {
        if (usersRepository.existsByEmail(user.getEmail())) {
            return false; // Email đã tồn tại
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedDate(LocalDateTime.now());
        user.setRole(Role.USER);
        usersRepository.save(user);
        return true;
    }

    public UsersModel findByUsername(String username) {
        return usersRepository.findByUsername(username).orElse(null);
    }

    public void generateAndSendOtp(String email) {
        UsersModel user = usersRepository.findByEmail(email);
        if (user != null) {
            String otp = String.valueOf(new Random().nextInt(999999));
            user.setOtp(otp);
            user.setOtpRequestedTime(LocalDateTime.now());
            usersRepository.save(user);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Mã OTP để lấy lại mật khẩu");
            message.setText("Mã OTP của bạn là: " + otp);
            mailSender.send(message);
        }
    }

    public Page<UsersModel> searchUsers(String keyword, Pageable pageable) {
        return usersRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);
    }

    public Page<UsersModel> findAllUsers(Pageable pageable) {
        return usersRepository.findAll(pageable);
    }

    public UsersModel findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public UsersModel findById(Long id) {
        return usersRepository.findById(id).orElse(null);
    }

    public List<UsersModel> findByRole(Role role) {
        return usersRepository.findByRole(role);
    }

    public void save(UsersModel user) {
        usersRepository.save(user);
    }

    public void delete(UsersModel user) {
        usersRepository.delete(user);
    }
}