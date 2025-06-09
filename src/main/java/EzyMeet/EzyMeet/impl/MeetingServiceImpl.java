package EzyMeet.EzyMeet.impl;

import EzyMeet.EzyMeet.dto.*;
import EzyMeet.EzyMeet.exception.TimeSlotConflictException;
import EzyMeet.EzyMeet.model.*;
import EzyMeet.EzyMeet.repository.MeetingParticipantRepository;
import EzyMeet.EzyMeet.repository.MeetingRepository;
import EzyMeet.EzyMeet.repository.UserRepository;
import EzyMeet.EzyMeet.service.MeetingService;
import EzyMeet.EzyMeet.service.NotificationService;
import com.google.cloud.spring.data.firestore.FirestoreReactiveOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("MeetingService")
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Autowired
    public MeetingServiceImpl(MeetingRepository meetingRepository, MeetingParticipantRepository meetingParticipantRepository, UserRepository userRepository, NotificationService notificationService) {
        this.meetingRepository = meetingRepository;
        this.meetingParticipantRepository = meetingParticipantRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
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


        // process agendaItem
        List<ResponseMeetingDto.ResponseAgendaItemDto> agendaItemResponses = new ArrayList<>();
        if(requestDto.getAgendaItems() != null) {
            meeting.setAgendaItems((new ArrayList<>()));

            for (RequestCreateMeetingDto.RequestAgendaItemDto agendaItemDto : requestDto.getAgendaItems()) {
                String topic = agendaItemDto.getTopic();
                String startTime = agendaItemDto.getStartTime();
                String endTime = agendaItemDto.getEndTime();

                AgendaItem agendaItem = new AgendaItem(startTime, endTime, topic);
                meeting.getAgendaItems().add(agendaItem);

                ResponseMeetingDto.ResponseAgendaItemDto responseAgendaItem = ResponseMeetingDto.ResponseAgendaItemDto.builder()
                        .topic(topic)
                        .startTime(startTime)
                        .endTime(endTime)
                        .build();

                agendaItemResponses.add(responseAgendaItem);
          }
        }

        Meeting savedMeeting = meetingRepository.create(meeting);

        List<ResponseMeetingDto.ResponseParticipantDto> participantResponses = new ArrayList<>();
        if (requestDto.getParticipants() != null) {
            for (RequestCreateMeetingDto.RequestParticipantDto participantDto : requestDto.getParticipants()) {
                MeetingParticipant participant = new MeetingParticipant();
                participant.setMeetingId(savedMeeting.getId());
                participant.setUserId(participantDto.getUserId());
                participant.setStatus(MeetingParticipant.Status.valueOf("INVITED"));

                MeetingParticipant savedParticipant = meetingParticipantRepository.create(participant);

                participantResponses.add(ResponseMeetingDto.ResponseParticipantDto.builder()
                        .userId(savedParticipant.getUserId())
                        .status(savedParticipant.getStatus())
                        .build());
            }
        }

        if( requestDto.getParticipants() != null && !requestDto.getParticipants().isEmpty()) {

            for (String participantId : requestDto.getParticipants().stream()
                    .map(RequestCreateMeetingDto.RequestParticipantDto::getUserId)
                    .toList()) {
                PlatformNotification notification = new PlatformNotification();
                notification.setTitle(savedMeeting.getTitle());
                notification.setRecipientId(participantId);
                notification.setMeetingId(savedMeeting.getId());
                notification.setStatus(PlatformNotification.Status.PENDING);
                notification.setNotificationType(PlatformNotification.NotificationType.INVITATION); // or UPDATED

                notificationService.createNotification(notification);
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
                .agendaItems(agendaItemResponses)
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

        List<ResponseMeetingInfoDto.ResponseAgendaItemDto> agendaItemDtos =
                meeting.getAgendaItems() != null ?
                        meeting.getAgendaItems().stream()
                                .map(item -> ResponseMeetingInfoDto.ResponseAgendaItemDto.builder()
                                        .topic(item.getTopic())
                                        .startTime(item.getStartTime())
                                        .endTime(item.getEndTime())
                                        .build())
                                .toList() :
                        new ArrayList<>();

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
                .agendaItems(agendaItemDtos)
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
        updatedMeeting.setAgendaItems(
                requestUpdateDto.getAgendaItems() != null ?
                        requestUpdateDto.getAgendaItems().stream()
                                .map(item -> new AgendaItem(item.getStartTime(), item.getEndTime(), item.getTopic()))
                                .collect(Collectors.toList()) :
                        existingMeeting.getAgendaItems()
        );
        Meeting savedMeeting = meetingRepository.update(meetingId, updatedMeeting);

        List<ResponseMeetingDto.ResponseParticipantDto> participantDtos = new ArrayList<>();
        if (requestUpdateDto.getParticipants() != null) {
            participantDtos = requestUpdateDto.getParticipants().stream()
                    .map(dto -> ResponseMeetingDto.ResponseParticipantDto.builder()
                            .userId(dto.getUserId())
                            .status(dto.getStatus())
                            .build())
                    .collect(Collectors.toList());
        }

        if( requestUpdateDto.getParticipants() != null && !requestUpdateDto.getParticipants().isEmpty()) {

            for (String participantId : requestUpdateDto.getParticipants().stream()
                    .map(RequestUpdateMeetingDto.RequestParticipantDto::getUserId)
                    .toList()) {
                PlatformNotification notification = new PlatformNotification();
                notification.setTitle(savedMeeting.getTitle());
                notification.setRecipientId(participantId);
                notification.setMeetingId(savedMeeting.getId());
                notification.setStatus(null);
                notification.setNotificationType(PlatformNotification.NotificationType.UPDATED); // or INVITATION
                notificationService.createNotification(notification);

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
                .participants(participantDtos)
                .meetingRecord(savedMeeting.getMeetingRecord())
                .agendaItems(
                        savedMeeting.getAgendaItems() != null ?
                                savedMeeting.getAgendaItems().stream()
                                        .map(item -> ResponseMeetingDto.ResponseAgendaItemDto.builder()
                                                .topic(item.getTopic())
                                                .startTime(item.getStartTime())
                                                .endTime(item.getEndTime())
                                                .build())
                                        .collect(Collectors.toList()) :
                                new ArrayList<>()
                )
                .build();
    }

    public ResponseMeetingDto deleteMeeting(String meetingId) {
        meetingParticipantRepository.deleteByMeetingId(meetingId);
        Meeting deletedMeeting = meetingRepository.delete(meetingId);
        return ResponseMeetingDto.builder()
                .title(deletedMeeting.getTitle())
                .build();
    }

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
