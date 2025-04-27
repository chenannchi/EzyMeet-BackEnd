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
        // TODO: time conflict check
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

    public Meeting getSingleMeetingById(String meetingId) {
        return meetingRepository.findSingleMeetingById(meetingId);
    }


//    public Meeting updateMeeting(String meetingId, Meeting meeting) {
//        // TODO: time conflict check
//
//        Meeting updatedMeeting = meetingRepository.update(meetingId, meeting);
//
//        List<MeetingParticipant> originalInvitees = meetingParticipantRepository.findByMeetingId(meetingId);
//
//        if (meeting.getInvitees() != null && !meeting.getInvitees().isEmpty()) {
//            List<MeetingParticipant> newInvitees = meeting.getInvitees();
//
//            for (MeetingParticipant newParticipant : newInvitees) {
//                if (!originalInvitees.contains(newParticipant)) {
//                    newParticipant.setMeetingId(updatedMeeting.getId());
//                    meetingParticipantRepository.create(newParticipant);
//                }
//            }
//
//            for (MeetingParticipant originalParticipant : originalInvitees) {
//                if (!newInvitees.contains(originalParticipant)) {
//                    meetingParticipantRepository.delete(originalParticipant.getId());
//                }
//            }
//
//        }
//        return updatedMeeting;
//    }

    public Meeting deleteMeeting(String meetingId) {
        meetingParticipantRepository.deleteByMeetingId(meetingId);
        return meetingRepository.delete(meetingId);
    }
}