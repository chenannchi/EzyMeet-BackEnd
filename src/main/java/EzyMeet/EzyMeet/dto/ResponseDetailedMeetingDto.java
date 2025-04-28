package EzyMeet.EzyMeet.dto;

import EzyMeet.EzyMeet.model.MeetingParticipant;
import EzyMeet.EzyMeet.model.TimeSlot;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ResponseDetailedMeetingDto {
    private String id;
    private String title;
    private String label;
    private TimeSlot timeslot;
    private String location;
    private String link;
    private String description;
    private String host;
    private List<ResponseMeetingDto.ParticipantResponseDto> invitedParticipants;
    private List<ResponseMeetingDto.ParticipantResponseDto> acceptedParticipants;
    private List<ResponseMeetingDto.ParticipantResponseDto> declinedParticipants;
}