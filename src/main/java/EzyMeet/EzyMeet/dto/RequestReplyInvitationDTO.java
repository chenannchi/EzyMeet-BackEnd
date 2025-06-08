package EzyMeet.EzyMeet.dto;

import EzyMeet.EzyMeet.model.PlatformNotification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RequestReplyInvitationDTO {
    private String notificationId;
    private PlatformNotification.Status status;


}
