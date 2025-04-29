package EzyMeet.EzyMeet.impl;

import EzyMeet.EzyMeet.dto.*;
import EzyMeet.EzyMeet.exception.TimeSlotConflictException;
import EzyMeet.EzyMeet.model.Meeting;
import EzyMeet.EzyMeet.model.MeetingParticipant;
import EzyMeet.EzyMeet.model.TimeSlot;
import EzyMeet.EzyMeet.model.User;
import EzyMeet.EzyMeet.repository.MeetingParticipantRepository;
import EzyMeet.EzyMeet.repository.MeetingRepository;
import EzyMeet.EzyMeet.repository.UserRepository;
import EzyMeet.EzyMeet.service.MeetingService;
import com.google.cloud.spring.data.firestore.FirestoreReactiveOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("MeetingService")
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;
    private final UserRepository userRepository;

    @Autowired
    public MeetingServiceImpl(MeetingRepository meetingRepository, MeetingParticipantRepository meetingParticipantRepository, UserRepository userRepository) {
        this.meetingRepository = meetingRepository;
        this.meetingParticipantRepository = meetingParticipantRepository;
        this.userRepository = userRepository;
    }

    public ResponseMeetingDto createMeeting(RequestCreateMeetingDto requestDto) {
        List<TimeSlot> userMeetingTimeSlots = getUserMeetingTimeSlots(requestDto.getHost());
        if (isTimeSlotConflict(requestDto.getTimeslot(), userMeetingTimeSlots)) {
            throw new TimeSlotConflictException("The provided time slot conflicts with existing time slots.");
        }

        Meeting meeting = new Meeting();
        meeting.setTitle(requestDto.getTitle());
        meeting.setLabel(requestDto.getLabel());
        meeting.setTimeslot(requestDto.getTimeslot());
        meeting.setLocation(requestDto.getLocation());
        meeting.setLink(requestDto.getLink());
        meeting.setDescription(requestDto.getDescription());
        meeting.setHost(requestDto.getHost());
        meeting.setMeetingRecord("");

        Meeting savedMeeting = meetingRepository.create(meeting);

        List<ResponseMeetingDto.ResponseParticipantDto> participantResponses = new ArrayList<>();
        if (requestDto.getParticipants() != null) {
            for (RequestCreateMeetingDto.RequestParticipantDto participantDto : requestDto.getParticipants()) {
                MeetingParticipant participant = new MeetingParticipant();
                participant.setMeetingId(savedMeeting.getId());
                participant.setUserId(participantDto.getUserId());
                participant.setStatus(participantDto.getStatus());

                MeetingParticipant savedParticipant = meetingParticipantRepository.create(participant);

                participantResponses.add(ResponseMeetingDto.ResponseParticipantDto.builder()
                        .userId(savedParticipant.getUserId())
                        .status(savedParticipant.getStatus())
                        .build());
            }
        }

        return ResponseMeetingDto.builder()
                .id(savedMeeting.getId())
                .title(savedMeeting.getTitle())
                .label(savedMeeting.getLabel())
                .timeslot(savedMeeting.getTimeslot())
                .location(savedMeeting.getLocation())
                .link(savedMeeting.getLink())
                .description(savedMeeting.getDescription())
                .host(savedMeeting.getHost())
                .participants(participantResponses)
                .meetingRecord(savedMeeting.getMeetingRecord())
                .build();
    }

    public List<ResponseMeetingDto> getUserMeetings(String userId) {
        List<Meeting> meetings = getUserAllMeetings(userId);
        return  meetings.stream()
                .map(this::convertMeetingToDto)
                .collect(Collectors.toList());
    }

    private List<Meeting> getUserAllMeetings(String userId) {
        List<String> participantMeetingIds = meetingParticipantRepository.findByUserId(userId)
                .stream()
                .map(MeetingParticipant::getMeetingId)
                .collect(Collectors.toList());

        List<Meeting> hostedMeetings = meetingRepository.findMeetingsByHost(userId);
        List<Meeting> participantMeetings = meetingRepository.findMeetingsById(participantMeetingIds);

        List<Meeting> allMeetings = new ArrayList<>(participantMeetings);
        allMeetings.addAll(hostedMeetings);

        return allMeetings;
    }

    public ResponseMeetingInfoDto getSingleMeetingById(String meetingId) {

        Meeting meeting = meetingRepository.findSingleMeetingById(meetingId);
        if (meeting == null) {
            throw new NoSuchElementException("Meeting not found with ID: " + meetingId);
        }

        List<MeetingParticipant> participants = meetingParticipantRepository.findByMeetingId(meetingId);

        List<ResponseMeetingInfoDto.ResponseParticipantDto> invited = new ArrayList<>();
        List<ResponseMeetingInfoDto.ResponseParticipantDto> accepted = new ArrayList<>();
        List<ResponseMeetingInfoDto.ResponseParticipantDto> declined = new ArrayList<>();

        for (MeetingParticipant participant : participants) {
                User user = userRepository.findByUserId(participant.getUserId());
                if (user == null) {
                    continue;
                }

            ResponseMeetingInfoDto.ResponseParticipantDto dto = ResponseMeetingInfoDto.ResponseParticipantDto.builder()
                    .userId(participant.getUserId())
                    .email(user.getEmail())
                    .name(user.getDisplayName())
                    .build();

            switch (participant.getStatus()) {
                case INVITED:
                    invited.add(dto);
                    break;
                case ACCEPTED:
                    accepted.add(dto);
                    break;
                case DECLINED:
                    declined.add(dto);
                    break;
            }
        }

        return ResponseMeetingInfoDto.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .label(meeting.getLabel())
                .timeslot(meeting.getTimeslot())
                .location(meeting.getLocation())
                .link(meeting.getLink())
                .description(meeting.getDescription())
                .host(meeting.getHost())
                .meetingRecord(meeting.getMeetingRecord())
                .invitedParticipants(invited)
                .acceptedParticipants(accepted)
                .declinedParticipants(declined)
                .build();
    }


    public ResponseMeetingDto updateMeeting(String meetingId, RequestUpdateMeetingDto requestUpdateDto) {
        List<TimeSlot> userMeetingTimeSlotsExceptCurrent = getUserMeetingTimeSlotsExcludeCurrentMeeting(requestUpdateDto.getHost(), meetingId);
        if (isTimeSlotConflict(requestUpdateDto.getTimeslot(), userMeetingTimeSlotsExceptCurrent)) {
            throw new TimeSlotConflictException("The provided time slot conflicts with existing time slots.");
        }

        Meeting existingMeeting = meetingRepository.findSingleMeetingById(meetingId);
        if (existingMeeting == null) {
            throw new NoSuchElementException("Meeting not found with ID: " + meetingId);
        }

        Meeting updatedMeeting = new Meeting();
        updatedMeeting.setId(meetingId);
        updatedMeeting.setTitle(requestUpdateDto.getTitle());
        updatedMeeting.setLabel(requestUpdateDto.getLabel());
        updatedMeeting.setTimeslot(requestUpdateDto.getTimeslot());
        updatedMeeting.setLocation(requestUpdateDto.getLocation());
        updatedMeeting.setLink(requestUpdateDto.getLink());
        updatedMeeting.setDescription(requestUpdateDto.getDescription());
        updatedMeeting.setHost(requestUpdateDto.getHost());
        updatedMeeting.setMeetingRecord(requestUpdateDto.getMeetingRecord());

        Meeting savedMeeting = meetingRepository.update(meetingId, updatedMeeting);

//        List<RequestUpdateMeetingDto.RequestParticipantDto> participantDtos = new ArrayList<>();
//        if (requestUpdateDto.getParticipants() != null) {
//            List<MeetingParticipant> participants = requestUpdateDto.getParticipants().stream()
//                    .map(dto -> {
//                        MeetingParticipant participant = new MeetingParticipant();
//                        participant.setId(dto.getId());
//                        participant.setMeetingId(meetingId);
//                        participant.setUserId(dto.getUserId());
//                        participant.setStatus(dto.getStatus());
//                        return participant;
//                    })
//                    .collect(Collectors.toList());
//
//            syncParticipants(meetingId, participants);
//
//            participantDtos = requestUpdateDto.getParticipants();
//        }
        List<ResponseMeetingDto.ResponseParticipantDto> participantDtos = new ArrayList<>();
        if (requestUpdateDto.getParticipants() != null) {
            participantDtos = requestUpdateDto.getParticipants().stream()
                    .map(dto -> ResponseMeetingDto.ResponseParticipantDto.builder()
                            .userId(dto.getUserId())
                            .status(dto.getStatus())
                            .build())
                    .collect(Collectors.toList());
        }


        return ResponseMeetingDto.builder()
                .id(savedMeeting.getId())
                .title(savedMeeting.getTitle())
                .label(savedMeeting.getLabel())
                .timeslot(savedMeeting.getTimeslot())
                .location(savedMeeting.getLocation())
                .link(savedMeeting.getLink())
                .description(savedMeeting.getDescription())
                .host(savedMeeting.getHost())
                .participants(participantDtos)
                .meetingRecord(savedMeeting.getMeetingRecord())
                .build();
    }

    public ResponseMeetingDto deleteMeeting(String meetingId) {
        meetingParticipantRepository.deleteByMeetingId(meetingId);
        Meeting deletedMeeting = meetingRepository.delete(meetingId);
        return ResponseMeetingDto.builder()
                .title(deletedMeeting.getTitle())
                .build();
    }

