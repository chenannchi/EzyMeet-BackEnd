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
    private String emailVerified;
    private String providerId;
    private String metaData;
    private String idToken;

    @Setter
    private String id;

    @Setter
    private String displayName;

    @Setter
    private String photoUrl;

    @Setter
    private String phoneNumber;


}