package group6.fit_ntu_cms.repositories;

import group6.fit_ntu_cms.models.SettingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends JpaRepository<SettingModel, Long> {
    // Tìm bản ghi đầu tiên (vì chỉ có 1 bản ghi)
    SettingModel findFirstByOrderByIdAsc();
}