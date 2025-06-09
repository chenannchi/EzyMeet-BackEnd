package EzyMeet.EzyMeet.dto;

import EzyMeet.EzyMeet.model.MeetingParticipant.Status;
import EzyMeet.EzyMeet.model.TimeSlot;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestUpdateMeetingDto {
    private String title;
    private String label;
    private TimeSlot timeslot;
    private String location;
    private String link;
    private String description;
    private String host;
    private String meetingRecord;
    private List<RequestParticipantDto> participants;
    private List<RequestAgendaItemDto> agendaItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestParticipantDto {
        private String id; // Can be null for new participants
        private String userId;
        private Status status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestAgendaItemDto {
        private String topic;
        private String startTime;
        private String endTime;
    }
}