package group6.fit_ntu_cms.services;

import group6.fit_ntu_cms.models.SettingModel;
import group6.fit_ntu_cms.repositories.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;

    // Lấy cài đặt hiện tại
    public SettingModel getSetting() {
        SettingModel setting = settingRepository.findFirstByOrderByIdAsc();
        if (setting == null) {
            // Nếu chưa có bản ghi, tạo một bản ghi mặc định
            setting = new SettingModel();
            setting.setSiteName("PUBLIC"); // Giá trị mặc định
            return settingRepository.save(setting);
        }
        return setting;
    }

    // Cập nhật cài đặt
    @Transactional
    public SettingModel updateSetting(SettingModel updatedSetting) {
        SettingModel currentSetting = getSetting();
        currentSetting.setSiteName(updatedSetting.getSiteName());
        return settingRepository.save(currentSetting);
    }
}