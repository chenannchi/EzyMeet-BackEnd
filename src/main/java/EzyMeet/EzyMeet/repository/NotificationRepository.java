package EzyMeet.EzyMeet.repository;

import EzyMeet.EzyMeet.model.PlatformNotification;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class NotificationRepository {
    @Autowired
    private Firestore firestore;
    private static final String COLLECTION_NAME = "platformNotifications";

    public void createNotification(PlatformNotification notification) {
        CollectionReference notifications = firestore.collection(COLLECTION_NAME);
        try {
            ApiFuture<DocumentReference> future = notifications.add(notification);
            DocumentReference docRef = future.get();
            notification.setId(docRef.getId());
            docRef.set(notification, SetOptions.merge());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save notification to Firestore", e);
        }
    }

    public List<PlatformNotification> getAllNotificationByUserId(String userId) {
        List<PlatformNotification> notifications = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("recipientId", userId)
                    .get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                notifications.add(document.toObject(PlatformNotification.class));
            }
            return notifications;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to fetch notifications for user: " + userId, e);
        }
    }

    public PlatformNotification getNotificationById(String notificationId) {
        if (notificationId == null) {
            throw new IllegalArgumentException("Notification ID must not be null");
        }
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(notificationId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                return document.toObject(PlatformNotification.class);
            } else {
                return new PlatformNotification();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to fetch notification by ID: " + notificationId, e);
        }
    }

    public void replyInvitation(String notificationId, PlatformNotification.Status status) {
        if (notificationId == null || status == null) {
            throw new IllegalArgumentException("Notification ID and status must not be null");
        }
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(notificationId);
            ApiFuture<WriteResult> future = docRef.update("status", status);
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to reply to invitation for notification: " + notificationId, e);
        }
    }

}
