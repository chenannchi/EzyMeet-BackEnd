package EzyMeet.EzyMeet.controller;

import EzyMeet.EzyMeet.dto.RequestReplyInvitationDTO;
import EzyMeet.EzyMeet.model.PlatformNotification;
import EzyMeet.EzyMeet.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/replyInvitation")
    public void replyInvitation(@RequestBody RequestReplyInvitationDTO request) {
        notificationService.replyInvitation(request.getNotificationId(), request.getStatus());
    }

    @GetMapping("/{userId}")
    public List<PlatformNotification> getUserNotifications(@PathVariable String userId) {
        return notificationService.getUserNotifications(userId);
    }
}
