package tf.cyber.gps.microservice.gpsmicroservice.service;

import org.springframework.stereotype.Service;
import tf.cyber.vk162.data.GPSData;

@Service
public interface GPSDataService {
     GPSData getGpsData();
}
