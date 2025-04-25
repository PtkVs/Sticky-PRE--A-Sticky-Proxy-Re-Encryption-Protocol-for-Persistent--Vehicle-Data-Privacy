package tf.cyber.gps.microservice.gpsmicroservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tf.cyber.gps.microservice.gpsmicroservice.config.GPSDataSource;
import tf.cyber.vk162.data.GPSData;

@Service
@Profile("production")
public class RealGPSDataService implements GPSDataService {
    @Autowired
    private GPSDataSource gpsDataSource;

    @Override
    public GPSData getGpsData() {
        return gpsDataSource.getGpsConnection().getGpsData();
    }
}
