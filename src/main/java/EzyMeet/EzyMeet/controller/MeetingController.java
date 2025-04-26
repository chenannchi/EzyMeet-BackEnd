package EzyMeet.EzyMeet.controller;

import EzyMeet.EzyMeet.model.Meeting;
import EzyMeet.EzyMeet.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

//    // 查詢所有會議
//    @GetMapping
//    public ResponseEntity<List<Meeting>> getAllMeetings() {
//        List<Meeting> meetings = meetingService.getAllMeetings();
//        return ResponseEntity.ok(meetings);
//    }
//
//    // 根據 ID 查詢會議
//    @GetMapping("/{id}")
//    public ResponseEntity<Meeting> getMeetingById(@PathVariable String id) {
//        Optional<Meeting> meeting = meetingService.getMeetingById(id);
//        return meeting.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    // 刪除會議
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteMeeting(@PathVariable String id) {
//        meetingService.deleteMeeting(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    // 更新會議
//    @PutMapping("/{id}")
//    public ResponseEntity<Meeting> updateMeeting(@PathVariable String id, @RequestBody Meeting updatedMeeting) {
//
//        Meeting meeting = meetingService.updateMeeting(id, updatedMeeting);
//        return meeting != null ? ResponseEntity.ok(meeting) : ResponseEntity.notFound().build();
//    }
}