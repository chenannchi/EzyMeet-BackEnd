package EzyMeet.EzyMeet.repository;

import EzyMeet.EzyMeet.model.Meeting;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MeetingRepository {
    private final List<Meeting> meetings = new ArrayList<>();

    public Meeting save(Meeting meeting) {
        meetings.add(meeting);
        return meeting;
    }

    public Optional<Meeting> findById(Long id) {
        return meetings.stream().filter(m -> m.getId().equals(id)).findFirst();
    }

    public List<Meeting> findAll() {
        return meetings;
    }

    public void delete(Long id) {
        meetings.removeIf(meeting -> meeting.getId().equals(id));
    }
}
