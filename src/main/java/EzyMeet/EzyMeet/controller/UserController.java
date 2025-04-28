package EzyMeet.EzyMeet.controller;

import EzyMeet.EzyMeet.dto.UserSyncDTO;
import EzyMeet.EzyMeet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import EzyMeet.EzyMeet.model.User;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.create(user);
        return ResponseEntity.ok(createdUser);
    }



    @PostMapping("/update/{userId}")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        User updatedUser = userService.update(user);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }


//    @PostMapping("/sync")
//    public ResponseEntity<User> syncGoogleUser(@RequestBody User googleUser) {
//        User syncedUser = userService.syncGoogleUser(googleUser);
//        return ResponseEntity.ok(syncedUser);
//    }
//
//    @GetMapping("/all-users-email-id")
//    public ResponseEntity<List<Map<String, String>>> getAllUsersEmailAndId() {
//        List<Map<String, String>> emailsAndIds = userService.getAllUsersEmailAndId();
//        return ResponseEntity.ok(emailsAndIds);
//    }

}