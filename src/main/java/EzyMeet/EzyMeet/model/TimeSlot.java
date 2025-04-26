package EzyMeet.EzyMeet.model;
import com.google.cloud.Timestamp;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;
import java.util.Date;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TimeSlot {
    private UUID id;
    private Date startDate;
    private Date endDate;


    public TimeSlot(Date startDateTime, Date endDateTime) {
        this.startDate = startDateTime;
        this.endDate = endDateTime;
    }
}