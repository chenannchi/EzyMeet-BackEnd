package EzyMeet.EzyMeet.dto;

import EzyMeet.EzyMeet.model.MeetingParticipant;
import EzyMeet.EzyMeet.model.TimeSlot;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestCreateMeetingDto {
    private String title;
    private String label;
    private TimeSlot timeslot;
    private String location;
    private String link;
    private String description;
    private String host;
    private List<RequestParticipantDto> participants;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestParticipantDto {
        private String userId;
        private MeetingParticipant.Status status;
    }
}