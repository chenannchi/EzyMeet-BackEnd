package EzyMeet.EzyMeet.model;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Meeting {
    private String id;
    private String title;
    private String label;
    private TimeSlot timeslot;
    private String location;
    private String link;
    private String description;
    private String host;
    private String meetingRecord;
}
