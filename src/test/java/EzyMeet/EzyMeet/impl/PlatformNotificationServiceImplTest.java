package EzyMeet.EzyMeet.impl;

import EzyMeet.EzyMeet.model.MeetingParticipant;
import EzyMeet.EzyMeet.model.PlatformNotification;
import EzyMeet.EzyMeet.repository.MeetingParticipantRepository;
import EzyMeet.EzyMeet.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlatformNotificationServiceImplTest {
    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private MeetingParticipantRepository meetingParticipantRepository;

    @InjectMocks
    private PlatformNotificationServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateNotification() {
        PlatformNotification notification = new PlatformNotification();
        service.createNotification(notification);
        assertNotNull(notification.getId());
        assertNotNull(notification.getCreatedAt());
        verify(notificationRepository).createNotification(notification);
    }

    @Test
    void testCreateNotEmptyNotification(){
        PlatformNotification notification = new PlatformNotification();
        notification.setTitle("Test message");
        notification.setRecipientId("user1");
        notification.setMeetingId("meeting1");
        notification.setStatus(PlatformNotification.Status.PENDING);

        service.createNotification(notification);

        assertNotNull(notification.getId());
        assertNotNull(notification.getCreatedAt());

        assertEquals("Test message", notification.getTitle());
        assertEquals("user1", notification.getRecipientId());
        assertEquals("meeting1", notification.getMeetingId());
        assertEquals(PlatformNotification.Status.PENDING, notification.getStatus());

        verify(notificationRepository).createNotification(notification);
    }

    @Test
    void testGetUserNotifications_SortedAndNotNull() {
        PlatformNotification n1 = new PlatformNotification();
        n1.setCreatedAt(new Date(System.currentTimeMillis() - 1000));
        PlatformNotification n2 = new PlatformNotification();
        n2.setCreatedAt(new Date());
        List<PlatformNotification> notifications = Arrays.asList(n1, n2);

        when(notificationRepository.getAllNotificationByUserId("user1")).thenReturn(notifications);

        List<PlatformNotification> result = service.getUserNotifications("user1");
        assertEquals(2, result.size());
        assertTrue(result.get(0).getCreatedAt().after(result.get(1).getCreatedAt()));
    }

    @Test
    void testGetUserNotifications_NullList() {
        when(notificationRepository.getAllNotificationByUserId("user1")).thenReturn(null);
        List<PlatformNotification> result = service.getUserNotifications("user1");
        assertTrue(result.isEmpty());
    }

    @Test
    void testReplyInvitation_Accepted() {
        PlatformNotification notification = new PlatformNotification();
        notification.setMeetingId("m1");
        notification.setRecipientId("u1");
        when(notificationRepository.getNotificationById("n1")).thenReturn(notification);

        service.replyInvitation("n1", PlatformNotification.Status.ACCEPTED);

        verify(notificationRepository).replyInvitation("n1", PlatformNotification.Status.ACCEPTED);
        verify(meetingParticipantRepository).updateStatus("m1", "u1", MeetingParticipant.Status.ACCEPTED);
    }

    @Test
    void testReplyInvitation_Rejected() {
        PlatformNotification notification = new PlatformNotification();
        notification.setMeetingId("m1");
        notification.setRecipientId("u1");
        when(notificationRepository.getNotificationById("n2")).thenReturn(notification);

        service.replyInvitation("n2", PlatformNotification.Status.REJECTED);

        verify(notificationRepository).replyInvitation("n2", PlatformNotification.Status.REJECTED);
        verify(meetingParticipantRepository).updateStatus("m1", "u1", MeetingParticipant.Status.DECLINED);
    }

    @Test
    void testReplyInvitation_NullStatus() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.replyInvitation("n3", null);
        });
        assertEquals("Status cannot be null", exception.getMessage());
    }

    @Test
    void testReplyInvitation_NotificationNull() {
        when(notificationRepository.getNotificationById("n4")).thenReturn(null);
        service.replyInvitation("n4", PlatformNotification.Status.ACCEPTED);
        verify(meetingParticipantRepository, never()).updateStatus(anyString(), anyString(), any());
    }
}
