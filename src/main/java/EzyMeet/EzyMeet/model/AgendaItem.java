package EzyMeet.EzyMeet.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
//@AllArgsConstructor
@NoArgsConstructor
public class AgendaItem {
    private String id;
    private String startTime;
    private String endTime;
    private String topic;
//    private String owner;


    public AgendaItem(String startTime, String endTime, String topic) {
        this.startTime = startTime;
        this.endTime = endTime;
//        this.owner = owner;
        this.topic = topic;
    }
}
