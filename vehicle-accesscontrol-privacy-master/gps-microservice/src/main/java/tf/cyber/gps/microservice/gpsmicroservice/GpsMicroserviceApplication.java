package tf.cyber.gps.microservice.gpsmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tf.cyber.vk162.io.VK162Connection;

@SpringBootApplication
public class GpsMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GpsMicroserviceApplication.class, args);
	}

}
