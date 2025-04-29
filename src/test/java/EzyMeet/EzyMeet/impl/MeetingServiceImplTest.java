package EzyMeet.EzyMeet.impl;

import EzyMeet.EzyMeet.dto.RequestCreateMeetingDto;
import EzyMeet.EzyMeet.dto.RequestUpdateMeetingDto;
import EzyMeet.EzyMeet.dto.ResponseDetailedMeetingDto;
import EzyMeet.EzyMeet.dto.ResponseMeetingDto;
import EzyMeet.EzyMeet.exception.TimeSlotConflictException;
import EzyMeet.EzyMeet.model.Meeting;
import EzyMeet.EzyMeet.model.MeetingParticipant;
import EzyMeet.EzyMeet.model.TimeSlot;
import EzyMeet.EzyMeet.repository.MeetingParticipantRepository;
import EzyMeet.EzyMeet.repository.MeetingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.time.ZonedDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MeetingServiceImplTest {
    @Mock
    private MeetingRepository meetingRepository;
    @Mock
    private MeetingParticipantRepository meetingParticipantRepository;
    @InjectMocks
    private MeetingServiceImpl meetingService;

    @Test
    public void createMeetingSuccess() {

        String startTimeStr = "2023-12-15T18:00:00.000+00:00";
        String endTimeStr = "2023-12-15T20:00:00.000+00:00";
        TimeSlot timeSlot = convertTimeSlot(startTimeStr, endTimeStr);

        RequestCreateMeetingDto requestDto = new RequestCreateMeetingDto();
        requestDto.setTitle("Test Meeting");
        requestDto.setLabel("Test");
        requestDto.setTimeslot(timeSlot);
        requestDto.setLocation("Test Location");
        requestDto.setLink("https://test.com");
        requestDto.setDescription("Test Description");
        requestDto.setHost("userHost");

        List<RequestCreateMeetingDto.RequestParticipantDto> participants = new ArrayList<>();
        RequestCreateMeetingDto.RequestParticipantDto participant = new RequestCreateMeetingDto.RequestParticipantDto();
        participant.setUserId("user456");
        participant.setStatus(MeetingParticipant.Status.INVITED);
        participants.add(participant);
        requestDto.setParticipants(participants);

        Meeting savedMeeting = new Meeting();
        savedMeeting.setId("meeting123");
        savedMeeting.setHost("userHost");
        savedMeeting.setTitle("Test Meeting");
        savedMeeting.setLabel("Test");
        savedMeeting.setTimeslot(timeSlot);
        savedMeeting.setLocation("Test Location");
        savedMeeting.setLink("https://test.com");
        savedMeeting.setDescription("Test Description");
        savedMeeting.setMeetingRecord("");

        MeetingParticipant savedParticipant = new MeetingParticipant();
        savedParticipant.setUserId("user456");
        savedParticipant.setMeetingId("meeting123");
        savedParticipant.setStatus(MeetingParticipant.Status.INVITED);

        when(meetingRepository.create(any(Meeting.class))).thenReturn(savedMeeting);
        when(meetingParticipantRepository.findByUserId(anyString())).thenReturn(Collections.emptyList());
        when(meetingRepository.findMeetingsByHost(anyString())).thenReturn(Collections.emptyList());
        when(meetingRepository.findMeetingsById(any())).thenReturn(Collections.emptyList());
        when(meetingParticipantRepository.create(any(MeetingParticipant.class))).thenReturn(savedParticipant);

        ResponseMeetingDto result = meetingService.createMeeting(requestDto);

        assertNotNull(result);
        assertEquals("meeting123", result.getId());
        assertEquals("userHost", result.getHost());
        assertEquals("Test Meeting", result.getTitle());
        assertEquals("Test", result.getLabel());
        assertEquals(1, result.getParticipants().size());
        assertEquals("user456", result.getParticipants().get(0).getUserId());
        assertEquals(MeetingParticipant.Status.INVITED, result.getParticipants().get(0).getStatus());
        assertEquals(timeSlot, result.getTimeslot());
        assertEquals("Test Location", result.getLocation());
        assertEquals("https://test.com", result.getLink());
        assertEquals("Test Description", result.getDescription());
        assertEquals("", result.getMeetingRecord());

        verify(meetingRepository).create(any(Meeting.class));
        verify(meetingParticipantRepository).create(any(MeetingParticipant.class));
    }
@Test
public void createMeetingTimeSlotConflict() {
    String startTimeStr = "2025-12-15T18:00:00.000+00:00";
    String endTimeStr = "2025-12-15T20:00:00.000+00:00";
    TimeSlot newTimeSlot = convertTimeSlot(startTimeStr, endTimeStr);

    RequestCreateMeetingDto requestDto = new RequestCreateMeetingDto();
    requestDto.setTitle("Conflicting Meeting");
    requestDto.setLabel("Conflict");
    requestDto.setTimeslot(newTimeSlot);
    requestDto.setLocation("Conflict Location");
    requestDto.setLink("https://conflict.com");
    requestDto.setDescription("This meeting will conflict");
    requestDto.setHost("userHost");

    TimeSlot existingTimeSlot = convertTimeSlot("2025-12-15T19:00:00.000+00:00", "2025-12-15T21:00:00.000+00:00");
    Meeting existingMeeting = new Meeting();
    existingMeeting.setId("existingMeeting123");
    existingMeeting.setTimeslot(existingTimeSlot);

    when(meetingRepository.findMeetingsByHost(anyString())).thenReturn(Collections.singletonList(existingMeeting));
    when(meetingParticipantRepository.findByUserId(anyString())).thenReturn(Collections.emptyList());

    TimeSlotConflictException exception = assertThrows(TimeSlotConflictException.class, () -> {
        meetingService.createMeeting(requestDto);
    });

    assertEquals("The provided time slot conflicts with existing time slots.", exception.getMessage());
}

    @Test
    public void createMeetingWithNullParticipants() {
        String startTimeStr = "2025-12-15T18:00:00.000+00:00";
        String endTimeStr = "2025-12-15T20:00:00.000+00:00";
        TimeSlot timeSlot = convertTimeSlot(startTimeStr, endTimeStr);

        RequestCreateMeetingDto requestDto = new RequestCreateMeetingDto();
        requestDto.setTitle("Test Meeting");
        requestDto.setLabel("Test");
        requestDto.setTimeslot(timeSlot);
        requestDto.setLocation("Test Location");
        requestDto.setLink("https://test.com");
        requestDto.setDescription("Test Description");
        requestDto.setHost("userHost");
        requestDto.setParticipants(null); // Participants are null

        Meeting savedMeeting = new Meeting();
        savedMeeting.setId("meeting123");
        savedMeeting.setHost("userHost");
        savedMeeting.setTitle("Test Meeting");
        savedMeeting.setLabel("Test");
        savedMeeting.setTimeslot(timeSlot);
        savedMeeting.setLocation("Test Location");
        savedMeeting.setLink("https://test.com");
        savedMeeting.setDescription("Test Description");
        savedMeeting.setMeetingRecord("");

        when(meetingRepository.create(any(Meeting.class))).thenReturn(savedMeeting);
        when(meetingParticipantRepository.findByUserId(anyString())).thenReturn(Collections.emptyList());
        when(meetingRepository.findMeetingsByHost(anyString())).thenReturn(Collections.emptyList());
        when(meetingRepository.findMeetingsById(any())).thenReturn(Collections.emptyList());

        ResponseMeetingDto result = meetingService.createMeeting(requestDto);

        assertNotNull(result);
        assertEquals("meeting123", result.getId());
        assertEquals("userHost", result.getHost());
        assertEquals("Test Meeting", result.getTitle());
        assertEquals("Test", result.getLabel());
        assertEquals(0, result.getParticipants().size());
        assertEquals(timeSlot, result.getTimeslot());
        assertEquals("Test Location", result.getLocation());
        assertEquals("https://test.com", result.getLink());
        assertEquals("Test Description", result.getDescription());
        assertEquals("", result.getMeetingRecord());

        verify(meetingRepository).create(any(Meeting.class));
        verify(meetingParticipantRepository, never()).create(any(MeetingParticipant.class));
    }

    @Test
    public void getUserMeetingsSuccess() {
        String userId = "testUser123";
        String startTimeStr1 = "2025-12-15T18:00:00.000+00:00";
        String endTimeStr1 = "2025-12-15T20:00:00.000+00:00";
        TimeSlot timeSlot1 = convertTimeSlot(startTimeStr1, endTimeStr1);
        String startTimeStr2 = "2025-12-21T14:00:00.000+00:00";
        String endTimeStr2= " 2025-12-21T15:00:00.000+00:00";
        TimeSlot timeSlot2 = convertTimeSlot(startTimeStr1, endTimeStr1);


        Meeting meeting1 = new Meeting();
        meeting1.setId("meeting1");
        meeting1.setTitle("User's Meeting 1");
        meeting1.setLabel("Meeting 1");
        meeting1.setTimeslot(timeSlot1);
        meeting1.setLocation("Location 1");
        meeting1.setLink("https://meeting1.com");
        meeting1.setDescription("Description 1");
        meeting1.setHost(userId);
        meeting1.setMeetingRecord("");

        Meeting meeting2 = new Meeting();
        meeting2.setId("meeting2");
        meeting2.setTitle("User's Meeting 2");
        meeting2.setLabel("Meeting 2");
        meeting2.setTimeslot(timeSlot2);
        meeting2.setLocation("Location 2");
        meeting2.setLink("https://meeting2.com");
        meeting2.setDescription("Description 2");
        meeting2.setHost("anotherUser");
        meeting2.setMeetingRecord("");

        MeetingParticipant participant1 = new MeetingParticipant();
        participant1.setId("participant1");
        participant1.setUserId(userId);
        participant1.setMeetingId("meeting2");
        participant1.setStatus(MeetingParticipant.Status.ACCEPTED);

        when(meetingRepository.findMeetingsByHost(userId)).thenReturn(Collections.singletonList(meeting1));
        when(meetingParticipantRepository.findByUserId(userId)).thenReturn(Collections.singletonList(participant1));
        when(meetingRepository.findMeetingsById(Collections.singletonList("meeting2"))).thenReturn(Collections.singletonList(meeting2));
        when(meetingParticipantRepository.findByMeetingId("meeting1")).thenReturn(Collections.emptyList());
        when(meetingParticipantRepository.findByMeetingId("meeting2")).thenReturn(Collections.singletonList(participant1));

        List<ResponseMeetingDto> result = meetingService.getUserMeetings(userId);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(result.stream().anyMatch(m -> m.getId().equals("meeting1")));
        assertTrue(result.stream().anyMatch(m -> m.getId().equals("meeting2")));

        ResponseMeetingDto meeting1Dto = result.stream().filter(m -> m.getId().equals("meeting1")).findFirst().orElse(null);
        assertNotNull(meeting1Dto);
        assertEquals("User's Meeting 1", meeting1Dto.getTitle());
        assertEquals("Meeting 1", meeting1Dto.getLabel());
        assertEquals(userId, meeting1Dto.getHost());

        ResponseMeetingDto meeting2Dto = result.stream().filter(m -> m.getId().equals("meeting2")).findFirst().orElse(null);
        assertNotNull(meeting2Dto);
        assertEquals("User's Meeting 2", meeting2Dto.getTitle());
        assertEquals("Meeting 2", meeting2Dto.getLabel());
        assertEquals("anotherUser", meeting2Dto.getHost());

        verify(meetingRepository).findMeetingsByHost(userId);
        verify(meetingParticipantRepository).findByUserId(userId);
        verify(meetingRepository).findMeetingsById(Collections.singletonList("meeting2"));
        verify(meetingParticipantRepository, times(1)).findByMeetingId("meeting1");
        verify(meetingParticipantRepository, times(1)).findByMeetingId("meeting2");
    }

    @Test
    public void getSingleMeetingByIdSuccess() {
        String meetingId = "meeting123";
        String startTimeStr = "2025-12-15T18:00:00.000+00:00";
        String endTimeStr = "2025-12-15T20:00:00.000+00:00";
        TimeSlot timeSlot = convertTimeSlot(startTimeStr, endTimeStr);

        Meeting meeting = new Meeting();
        meeting.setId(meetingId);
        meeting.setTitle("Test Meeting");
        meeting.setLabel("Test Label");
        meeting.setTimeslot(timeSlot);
        meeting.setLocation("Test Location");
        meeting.setLink("https://test.com");
        meeting.setDescription("Test Description");
        meeting.setHost("userHost");
        meeting.setMeetingRecord("Meeting notes");

        MeetingParticipant invitedParticipant = new MeetingParticipant();
        invitedParticipant.setId("participant1");
        invitedParticipant.setUserId("userInvited");
        invitedParticipant.setMeetingId(meetingId);
        invitedParticipant.setStatus(MeetingParticipant.Status.INVITED);

        MeetingParticipant acceptedParticipant = new MeetingParticipant();
        acceptedParticipant.setId("participant2");
        acceptedParticipant.setUserId("userAccepted");
        acceptedParticipant.setMeetingId(meetingId);
        acceptedParticipant.setStatus(MeetingParticipant.Status.ACCEPTED);

        MeetingParticipant declinedParticipant = new MeetingParticipant();
        declinedParticipant.setId("participant3");
        declinedParticipant.setUserId("userDeclined");
        declinedParticipant.setMeetingId(meetingId);
        declinedParticipant.setStatus(MeetingParticipant.Status.DECLINED);

        List<MeetingParticipant> participants = List.of(acceptedParticipant, invitedParticipant, declinedParticipant);

        when(meetingRepository.findSingleMeetingById(meetingId)).thenReturn(meeting);
        when(meetingParticipantRepository.findByMeetingId(meetingId)).thenReturn(participants);

        ResponseDetailedMeetingDto result = meetingService.getSingleMeetingById(meetingId);

        assertNotNull(result);
        assertEquals(meetingId, result.getId());
        assertEquals("Test Meeting", result.getTitle());
        assertEquals("Test Label", result.getLabel());
        assertEquals(timeSlot, result.getTimeslot());
        assertEquals("Test Location", result.getLocation());
        assertEquals("https://test.com", result.getLink());
        assertEquals("Test Description", result.getDescription());
        assertEquals("userHost", result.getHost());
        assertEquals("Meeting notes", result.getMeetingRecord());

        assertEquals(1, result.getInvitedParticipants().size());
        assertEquals("userInvited", result.getInvitedParticipants().get(0).getUserId());
        assertEquals(MeetingParticipant.Status.INVITED, result.getInvitedParticipants().get(0).getStatus());

        assertEquals(1, result.getAcceptedParticipants().size());
        assertEquals("userAccepted", result.getAcceptedParticipants().get(0).getUserId());
        assertEquals(MeetingParticipant.Status.ACCEPTED, result.getAcceptedParticipants().get(0).getStatus());

        assertEquals(1, result.getDeclinedParticipants().size());
        assertEquals("userDeclined", result.getDeclinedParticipants().get(0).getUserId());
        assertEquals(MeetingParticipant.Status.DECLINED, result.getDeclinedParticipants().get(0).getStatus());

        verify(meetingRepository).findSingleMeetingById(meetingId);
        verify(meetingParticipantRepository).findByMeetingId(meetingId);
    }

    @Test
    public void getSingleMeetingByIdNotFound() {
        String meetingId = "nonexistentMeeting";
        when(meetingRepository.findSingleMeetingById(meetingId)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> {
            meetingService.getSingleMeetingById(meetingId);
        });

        verify(meetingRepository).findSingleMeetingById(meetingId);
        verify(meetingParticipantRepository, never()).findByMeetingId(anyString());
    }

    @Test
    public void updateMeetingSuccess() {
        String meetingId = "meeting123";
        String userId = "user456";
        TimeSlot timeSlot = convertTimeSlot("2025-12-15T18:00:00.000+00:00", "2025-12-15T20:00:00.000+00:00");

        Meeting existingMeeting = new Meeting();
        existingMeeting.setId(meetingId);
        existingMeeting.setTitle("Original Title");
        existingMeeting.setLabel("Original Label");
        existingMeeting.setTimeslot(timeSlot);
        existingMeeting.setLocation("Original Location");
        existingMeeting.setLink("https://original.com");
        existingMeeting.setDescription("Original Description");
        existingMeeting.setHost(userId);
        existingMeeting.setMeetingRecord("Original notes");

        RequestUpdateMeetingDto requestUpdateDto = new RequestUpdateMeetingDto();
        requestUpdateDto.setTitle("Updated Title");
        requestUpdateDto.setLabel("Updated Label");
        requestUpdateDto.setTimeslot(timeSlot);
        requestUpdateDto.setLocation("Updated Location");
        requestUpdateDto.setLink("https://updated.com");
        requestUpdateDto.setDescription("Updated Description");
        requestUpdateDto.setHost(userId);
        requestUpdateDto.setMeetingRecord("Updated notes");

        RequestUpdateMeetingDto.RequestParticipantDto participantDto = new RequestUpdateMeetingDto.RequestParticipantDto();
        participantDto.setUserId("participant123");
        participantDto.setStatus(MeetingParticipant.Status.ACCEPTED);
        requestUpdateDto.setParticipants(Collections.singletonList(participantDto));

        Meeting updatedMeeting = new Meeting();
        updatedMeeting.setId(meetingId);
        updatedMeeting.setTitle("Updated Title");
        updatedMeeting.setLabel("Updated Label");
        updatedMeeting.setTimeslot(timeSlot);
        updatedMeeting.setLocation("Updated Location");
        updatedMeeting.setLink("https://updated.com");
        updatedMeeting.setDescription("Updated Description");
        updatedMeeting.setHost(userId);
        updatedMeeting.setMeetingRecord("Updated notes");

        when(meetingRepository.findSingleMeetingById(meetingId)).thenReturn(existingMeeting);
        when(meetingRepository.update(eq(meetingId), any(Meeting.class))).thenReturn(updatedMeeting);
        when(meetingRepository.findMeetingsById(anyList())).thenReturn(Collections.emptyList());
        when(meetingRepository.findMeetingsByHost(userId)).thenReturn(Collections.emptyList());

        ResponseMeetingDto result = meetingService.updateMeeting(meetingId, requestUpdateDto);

        assertNotNull(result);
        assertEquals(meetingId, result.getId());
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Label", result.getLabel());
        assertEquals(timeSlot, result.getTimeslot());
        assertEquals("Updated Location", result.getLocation());
        assertEquals("https://updated.com", result.getLink());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(userId, result.getHost());
        assertEquals("Updated notes", result.getMeetingRecord());
        assertEquals(1, result.getParticipants().size());
        assertEquals("participant123", result.getParticipants().get(0).getUserId());
        assertEquals(MeetingParticipant.Status.ACCEPTED, result.getParticipants().get(0).getStatus());

        verify(meetingRepository).findSingleMeetingById(meetingId);
        verify(meetingRepository).update(eq(meetingId), any(Meeting.class));
        verify(meetingParticipantRepository).findByUserId(userId);
        verify(meetingRepository).findMeetingsByHost(userId);

    }

    @Test
    public void updateMeetingTimeSlotConflict() {
        String meetingId = "meeting123";
        String userId = "user456";

        Meeting existingConflictingMeeting = new Meeting();
        existingConflictingMeeting.setId("otherMeeting");
        existingConflictingMeeting.setHost(userId);
        TimeSlot conflictingTimeSlot = convertTimeSlot("2025-12-20T19:00:00.000+00:00", "2025-12-20T21:00:00.000+00:00");
        existingConflictingMeeting.setTimeslot(conflictingTimeSlot);

        Meeting existingMeeting = new Meeting();
        existingMeeting.setId(meetingId);
        existingMeeting.setTitle("Original Title");
        existingMeeting.setLabel("Original Label");
        TimeSlot originalTimeSlot = convertTimeSlot("2025-12-15T18:00:00.000+00:00", "2025-12-15T20:00:00.000+00:00");
        existingMeeting.setTimeslot(originalTimeSlot);
        existingMeeting.setLocation("Original Location");
        existingMeeting.setLink("https://original.com");
        existingMeeting.setDescription("Original Description");
        existingMeeting.setHost(userId);
        existingMeeting.setMeetingRecord("Original notes");

        RequestUpdateMeetingDto requestUpdateDto = new RequestUpdateMeetingDto();
        requestUpdateDto.setTitle("Updated Title");
        requestUpdateDto.setLabel("Updated Label");
        TimeSlot newConflictingTimeSlot = convertTimeSlot("2025-12-20T20:00:00.000+00:00", "2025-12-20T22:00:00.000+00:00");
        requestUpdateDto.setTimeslot(newConflictingTimeSlot);
        requestUpdateDto.setLocation("Updated Location");
        requestUpdateDto.setLink("https://updated.com");
        requestUpdateDto.setDescription("Updated Description");
        requestUpdateDto.setHost(userId);
        requestUpdateDto.setMeetingRecord("Updated notes");


        when(meetingRepository.findMeetingsByHost(userId)).thenReturn(List.of(existingConflictingMeeting));
        when(meetingParticipantRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(meetingRepository.findMeetingsById(anyList())).thenReturn(Collections.emptyList());


        assertThrows(TimeSlotConflictException.class, () -> {
            meetingService.updateMeeting(meetingId, requestUpdateDto);
        });

        verify(meetingRepository).findMeetingsByHost(userId);
        verify(meetingRepository).findMeetingsById(anyList());
        verify(meetingParticipantRepository).findByUserId(userId);
        verify(meetingRepository, never()).update(anyString(), any(Meeting.class));
    }

    @Test
    public void updateMeetingExistingMeetingIsNull() {
        String meetingId = "nonexistentMeeting";
        String userId = "user456";

        RequestUpdateMeetingDto requestUpdateDto = new RequestUpdateMeetingDto();
        requestUpdateDto.setTitle("Updated Title");
        requestUpdateDto.setLabel("Updated Label");
        TimeSlot timeSlot = convertTimeSlot("2025-12-15T18:00:00.000+00:00", "2025-12-15T20:00:00.000+00:00");
        requestUpdateDto.setTimeslot(timeSlot);
        requestUpdateDto.setLocation("Updated Location");
        requestUpdateDto.setLink("https://updated.com");
        requestUpdateDto.setDescription("Updated Description");
        requestUpdateDto.setHost(userId);
        requestUpdateDto.setMeetingRecord("Updated notes");

        when(meetingRepository.findMeetingsByHost(userId)).thenReturn(Collections.emptyList());
        when(meetingParticipantRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(meetingRepository.findMeetingsById(anyList())).thenReturn(Collections.emptyList());
        when(meetingRepository.findSingleMeetingById(meetingId)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> {
            meetingService.updateMeeting(meetingId, requestUpdateDto);
        });

        verify(meetingRepository).findMeetingsByHost(userId);
        verify(meetingRepository).findSingleMeetingById(meetingId);
        verify(meetingRepository).findMeetingsById(anyList());
        verify(meetingParticipantRepository).findByUserId(userId);
        verify(meetingRepository).update(eq(meetingId), any(Meeting.class));

    }

    @Test
    public void updateMeetingWithNullParticipants() {
        String meetingId = "meeting123";
        String userId = "user456";
        TimeSlot timeSlot = convertTimeSlot("2025-12-15T18:00:00.000+00:00", "2025-12-15T20:00:00.000+00:00");

        Meeting existingMeeting = new Meeting();
        existingMeeting.setId(meetingId);
        existingMeeting.setTitle("Original Title");
        existingMeeting.setLabel("Original Label");
        existingMeeting.setTimeslot(timeSlot);
        existingMeeting.setLocation("Original Location");
        existingMeeting.setLink("https://original.com");
        existingMeeting.setDescription("Original Description");
        existingMeeting.setHost(userId);
        existingMeeting.setMeetingRecord("Original notes");

        RequestUpdateMeetingDto requestUpdateDto = new RequestUpdateMeetingDto();
        requestUpdateDto.setTitle("Updated Title");
        requestUpdateDto.setLabel("Updated Label");
        requestUpdateDto.setTimeslot(timeSlot);
        requestUpdateDto.setLocation("Updated Location");
        requestUpdateDto.setLink("https://updated.com");
        requestUpdateDto.setDescription("Updated Description");
        requestUpdateDto.setHost(userId);
        requestUpdateDto.setMeetingRecord("Updated notes");
        requestUpdateDto.setParticipants(null);

        Meeting updatedMeeting = new Meeting();
        updatedMeeting.setId(meetingId);
        updatedMeeting.setTitle("Updated Title");
        updatedMeeting.setLabel("Updated Label");
        updatedMeeting.setTimeslot(timeSlot);
        updatedMeeting.setLocation("Updated Location");
        updatedMeeting.setLink("https://updated.com");
        updatedMeeting.setDescription("Updated Description");
        updatedMeeting.setHost(userId);
        updatedMeeting.setMeetingRecord("Updated notes");

        when(meetingRepository.findMeetingsByHost(userId)).thenReturn(Collections.emptyList());
        when(meetingParticipantRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(meetingRepository.findMeetingsById(anyList())).thenReturn(Collections.emptyList());
        when(meetingRepository.findSingleMeetingById(meetingId)).thenReturn(existingMeeting);
        when(meetingRepository.update(eq(meetingId), any(Meeting.class))).thenReturn(updatedMeeting);

        ResponseMeetingDto result = meetingService.updateMeeting(meetingId, requestUpdateDto);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Label", result.getLabel());
        assertEquals(timeSlot, result.getTimeslot());
        assertEquals("Updated Location", result.getLocation());
        assertEquals("https://updated.com", result.getLink());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(userId, result.getHost());
        assertEquals("Updated notes", result.getMeetingRecord());
        assertEquals(0, result.getParticipants().size());

        verify(meetingRepository).findMeetingsByHost(userId);
        verify(meetingParticipantRepository).findByUserId(userId);
        verify(meetingRepository).findMeetingsById(anyList());
        verify(meetingRepository).findSingleMeetingById(meetingId);
        verify(meetingRepository, never()).update(anyString(), any(Meeting.class));
    }

    @Test
    public void deleteMeetingSuccess() {
        String meetingId = "meeting123";
        String startTimeStr = "2025-12-15T18:00:00.000+00:00";
        String endTimeStr = "2025-12-15T20:00:00.000+00:00";
        TimeSlot timeSlot = convertTimeSlot(startTimeStr, endTimeStr);

        MeetingParticipant participant = new MeetingParticipant();
        participant.setId("participant1");
        participant.setUserId("userParticipant");
        participant.setMeetingId(meetingId);
        participant.setStatus(MeetingParticipant.Status.ACCEPTED);

        Meeting deletedMeeting = new Meeting();
        deletedMeeting.setId(meetingId);
        deletedMeeting.setTitle("Meeting To Delete");
        deletedMeeting.setLabel("Test Label");
        deletedMeeting.setDescription("Test Description");
        deletedMeeting.setHost("userHost");
        deletedMeeting.setTimeslot(timeSlot);
        deletedMeeting.setLocation("Test Location");
        deletedMeeting.setLink("https://test.com");
        deletedMeeting.setMeetingRecord("Meeting notes");

        doNothing().when(meetingParticipantRepository).deleteByMeetingId(meetingId);
        when(meetingRepository.delete(meetingId)).thenReturn(deletedMeeting);

        ResponseMeetingDto result = meetingService.deleteMeeting(meetingId);

        assertNotNull(result);
        assertEquals("Meeting To Delete", result.getTitle());

        verify(meetingParticipantRepository).deleteByMeetingId(meetingId);
        verify(meetingRepository).delete(meetingId);
    }


    private TimeSlot convertTimeSlot(String startTimeStr, String endTimeStr) {
        ZonedDateTime startZdt = ZonedDateTime.parse(startTimeStr);
        ZonedDateTime endZdt = ZonedDateTime.parse(endTimeStr);

        Date startDate = Date.from(startZdt.toInstant());
        Date endDate = Date.from(endZdt.toInstant());

        return new TimeSlot(startDate, endDate);
    }
}
