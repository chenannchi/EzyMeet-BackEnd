package EzyMeet.EzyMeet.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PlatformNotification {
    private String id;

    private String title;

    private String recipientId;
    private String meetingId;
    private Status status;
    private Date createdAt;

    private NotificationType notificationType;

    public enum NotificationType {
        INVITATION,
        UPDATED
    }

    public enum Status {
        PENDING,
        REPLIED
    }
}
