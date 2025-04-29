package EzyMeet.EzyMeet.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String googleUid;
    private String email;
    private boolean emailVerified;
    private String providerId;

    @Setter
    private String idToken;

    @Setter
    private String createdAt;

    @Setter
    private String lastSignInTime;

    @Setter
    private String id;

    @Setter
    private String displayName;

    @Setter
    private String photoURL;

    @Setter
    private String phoneNumber;
}