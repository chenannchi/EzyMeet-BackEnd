package EzyMeet.EzyMeet.repository;

import EzyMeet.EzyMeet.model.Meeting;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


@Repository
public class MeetingRepository {
    @Autowired
    private Firestore firestore;
    private static final String COLLECTION_NAME = "meetings";

    public Meeting create(Meeting meeting) {
        CollectionReference meetings = firestore.collection(COLLECTION_NAME);
        try {
            ApiFuture<DocumentReference> future = meetings.add(meeting);
            DocumentReference docRef = future.get();
            meeting.setId(docRef.getId());
            return meeting;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save meeting to Firestore", e);
        }
    }

    public List<Meeting> findMeetingById(List<String> meetingIds) {
        List<Meeting> meetings = new ArrayList<>();
        try {
            for (String id : meetingIds) {
                DocumentSnapshot doc = firestore.collection(COLLECTION_NAME).document(id).get().get();
                if (doc.exists()) {
                    Meeting meeting = doc.toObject(Meeting.class);
                    meetings.add(meeting);
                }
            }
            return meetings;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch meetings by IDs", e);
        }
    }

    public Meeting edit(Meeting meeting) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(meeting.getId());
            docRef.set(meeting);
            return meeting;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update meeting in Firestore", e);
        }
    }
}
