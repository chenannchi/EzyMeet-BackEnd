package EzyMeet.EzyMeet.controller;

import EzyMeet.EzyMeet.model.PlatformNotification;
import EzyMeet.EzyMeet.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

//    @PostMapping("/replyInvitation")
//    public void replyInvitation(@RequestParam String userId, @RequestParam String meetingId, @RequestParam PlatformNotification.Status status) {
//        notificationService.replyInvitation(userId, meetingId, status);
//    }
//
    @GetMapping("/{userId}")
    public List<PlatformNotification> getUserNotifications(@PathVariable String userId) {
        return notificationService.getUserNotifications(userId);
    }
}
