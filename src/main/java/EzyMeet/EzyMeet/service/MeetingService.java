package EzyMeet.EzyMeet.service;

import EzyMeet.EzyMeet.model.Meeting;
import EzyMeet.EzyMeet.model.MeetingParticipant;
import EzyMeet.EzyMeet.repository.MeetingParticipantRepository;
import EzyMeet.EzyMeet.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;

    // 使用 constructor injection 將 repository 注入 service
    @Autowired
    public MeetingService(MeetingRepository meetingRepository, MeetingParticipantRepository meetingParticipantRepository) {
        this.meetingRepository = meetingRepository;
        this.meetingParticipantRepository = meetingParticipantRepository;
    }

    // 創建會議
    public Meeting createMeeting(Meeting meeting) {
        // 這裡可以添加一些驗證邏輯，例如檢查會議時間是否衝突等
        Meeting createdMeeting = meetingRepository.create(meeting);
        if (meeting.getInvitees() != null && !meeting.getInvitees().isEmpty()) {
            for (MeetingParticipant participant : meeting.getInvitees()) {
                participant.setMeetingId(createdMeeting.getId());
                meetingParticipantRepository.create(participant);
            }
        }
        return createdMeeting;
    }

    public List<Meeting> getUserMeetings(String userId) {
        List<MeetingParticipant> joinedMeetings = meetingParticipantRepository.findByUserId(userId);
        List<String> meetingIds = joinedMeetings.stream()
                .map(MeetingParticipant::getMeetingId)
                .collect(Collectors.toList());

        if (meetingIds.isEmpty()) {
            return new ArrayList<>();
        }

        return meetingRepository.findMeetingsById(meetingIds);
    }

//    public Meeting editMeeting(Meeting meeting) {
//        // 這裡可以添加一些驗證邏輯，例如檢查會議時間是否衝突等
//
//        List<MeetingParticipant> currentParticipants =
//                meetingParticipantRepository.findByMeetingId(meeting.getId());
//        List<MeetingParticipant> newParticipants = meeting.getInvitees();
//
//        if (newParticipants != null) {
//            Map<String, MeetingParticipant> currentParticipantMap = currentParticipants.stream()
//                    .collect(Collectors.toMap(MeetingParticipant::getUserId, p -> p));
//
//            for (MeetingParticipant newParticipant : newParticipants) {
//                newParticipant.setMeetingId(meeting.getId());
//
//                if (!currentParticipantMap.containsKey(newParticipant.getUserId())) {
//                    meetingParticipantRepository.create(newParticipant);
//                } else {
//                    MeetingParticipant existing = currentParticipantMap.get(newParticipant.getUserId());
//                    newParticipant.setId(existing.getId());
//                    meetingParticipantRepository.update(newParticipant);
//                }
//            }
//
//            return meetingRepository.edit(meeting);
//        }
//    }

    public Meeting deleteMeeting(String meetingId) {
        meetingParticipantRepository.deleteByMeetingId(meetingId);
        return meetingRepository.delete(meetingId);
    }
}