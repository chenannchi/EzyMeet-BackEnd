package EzyMeet.EzyMeet.service;

import EzyMeet.EzyMeet.model.Meeting;
import EzyMeet.EzyMeet.model.MeetingParticipant;
import EzyMeet.EzyMeet.repository.MeetingParticipantRepository;
import EzyMeet.EzyMeet.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;

    @Autowired
    public MeetingService(MeetingRepository meetingRepository, MeetingParticipantRepository meetingParticipantRepository) {
        this.meetingRepository = meetingRepository;
        this.meetingParticipantRepository = meetingParticipantRepository;
    }

    public Meeting createMeeting(Meeting meeting) {
        // TODO: time conflict check
        Meeting createdMeeting = meetingRepository.create(meeting);
        if (meeting.getInvitees() != null && !meeting.getInvitees().isEmpty()) {
            List<MeetingParticipant> updatedInvitees = new ArrayList<>();

            for (MeetingParticipant participant : meeting.getInvitees()) {
                participant.setMeetingId(createdMeeting.getId());
                MeetingParticipant savedParticipant = meetingParticipantRepository.create(participant);
                updatedInvitees.add(savedParticipant);
            }

            createdMeeting.setInvitees(updatedInvitees);
            meetingRepository.update(createdMeeting.getId(), createdMeeting);
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

    public Map<String, Object> getSingleMeetingById(String meetingId) {
        Meeting meeting = meetingRepository.findSingleMeetingById(meetingId);
        if (meeting == null) {
            throw new NoSuchElementException("Meeting not found with ID: " + meetingId);
        }

        Map<String, List<MeetingParticipant>> dividedInvitees = new HashMap<>();
        dividedInvitees.put("invited", new ArrayList<>());
        dividedInvitees.put("accepted", new ArrayList<>());
        dividedInvitees.put("declined", new ArrayList<>());

        for (MeetingParticipant invitee : meeting.getInvitees()) {
            switch (invitee.getStatus()) {
                case INVITED:
                    dividedInvitees.get("invited").add(invitee);
                    break;
                case ACCEPTED:
                    dividedInvitees.get("accepted").add(invitee);
                    break;
                case DECLINED:
                    dividedInvitees.get("declined").add(invitee);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown status: " + invitee.getStatus());
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("meeting", meeting);
        response.putAll(dividedInvitees);

        return response;
//        return meetingRepository.findSingleMeetingById(meetingId);
    }


    public Meeting updateMeeting(String meetingId, Meeting newMeeting) {
        // TODO: time conflict check

        Meeting updatedMeeting = meetingRepository.update(meetingId, newMeeting);
        syncInvitees(updatedMeeting.getId(), newMeeting.getInvitees());

        return updatedMeeting;
    }

    public Meeting deleteMeeting(String meetingId) {
        meetingParticipantRepository.deleteByMeetingId(meetingId);
        return meetingRepository.delete(meetingId);
    }

    public void syncInvitees(String meetingId, List<MeetingParticipant> newInvitees) {
        List<MeetingParticipant> originalInvitees = meetingParticipantRepository.findByMeetingId(meetingId);
        Map<String, MeetingParticipant> origInviteesMap = originalInvitees.stream()
                .collect(Collectors.toMap(MeetingParticipant::getId, Function.identity()));

        Set<String> incomingIds = newInvitees == null
                ? Collections.emptySet()
                : newInvitees.stream()
                .map(MeetingParticipant::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        newInvitees.stream()
                .filter(p -> p.getId() == null || !origInviteesMap.containsKey(p.getId()))
                .forEach(p -> {
                    p.setMeetingId(meetingId);
                    meetingParticipantRepository.create(p);
                });

        origInviteesMap.keySet().stream()
                .filter(id -> !incomingIds.contains(id))
                .forEach(meetingParticipantRepository::delete);

        newInvitees.stream()
                .filter(p -> p.getId() != null && origInviteesMap.containsKey(p.getId()))
                .filter(p -> !Objects.equals(origInviteesMap.get(p.getId()).getStatus(), p.getStatus()))
                .forEach(p -> meetingParticipantRepository.update(p.getId(), p));
    }
}