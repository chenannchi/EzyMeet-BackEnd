package EzyMeet.EzyMeet.repository;

import EzyMeet.EzyMeet.model.Meeting;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


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
            docRef.set(meeting, SetOptions.merge());
            return meeting;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save meeting to Firestore", e);
        }
    }

    public List<Meeting> findMeetingsById(List<String> meetingIds) {
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

    public Meeting findSingleMeetingById(String meetingId) {
        try {
            DocumentSnapshot doc = firestore.collection(COLLECTION_NAME).document(meetingId).get().get();
            if (doc.exists()) {
                return doc.toObject(Meeting.class);
            } else {
                throw new NoSuchElementException("Meeting not found: " + meetingId);
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to fetch meeting by ID", e);
        }
    }

    public Meeting update(String meetingId, Meeting meeting) {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(meetingId);
        try {
            DocumentSnapshot snapshot = docRef.get().get();
            if (!snapshot.exists()) {
                throw new NoSuchElementException("Meeting not found: " + meetingId);
            }
            docRef.set(meeting);
            return meeting;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to update meeting in Firestore", e);
        }
    }

    public Meeting delete(String meetingId) {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(meetingId);
        try {
            DocumentSnapshot snapshot = docRef.get().get();
            if (!snapshot.exists()) {
                throw new NoSuchElementException("Meeting not found: " + meetingId);
            }
            Meeting meeting = snapshot.toObject(Meeting.class);
            docRef.delete().get();
            return meeting;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to delete meeting from Firestore", e);
        }
    }

    public List<Meeting> findMeetingsByHost(String hostId) {
        try {
            return firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("host", hostId)
                    .get()
                    .get()
                    .getDocuments()
                    .stream()
                    .map(doc -> doc.toObject(Meeting.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch meetings by host", e);
        }
    }
}
