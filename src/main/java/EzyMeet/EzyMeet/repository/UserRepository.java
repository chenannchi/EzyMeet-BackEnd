package EzyMeet.EzyMeet.repository;

import EzyMeet.EzyMeet.model.User;
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
public class UserRepository {
    @Autowired
    private Firestore firestore;
    private static final String COLLECTION_NAME = "users";

    public User create(User user) {
        CollectionReference users = firestore.collection(COLLECTION_NAME);
        try {
            ApiFuture<DocumentReference> future = users.add(user);
            DocumentReference docRef = future.get();
            return user;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save user to Firestore", e);
        }
    }
}