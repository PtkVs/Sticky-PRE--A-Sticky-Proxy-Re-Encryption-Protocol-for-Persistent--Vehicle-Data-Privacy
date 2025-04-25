package cyber.tf.accesslogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class AccessLogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccessLogServiceApplication.class, args);
	}

}
