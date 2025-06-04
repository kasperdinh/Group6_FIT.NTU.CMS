package group6.fit_ntu_cms.repositories;

import group6.fit_ntu_cms.models.NotifyModel;
import group6.fit_ntu_cms.models.UsersModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotifyRepository extends JpaRepository<NotifyModel, Long> {
    // Tìm tất cả thông báo của một người dùng
    List<NotifyModel> findByUser(UsersModel user);

    // Tìm thông báo của một người dùng theo trạng thái
    List<NotifyModel> findByUserAndStatus(UsersModel user, boolean status);

    // Tìm thông báo của một người dùng theo trạng thái và sắp xếp theo thời gian tạo
    List<NotifyModel> findByUserAndStatusOrderByCreatedAtDesc(UsersModel user, boolean status);

    // Xóa thông báo theo user_id (nếu cần)
    void deleteByUser(UsersModel user);
}