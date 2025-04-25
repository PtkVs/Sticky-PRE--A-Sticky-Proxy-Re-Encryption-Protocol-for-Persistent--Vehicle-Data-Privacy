package tf.cyber.gps.microservice.gpsmicroservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import tf.cyber.vk162.io.VK162Connection;

@Configuration
public class GPSDataSource {
    @Bean
    @Profile("production")
    public VK162Connection getGpsConnection() {
        VK162Connection.init("/dev/ttyACM0");
        return VK162Connection.getConnection();
    }
}
