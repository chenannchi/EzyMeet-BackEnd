package EzyMeet.EzyMeet.repository;

import EzyMeet.EzyMeet.model.Meeting;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


@Repository
public class MeetingRepository {
    @Autowired
    private Firestore firestore;
    private static final String COLLECTION_NAME = "meetings";

    public Meeting save(Meeting meeting) {
        CollectionReference meetings = firestore.collection(COLLECTION_NAME);
        try {
            ApiFuture<DocumentReference> future = meetings.add(meeting);
            DocumentReference docRef = future.get();
            return meeting;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save meeting to Firestore", e);
        }
    }

    public Optional<Meeting> findById(String id) {
//        return meetings.stream().filter(m -> m.getId().equals(id)).findFirst();
        return Optional.empty();
    }

    public List<Meeting> findAll() {
        return new ArrayList<>();  // Stub implementation
    }

    public void deleteById(String id) {
        // Empty stub implementation
    }
}
