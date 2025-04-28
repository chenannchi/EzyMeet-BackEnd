package EzyMeet.EzyMeet.model;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Setter
@Getter
@NoArgsConstructor
public class MeetingParticipant {
    private String id;
    private String userId;
    private String meetingId;
    private Status status;

    public enum Status {
        INVITED,
        ACCEPTED,
        DECLINED
    }

    public MeetingParticipant(String id, String userId, String meetingId, Status status) {
        this.id = id;
        this.userId = userId;
        this.meetingId = meetingId;
        this.status = status;
    }
}
