package EzyMeet.EzyMeet.impl;

import EzyMeet.EzyMeet.model.MeetingParticipant;
import EzyMeet.EzyMeet.model.PlatformNotification;
import EzyMeet.EzyMeet.repository.MeetingParticipantRepository;
import EzyMeet.EzyMeet.repository.NotificationRepository;
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
    public void createNotification(PlatformNotification notification) {
        notification.setId(UUID.randomUUID().toString());
        notification.setCreatedAt(new Date());
        notificationRepository.createNotification(notification);
    }

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

        PlatformNotification notification = notificationRepository.getNotificationById(notificationId);
        if (notification != null) {
            MeetingParticipant.Status participantStatus;
            if (status == PlatformNotification.Status.ACCEPTED) {
                participantStatus = MeetingParticipant.Status.ACCEPTED;
            } else if (status == PlatformNotification.Status.REJECTED) {
                participantStatus = MeetingParticipant.Status.DECLINED;
            } else {
                participantStatus = MeetingParticipant.Status.INVITED;
            }
            meetingParticipantRepository.updateStatus(notification.getMeetingId(), notification.getRecipientId(), participantStatus);
        }

    }

}
