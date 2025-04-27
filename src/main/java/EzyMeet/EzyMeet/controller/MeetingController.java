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
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/meetings")
@CrossOrigin(origins = "http://localhost:3000")
public class MeetingController {

    private final MeetingService meetingService;

    @Autowired
    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @PostMapping("/create")
    public ResponseEntity<Meeting> createMeeting(@RequestBody Meeting meeting) {
        Meeting createdMeeting = meetingService.createMeeting(meeting);
        return ResponseEntity.ok(createdMeeting);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Meeting>> getUserMeetings(@PathVariable String userId) {
        List<Meeting> meetings = meetingService.getUserMeetings(userId);
        return ResponseEntity.ok(meetings);
    }

    @GetMapping("/meeting/{meetingId}")
    public ResponseEntity<Map<String, Object>> getSingleMeetingById(@PathVariable String meetingId) {
        try {
            Map<String, Object> response = meetingService.getSingleMeetingById(meetingId);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

//    @PostMapping("/update/{meetingId}")
//    public ResponseEntity<Meeting> updateMeeting(@PathVariable String meetingId, @RequestBody Meeting meeting) {
//        // 這裡可以添加一些驗證邏輯，例如檢查會議時間是否衝突等
//        Meeting updatedMeeting = meetingService.updateMeeting(meetingId, meeting);
//        return ResponseEntity.ok(updatedMeeting);
//    }

    @DeleteMapping("/delete/{meetingId}")
    public ResponseEntity<Map<String, String>> deleteMeeting(@PathVariable String meetingId) {
        Meeting deletedMeeting = meetingService.deleteMeeting(meetingId);
        Map<String, String> response = new HashMap<>();
        response.put("title", deletedMeeting.getTitle());
        return ResponseEntity.ok(response);
    }
}