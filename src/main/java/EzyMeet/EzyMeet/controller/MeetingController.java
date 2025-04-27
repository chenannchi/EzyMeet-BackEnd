package EzyMeet.EzyMeet.controller;

import EzyMeet.EzyMeet.model.Meeting;
import EzyMeet.EzyMeet.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/meetings")
@CrossOrigin(origins = "http://localhost:3000")
public class MeetingController {

    private final MeetingService meetingService;

    @Autowired
    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    // 創建會議
    @PostMapping("/create")
    public ResponseEntity<Meeting> createMeeting(@RequestBody Meeting meeting) {
        //如果 JSON 中缺少某個字段（如 id），相應的對象屬性會被設為 null（對於 Long 等引用類型）
        Meeting createdMeeting = meetingService.createMeeting(meeting);
        return ResponseEntity.ok(createdMeeting);
    }

    @GetMapping("/user/meetings/{userId}")
    public ResponseEntity<List<Meeting>> getUserMeetings(@PathVariable String userId) {
        List<Meeting> meetings = meetingService.getUserMeetings(userId);
        return ResponseEntity.ok(meetings);
    }

    @GetMapping("/meeting/{meetingId}")
    public ResponseEntity<Meeting> getSingleMeetingById(@PathVariable String meetingId) {
        Meeting meeting = meetingService.getSingleMeetingById(meetingId);
        if (meeting != null) {
            return ResponseEntity.ok(meeting);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/update/{meetingId}")
    public ResponseEntity<Meeting> updateMeeting(@PathVariable String meetingId, @RequestBody Meeting meeting) {
        // 這裡可以添加一些驗證邏輯，例如檢查會議時間是否衝突等
        Meeting updatedMeeting = meetingService.updateMeeting(meetingId, meeting);
        return ResponseEntity.ok(updatedMeeting);
    }

    @DeleteMapping("/delete/{meetingId}")
    public ResponseEntity<Map<String, String>> deleteMeeting(@PathVariable String meetingId) {
        Meeting deletedMeeting = meetingService.deleteMeeting(meetingId);
        Map<String, String> response = new HashMap<>();
        response.put("title", deletedMeeting.getTitle());
        return ResponseEntity.ok(response);
    }
}