package EzyMeet.EzyMeet.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Setter
@Getter
@AllArgsConstructor
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
}
