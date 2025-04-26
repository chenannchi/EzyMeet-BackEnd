package EzyMeet.EzyMeet.model;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class TimeSlot {
    private UUID id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public TimeSlot() {}

    public TimeSlot(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.startDate = startDateTime;
        this.endDate = endDateTime;
    }
}