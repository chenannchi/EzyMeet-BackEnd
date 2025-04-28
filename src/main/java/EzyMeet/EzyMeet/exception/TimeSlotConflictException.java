package EzyMeet.EzyMeet.exception;

import EzyMeet.EzyMeet.model.Meeting;
import lombok.Getter;

import java.util.List;

@Getter
public class TimeSlotConflictException extends RuntimeException {
    private final List<Meeting> conflictingMeetings;

    public TimeSlotConflictException(String message) {
        super(message);
        this.conflictingMeetings = null;
    }
}