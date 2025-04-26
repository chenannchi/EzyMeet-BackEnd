package EzyMeet.EzyMeet.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class Meeting {
    private Long id;
    private String title;
    private String label;
    private TimeSlot timeslot;
    private String location;
    private String link;
    private String[] invitees;
    private String description;
}
