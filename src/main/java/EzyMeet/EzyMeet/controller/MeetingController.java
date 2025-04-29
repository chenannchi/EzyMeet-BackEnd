package EzyMeet.EzyMeet.controller;

import EzyMeet.EzyMeet.dto.RequestCreateMeetingDto;
import EzyMeet.EzyMeet.dto.RequestUpdateMeetingDto;
import EzyMeet.EzyMeet.dto.ResponseDetailedMeetingDto;
import EzyMeet.EzyMeet.dto.ResponseMeetingDto;
import EzyMeet.EzyMeet.exception.TimeSlotConflictException;
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
    public ResponseEntity<?> createMeeting(@RequestBody RequestCreateMeetingDto requestDto) {
        try {
            ResponseMeetingDto createdMeeting = meetingService.createMeeting(requestDto);
            return ResponseEntity.ok(createdMeeting);
        } catch (TimeSlotConflictException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("The provided time slot conflicts with existing time slots.", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ResponseMeetingDto>> getUserMeetings(@PathVariable String userId) {
        List<ResponseMeetingDto> meetings = meetingService.getUserMeetings(userId);
        return ResponseEntity.ok(meetings);
    }

    @GetMapping("/meeting/{meetingId}")
    public ResponseEntity<ResponseDetailedMeetingDto> getSingleMeetingById(@PathVariable String meetingId) {
        try {
            ResponseDetailedMeetingDto response = meetingService.getSingleMeetingById(meetingId);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/update/{meetingId}")
    public ResponseEntity<?> updateMeeting(@PathVariable String meetingId, @RequestBody RequestUpdateMeetingDto requestUpdateDto) {
        try {
            ResponseMeetingDto updatedMeeting = meetingService.updateMeeting(meetingId, requestUpdateDto);
            return ResponseEntity.ok(updatedMeeting);
        } catch (TimeSlotConflictException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("The provided time slot conflicts with existing time slots.", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @DeleteMapping("/delete/{meetingId}")
    public ResponseEntity<String> deleteMeeting(@PathVariable String meetingId) {
        ResponseMeetingDto deletedMeeting = meetingService.deleteMeeting(meetingId);
        return ResponseEntity.ok(deletedMeeting.getTitle());
    }
}