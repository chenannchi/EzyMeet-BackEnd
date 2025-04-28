package EzyMeet.EzyMeet.repository;

import EzyMeet.EzyMeet.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
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
            String generatedId = docRef.getId();
            user.setId(generatedId);
            docRef.set(user, SetOptions.merge());
            return user;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save user to Firestore", e);
        }
    }

    public User update(User user) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(user.getId());
            ApiFuture<WriteResult> future = docRef.set(user, SetOptions.merge());
            future.get();
            return user;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to update user in Firestore", e);
        }
    }

    public List<User> findAll() {
        try {
            List<QueryDocumentSnapshot> documents = firestore.collection(COLLECTION_NAME).get().get().getDocuments();
            List<User> users = new ArrayList<>();
            for (QueryDocumentSnapshot document : documents) {
                users.add(document.toObject(User.class));
            }
            return users;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to fetch users from Firestore", e);
        }
    }


}