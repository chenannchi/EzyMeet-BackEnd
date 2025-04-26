package EzyMeet.EzyMeet.model;

import jakarta.persistence.Id;
import lombok.Getter;

import java.util.UUID;

@Getter
public class User{
    private UUID id;
    private UUID googleId;
    private String userName;
    private String email;

    public User(UUID id, String name, String email) {
        this.id = id;
        this.userName = name;
        this.email = email;
    }
}