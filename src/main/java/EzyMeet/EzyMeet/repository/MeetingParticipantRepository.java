package EzyMeet.EzyMeet.repository;

import EzyMeet.EzyMeet.model.MeetingParticipant;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MeetingParticipantRepository {
    @Autowired
    private Firestore firestore;
    private static final String COLLECTION_NAME = "meetingParticipants";

    public MeetingParticipant create(MeetingParticipant meetingParticipant) {
        CollectionReference meetingParticipants = firestore.collection(COLLECTION_NAME);
        try {
            ApiFuture<DocumentReference> future = meetingParticipants.add(meetingParticipant);
            return meetingParticipant;
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to save meeting participant to Firestore", e);
        }
    }

    public List<MeetingParticipant> findByUserId(String userId) {
        try {
            return firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("userId", userId)
                    .get()
                    .get()
                    .getDocuments()
                    .stream()
                    .map(doc -> doc.toObject(MeetingParticipant.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to query meeting participants", e);
        }
    }

}
