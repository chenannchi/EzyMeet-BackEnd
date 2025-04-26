package EzyMeet.EzyMeet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class EzyMeetApplication {

	public static void main(String[] args) {
		SpringApplication.run(EzyMeetApplication.class, args);
	}

}
