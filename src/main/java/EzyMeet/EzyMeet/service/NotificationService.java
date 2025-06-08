package EzyMeet.EzyMeet.service;

import EzyMeet.EzyMeet.model.PlatformNotification;

import java.util.List;

public interface NotificationService {
    PlatformNotification createNotification(PlatformNotification notification);
//    void notifyParticipants(List<String> userIds, String meetingTitle, String recipientId, String meetingId, PlatformNotification.NotificationType type);
    List<PlatformNotification> getUserNotifications(String userId);
//    void replyInvitation(String userId, String meetingId, PlatformNotification.Status status);
}
