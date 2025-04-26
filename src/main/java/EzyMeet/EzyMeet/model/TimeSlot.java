package EzyMeet.EzyMeet.model;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TimeSlot {

    private Date startDate;
    private Date endDate;

    public TimeSlot(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}