package tf.cyber.thesis.automotiveaccesscontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.automotive.accesscontrol")
public class AutomotiveAccessControlApplication {
	public static void main(String[] args) {
		SpringApplication.run(AutomotiveAccessControlApplication.class, args);
	}
}
