package EzyMeet.EzyMeet.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


public class MeetingParticipant {
    @Getter
    private UUID id;
    @Getter
    private UUID userId;
    @Getter
    private UUID meetingId;
    @Setter
    @Getter
    private Status status;

    public enum Status {
        INVITED,
        ACCEPTED,
        DECLINED
    }

    public MeetingParticipant(UUID id, UUID userId, UUID meetingId, Status status) {
        this.id = id;
        this.userId = userId;
        this.meetingId = meetingId;
        this.status = status;
    }

}
