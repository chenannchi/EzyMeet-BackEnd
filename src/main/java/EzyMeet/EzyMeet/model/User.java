package EzyMeet.EzyMeet.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class User {
    private String id;
    private String googleUid;
    private String email;
    private String emailVerified;
    private String providerId;
    private String metaData;
    private String idToken;

    @Setter
    private String displayName;

    @Setter
    private String photoUrl;

    @Setter
    private String phoneNumber;
}