package EzyMeet.EzyMeet.dto;

import EzyMeet.EzyMeet.model.MeetingParticipant;
import EzyMeet.EzyMeet.model.TimeSlot;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseMeetingInfoDto {
    private String id;
    private String title;
    private String label;
    private TimeSlot timeslot;
    private String location;
    private String link;
    private String description;
    private String host;
    private String meetingRecord;
    private List<ResponseMeetingInfoDto.ResponseParticipantDto> invitedParticipants;
    private List<ResponseMeetingInfoDto.ResponseParticipantDto> acceptedParticipants;
    private List<ResponseMeetingInfoDto.ResponseParticipantDto> declinedParticipants;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResponseParticipantDto {
        private String userId;
        private String email;
        private String name;
    }
}
