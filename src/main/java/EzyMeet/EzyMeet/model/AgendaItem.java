package EzyMeet.EzyMeet.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
//@AllArgsConstructor
public class AgendaItem {
    private UUID id;
    private LocalTime startTime;
    private LocalTime endTime;
    private String topic;
    private String owner;


    public AgendaItem(LocalTime startTime, LocalTime endTime, String topic, String owner) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.owner = owner;
        this.topic = topic;
    }
}
