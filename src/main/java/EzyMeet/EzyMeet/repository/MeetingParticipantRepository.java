package EzyMeet.EzyMeet.repository;

import EzyMeet.EzyMeet.model.MeetingParticipant;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
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
            DocumentReference docRef = future.get();
            String generatedId = docRef.getId();
            meetingParticipant.setId(generatedId);

            docRef.set(meetingParticipant, SetOptions.merge());
            return meetingParticipant;
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to save meeting participant to Firestore", e);
        }
    }

    //public void deleteParti

    public void deleteByMeetingId(String meetingId) {
        try {
            List<MeetingParticipant> participants = findByMeetingId(meetingId);
            for (MeetingParticipant participant : participants) {
                DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(participant.getId());
                docRef.delete();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete meeting participants", e);
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

    public List<MeetingParticipant> findByMeetingId(String meetingId) {
        try {
            return firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("meetingId", meetingId)
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
