package EzyMeet.EzyMeet.dto;

import EzyMeet.EzyMeet.model.MeetingParticipant;
import EzyMeet.EzyMeet.model.TimeSlot;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseMeetingDto {
    private String id;
    private String title;
    private String label;
    private TimeSlot timeslot;
    private String location;
    private String link;
    private String description;
    private String host;
    private List<ResponseParticipantDto> participants;
    private String meetingRecord;
    private List<ResponseAgendaItemDto> agendaItems;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResponseParticipantDto {
        private String userId;
        private MeetingParticipant.Status status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResponseAgendaItemDto {
        private String topic;
        private String startTime;
        private String endTime;
    }
}