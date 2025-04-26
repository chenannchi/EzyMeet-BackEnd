package EzyMeet.EzyMeet.model;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Meeting {
    private String title;
    private String label;
    private TimeSlot timeslot;
    private String location;
    private String link;
    private MeetingParticipant[] invitees;
    private String description;
}
