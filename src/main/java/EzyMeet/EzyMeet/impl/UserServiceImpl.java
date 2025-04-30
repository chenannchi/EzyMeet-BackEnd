package EzyMeet.EzyMeet.impl;

import EzyMeet.EzyMeet.model.User;
import EzyMeet.EzyMeet.service.UserService;

import EzyMeet.EzyMeet.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        return userRepository.create(user);
    }

    @Override
    public User update(User user) {
        return userRepository.update(user);
    }

    @Override
    public String getUserIdByGoogleId(String googleId) {
        return userRepository.findAll().stream()
                .filter(user -> user.getGoogleUid().equals(googleId))
                .map(User::getId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public User syncGoogleUser(User googleUser) {
        String userId = getUserIdByGoogleId(googleUser.getGoogleUid());
        if (userId != null) {
            googleUser.setId(userId);
            return update(googleUser);
        } else {
            return create(googleUser);
        }
    }

    @Override
    public List<Map<String, String>> getAllParticipantOptions(String loginUserId)  {
        return userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(loginUserId))
                .map(user -> Map.of("id", user.getId(), "email", user.getEmail(), "displayName", user.getDisplayName()))
                .collect(Collectors.toList());
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findAll().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}
