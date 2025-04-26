package EzyMeet.EzyMeet.model;

import jakarta.persistence.Id;
import lombok.Getter;

import java.util.UUID;

@Getter
public class User{
    private String id;
    private String googleId;
    private String userName;
    private String email;

    public User(String id, String googleId, String name, String email) {
        this.id = id;
        this.googleId = googleId;
        this.userName = name;
        this.email = email;
    }
}