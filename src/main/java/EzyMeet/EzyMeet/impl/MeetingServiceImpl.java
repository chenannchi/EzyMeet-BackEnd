package EzyMeet.EzyMeet.impl;

import EzyMeet.EzyMeet.dto.RequestCreateMeetingDto;
import EzyMeet.EzyMeet.dto.RequestUpdateMeetingDto;
import EzyMeet.EzyMeet.dto.ResponseDetailedMeetingDto;
import EzyMeet.EzyMeet.dto.ResponseMeetingDto;
import EzyMeet.EzyMeet.model.Meeting;
import EzyMeet.EzyMeet.model.MeetingParticipant;
import EzyMeet.EzyMeet.repository.MeetingParticipantRepository;
import EzyMeet.EzyMeet.repository.MeetingRepository;
import EzyMeet.EzyMeet.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("MeetingService")
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;

    @Autowired
    public MeetingServiceImpl(MeetingRepository meetingRepository, MeetingParticipantRepository meetingParticipantRepository) {
        this.meetingRepository = meetingRepository;
        this.meetingParticipantRepository = meetingParticipantRepository;
    }

    public ResponseMeetingDto createMeeting(RequestCreateMeetingDto requestDto) {
        Meeting meeting = new Meeting();
        meeting.setTitle(requestDto.getTitle());
        meeting.setLabel(requestDto.getLabel());
        meeting.setTimeslot(requestDto.getTimeslot());
        meeting.setLocation(requestDto.getLocation());
        meeting.setLink(requestDto.getLink());
        meeting.setDescription(requestDto.getDescription());
        meeting.setHost(requestDto.getHost());

        // TODO: time conflict check

        Meeting savedMeeting = meetingRepository.create(meeting);

        List<ResponseMeetingDto.ParticipantResponseDto> participantResponses = new ArrayList<>();
        if (requestDto.getParticipants() != null) {
            for (RequestCreateMeetingDto.RequestParticipantDto participantDto : requestDto.getParticipants()) {
                MeetingParticipant participant = new MeetingParticipant();
                participant.setMeetingId(savedMeeting.getId());
                participant.setUserId(participantDto.getUserId());
                participant.setStatus(participantDto.getStatus());

                MeetingParticipant savedParticipant = meetingParticipantRepository.create(participant);

                participantResponses.add(ResponseMeetingDto.ParticipantResponseDto.builder()
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
                .build();
    }

    public List<ResponseMeetingDto> getUserMeetings(String userId) {
        List<MeetingParticipant> joinedMeetings = meetingParticipantRepository.findByUserId(userId);
        List<String> meetingIds = joinedMeetings.stream()
                .map(MeetingParticipant::getMeetingId)
                .collect(Collectors.toList());

        if (meetingIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Meeting> meetings = meetingRepository.findMeetingsById(meetingIds);

        return  meetings.stream()
                .map(this::convertMeetingToDto)
                .collect(Collectors.toList());
    }

    public ResponseDetailedMeetingDto getSingleMeetingById(String meetingId) {
        Meeting meeting = meetingRepository.findSingleMeetingById(meetingId);
        if (meeting == null) {
            throw new NoSuchElementException("Meeting not found with ID: " + meetingId);
        }

        List<MeetingParticipant> participants = meetingParticipantRepository.findByMeetingId(meetingId);

        List<ResponseMeetingDto.ParticipantResponseDto> invited = new ArrayList<>();
        List<ResponseMeetingDto.ParticipantResponseDto> accepted = new ArrayList<>();
        List<ResponseMeetingDto.ParticipantResponseDto> declined = new ArrayList<>();

        for (MeetingParticipant participant : participants) {
            ResponseMeetingDto.ParticipantResponseDto dto = ResponseMeetingDto.ParticipantResponseDto.builder()
                    .userId(participant.getUserId())
                    .status(participant.getStatus())
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

        return ResponseDetailedMeetingDto.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .label(meeting.getLabel())
                .timeslot(meeting.getTimeslot())
                .location(meeting.getLocation())
                .link(meeting.getLink())
                .description(meeting.getDescription())
                .host(meeting.getHost())
                .invitedParticipants(invited)
                .acceptedParticipants(accepted)
                .declinedParticipants(declined)
                .build();
    }


    public RequestUpdateMeetingDto updateMeeting(String meetingId, RequestUpdateMeetingDto requestUpdateDto) {
        // TODO: time conflict check

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

        Meeting savedMeeting = meetingRepository.update(meetingId, updatedMeeting);

        List<RequestUpdateMeetingDto.RequestParticipantDto> participantDtos = new ArrayList<>();
        if (requestUpdateDto.getParticipants() != null) {
            List<MeetingParticipant> participants = requestUpdateDto.getParticipants().stream()
                    .map(dto -> {
                        MeetingParticipant participant = new MeetingParticipant();
                        participant.setId(dto.getId());
                        participant.setMeetingId(meetingId);
                        participant.setUserId(dto.getUserId());
                        participant.setStatus(dto.getStatus());
                        return participant;
                    })
                    .collect(Collectors.toList());

            syncParticipants(meetingId, participants);

            participantDtos = requestUpdateDto.getParticipants();
        }

        return RequestUpdateMeetingDto.builder()
                .title(savedMeeting.getTitle())
                .label(savedMeeting.getLabel())
                .timeslot(savedMeeting.getTimeslot())
                .location(savedMeeting.getLocation())
                .link(savedMeeting.getLink())
                .description(savedMeeting.getDescription())
                .host(savedMeeting.getHost())
                .participants(participantDtos)
                .build();
    }

    public ResponseMeetingDto deleteMeeting(String meetingId) {
        meetingParticipantRepository.deleteByMeetingId(meetingId);
        Meeting deletedMeeting = meetingRepository.delete(meetingId);
        return ResponseMeetingDto.builder()
                .title(deletedMeeting.getTitle())
                .build();
    }

    private void syncParticipants(String meetingId, List<MeetingParticipant> newParticipants) {
        List<MeetingParticipant> originalParticipants = meetingParticipantRepository.findByMeetingId(meetingId);
        Map<String, MeetingParticipant> originalParticipantMap = originalParticipants.stream()
                .collect(Collectors.toMap(MeetingParticipant::getUserId, Function.identity()));

        Set<String> incomingUserIds = newParticipants == null
                ? Collections.emptySet()
                : newParticipants.stream()
                .map(MeetingParticipant::getUserId)
                .collect(Collectors.toSet());

        newParticipants.stream()
                .filter(p -> !originalParticipantMap.containsKey(p.getUserId()))
                .forEach(p -> {
                    p.setMeetingId(meetingId);
                    // Don't keep the ID if it's a new participant
                    p.setId(null);
                    meetingParticipantRepository.create(p);
                });

        originalParticipants.stream()
                .filter(p -> !incomingUserIds.contains(p.getUserId()))
                .forEach(p -> meetingParticipantRepository.delete(p.getId()));

        newParticipants.stream()
                .filter(p -> originalParticipantMap.containsKey(p.getUserId()))
                .filter(p -> !Objects.equals(originalParticipantMap.get(p.getUserId()).getStatus(), p.getStatus()))
                .forEach(p -> {
                    MeetingParticipant existingParticipant = originalParticipantMap.get(p.getUserId());
                    p.setId(existingParticipant.getId());
                    p.setMeetingId(meetingId);
                    meetingParticipantRepository.update(existingParticipant.getId(), p);
                });
    }

    private ResponseMeetingDto convertMeetingToDto(Meeting meeting) {
        // Get participants for this meeting
        List<MeetingParticipant> participants =
                meetingParticipantRepository.findByMeetingId(meeting.getId());

        // Convert participants to DTOs
        List<ResponseMeetingDto.ParticipantResponseDto> participantDtos =
                participants.stream()
                        .map(p -> ResponseMeetingDto.ParticipantResponseDto.builder()
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
                .build();
    }

    //    public MeetingRecord createMeetingRecord(String meetingId, MeetingRecord meetingRecord) {
//        meetingRecord.setMeetingId(meetingId);
//        return meetingRecordRepository.create(meetingRecord);
//    }
}
