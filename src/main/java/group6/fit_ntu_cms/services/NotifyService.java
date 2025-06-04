package group6.fit_ntu_cms.services;

import group6.fit_ntu_cms.models.NotifyModel;
import group6.fit_ntu_cms.models.UsersModel;
import group6.fit_ntu_cms.repositories.NotifyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotifyService {

    @Autowired
    private NotifyRepository notifyRepository;

    // Lưu thông báo mới
    @Transactional
    public NotifyModel saveNotify(NotifyModel notify, UsersModel user, String content) {
        if (notify.getCreatedAt() == null) {
            notify.setCreatedAt(LocalDateTime.now());
        }
        notify.setStatus(false);
        notify.setUser(user);
        notify.setContent(content);
        return notifyRepository.save(notify);
    }

    @Transactional
    public void saveNotifyForUsers(List<UsersModel> users, String content) {
        for (UsersModel user : users) {
            NotifyModel notify = new NotifyModel();
            saveNotify(notify, user, content);
        }
    }

    // Lấy thông báo theo ID
    public Optional<NotifyModel> getNotifyById(Long notifyId) {
        return notifyRepository.findById(notifyId);
    }

    // Lấy tất cả thông báo của một người dùng
    public List<NotifyModel> getNotificationsByUser(UsersModel user) {
        return notifyRepository.findByUser(user);
    }

    // Lấy thông báo chưa đọc của một người dùng
    public List<NotifyModel> getUnreadNotificationsByUser(UsersModel user) {
        return notifyRepository.findByUserAndStatus(user, false);
    }

    // Đánh dấu thông báo là đã đọc
    @Transactional
    public void markAsRead(Long notifyId) {
        Optional<NotifyModel> notifyOpt = notifyRepository.findById(notifyId);
        notifyOpt.ifPresent(notify -> {
            notify.setStatus(true);
            notifyRepository.save(notify);
        });
    }

    // Đánh dấu tất cả thông báo của một người dùng là đã đọc
    @Transactional
    public void markAllAsRead(UsersModel user) {
        List<NotifyModel> notifications = notifyRepository.findByUser(user);
        notifications.forEach(notification -> {
            if (!notification.isStatus()) {
                notification.setStatus(true);
                notifyRepository.save(notification);
            }
        });
    }

    // Xóa thông báo theo ID
    @Transactional
    public void deleteNotify(Long notifyId) {
        notifyRepository.deleteById(notifyId);
    }

    // Xóa tất cả thông báo của một người dùng
    @Transactional
    public void clearAllNotifications(UsersModel user) {
        List<NotifyModel> notifications = notifyRepository.findByUser(user);
        notifyRepository.deleteAll(notifications);
    }
}