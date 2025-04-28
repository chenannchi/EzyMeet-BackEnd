package EzyMeet.EzyMeet.service;

import EzyMeet.EzyMeet.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    User create(User user);

    String getUserIdByGoogleId(String googleId);

    User syncGoogleUser(User googleUser);

    User update(User user);

    User getUserById(String userId);

    List<Map<String, String>> getAllParticipantOptions();
}