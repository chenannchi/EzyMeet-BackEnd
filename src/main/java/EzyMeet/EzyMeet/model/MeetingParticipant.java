package EzyMeet.EzyMeet.model;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


public class MeetingParticipant {
    @Getter
    private UUID id;
    @Getter
    private UUID userId;
    @Getter
    private String meetingId;
    @Setter
    @Getter
    private Status status;

    public enum Status {
        INVITED,
        ACCEPTED,
        DECLINED
    }

    public MeetingParticipant(UUID id, UUID userId, String meetingId, Status status) {
        this.id = id;
        this.userId = userId;
        this.meetingId = meetingId;
        this.status = status;
    }


}