//    private void syncParticipants(String meetingId, List<MeetingParticipant> newParticipants) {
//        List<MeetingParticipant> originalParticipants = meetingParticipantRepository.findByMeetingId(meetingId);
//        Map<String, MeetingParticipant> originalParticipantMap = originalParticipants.stream()
//                .collect(Collectors.toMap(MeetingParticipant::getUserId, Function.identity()));
//
//        Set<String> incomingUserIds = newParticipants == null
//                ? Collections.emptySet()
//                : newParticipants.stream()
//                .map(MeetingParticipant::getUserId)
//                .collect(Collectors.toSet());
//
//        newParticipants.stream()
//                .filter(p -> !originalParticipantMap.containsKey(p.getUserId()))
//                .forEach(p -> {
//                    p.setMeetingId(meetingId);
//                    p.setId(null);
//                    meetingParticipantRepository.create(p);
//                });
//
//        originalParticipants.stream()
//                .filter(p -> !incomingUserIds.contains(p.getUserId()))
//                .forEach(p -> meetingParticipantRepository.delete(p.getId()));
//
//        newParticipants.stream()
//                .filter(p -> originalParticipantMap.containsKey(p.getUserId()))
//                .filter(p -> !Objects.equals(originalParticipantMap.get(p.getUserId()).getStatus(), p.getStatus()))
//                .forEach(p -> {
//                    MeetingParticipant existingParticipant = originalParticipantMap.get(p.getUserId());
//                    p.setId(existingParticipant.getId());
//                    p.setMeetingId(meetingId);
//                    meetingParticipantRepository.update(existingParticipant.getId(), p);
//                });
//    }

    private ResponseMeetingDto convertMeetingToDto(Meeting meeting) {
        List<MeetingParticipant> participants =
                meetingParticipantRepository.findByMeetingId(meeting.getId());

        List<ResponseMeetingDto.ResponseParticipantDto> participantDtos =
                participants.stream()
                        .map(p -> ResponseMeetingDto.ResponseParticipantDto.builder()
                                .userId(p.getUserId())
                                .status(p.getStatus())
                                .build())
                        .collect(Collectors.toList());

        return ResponseMeetingDto.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .label(meeting.getLabel())
                .timeslot(meeting.getTimeslot())
                .location(meeting.getLocation())
                .link(meeting.getLink())
                .description(meeting.getDescription())
                .host(meeting.getHost())
                .participants(participantDtos)
                .meetingRecord(meeting.getMeetingRecord())
                .build();
    }

    private boolean isTimeSlotConflict(TimeSlot newTimeSlot, List<TimeSlot> existingTimeSlots) {
        return existingTimeSlots.stream()
                .anyMatch(existingTimeSlot -> existingTimeSlot.conflictsWith(newTimeSlot));
    }

    private List<TimeSlot> getUserMeetingTimeSlotsExcludeCurrentMeeting(String userId, String meetingIdToExclude) {
        return getUserAllMeetings(userId).stream()
                .filter(meeting -> !meeting.getId().equals(meetingIdToExclude))
                .map(Meeting::getTimeslot)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<TimeSlot> getUserMeetingTimeSlots(String userId) {
        return getUserAllMeetings(userId).stream()
                .map(Meeting::getTimeslot)
                .collect(Collectors.toList());
    }
}
