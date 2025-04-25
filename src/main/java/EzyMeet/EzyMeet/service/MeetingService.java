package EzyMeet.EzyMeet.service;

import EzyMeet.EzyMeet.model.Meeting;
import EzyMeet.EzyMeet.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;

    // 使用 constructor injection 將 repository 注入 service
    @Autowired
    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    // 創建會議
    public Meeting createMeeting(Meeting meeting) {
        // 這裡可以添加更多的業務邏輯，例如檢查是否有重複的會議
        return meetingRepository.save(meeting);
    }

    // 查詢所有會議
    public List<Meeting> getAllMeetings() {
        return meetingRepository.findAll();
    }

    // 根據 ID 查詢單個會議
    public Optional<Meeting> getMeetingById(Long id) {
        return meetingRepository.findById(id);
    }

    // 刪除會議
    public void deleteMeeting(Long id) {
        meetingRepository.delete(id);
    }

    // 更新會議（如果有需求）
    public Meeting updateMeeting(Long id, Meeting updatedMeeting) {
        Optional<Meeting> existingMeeting = meetingRepository.findById(id);
        if (existingMeeting.isPresent()) {
            Meeting meeting = existingMeeting.get();
            meeting.setTitle(updatedMeeting.getTitle());  // 假設你要更新標題
            meeting.setDescription(updatedMeeting.getDescription());  // 假設你要更新描述
            // 可以更新更多字段...
            return meetingRepository.save(meeting);  // 保存更新後的會議
        } else {
            // 如果會議不存在，可以丟出錯誤或返回某個標誌
            return null;
        }
    }
}