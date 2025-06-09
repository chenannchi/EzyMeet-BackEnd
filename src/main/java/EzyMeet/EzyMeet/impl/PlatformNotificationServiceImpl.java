package EzyMeet.EzyMeet.impl;

import EzyMeet.EzyMeet.model.MeetingParticipant;
import EzyMeet.EzyMeet.model.PlatformNotification;
import EzyMeet.EzyMeet.repository.MeetingParticipantRepository;
import EzyMeet.EzyMeet.repository.NotificationRepository;
import EzyMeet.EzyMeet.repository.UserRepository;
import EzyMeet.EzyMeet.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlatformNotificationServiceImpl implements NotificationService {


    private final NotificationRepository notificationRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;

    @Autowired
    public PlatformNotificationServiceImpl(NotificationRepository notificationRepository, MeetingParticipantRepository meetingParticipantRepository) {
        this.notificationRepository = notificationRepository;
        this.meetingParticipantRepository = meetingParticipantRepository;
    }


    @Override
    public PlatformNotification createNotification(PlatformNotification notification) {
        notification.setId(UUID.randomUUID().toString());
        notification.setCreatedAt(new Date());
        notificationRepository.createNotification(notification);
        return notification;
    }

//    @Override
//    public void notifyParticipants(List<String> userIds, String meetingTitle, String recipientId, String meetingId, PlatformNotification.NotificationType type) {
//        for (String userId : userIds) {
//            MeetingParticipant.Status participantStatus = getParticipantStatus(userId, meetingId);
//
//            PlatformNotification notification = new PlatformNotification();
//            notification.setId(UUID.randomUUID().toString());
//            notification.setTitle(meetingTitle);
//            notification.setRecipientId(userId);
//            notification.setMeetingId(meetingId);
//
//            if (participantStatus == MeetingParticipant.Status.INVITED) {
//                notification.setStatus(PlatformNotification.Status.PENDING);
//            } else {
//                notification.setStatus(PlatformNotification.Status.REPLIED);
//            }
//
//            notification.setCreatedAt(new Date());
//            notification.setNotificationType(type);
//
//            notificationRepository.createNotification(notification);
//        }
//    }
//
//    private MeetingParticipant.Status getParticipantStatus(String userId, String meetingId) {
//        MeetingParticipant participant = meetingParticipantRepository.findByMeetingIdAndUserId(meetingId, userId);
//        return participant != null ? participant.getStatus() : MeetingParticipant.Status.INVITED;
//    }
//
    @Override
    public List<PlatformNotification> getUserNotifications(String userId) {
        List<PlatformNotification> notifications = notificationRepository.getAllNotificationByUserId(userId);
        if (notifications == null) {
            return Collections.emptyList();
        }
        notifications.sort(Comparator.comparing(PlatformNotification::getCreatedAt, Comparator.nullsLast(Date::compareTo)).reversed());
        return notifications;
    }

    @Override
    public void replyInvitation(String notificationId, PlatformNotification.Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        notificationRepository.replyInvitation(notificationId, status);
    }

}
