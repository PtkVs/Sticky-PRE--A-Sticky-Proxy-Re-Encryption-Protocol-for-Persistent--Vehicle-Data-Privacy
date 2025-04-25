package tf.cyber.gps.microservice.gpsmicroservice.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tf.cyber.vk162.data.GPSData;

import java.math.BigDecimal;

@Service
@Profile("debug")
public class DebugGPSDataService implements GPSDataService {
    @Override
    public GPSData getGpsData() {
        return GPSData.builder()
                .header("Speed")
                .timestamp(5d)
                .build();
                /*
                .header("DEBUG")
                .antennaAltitudeUnit(GPSData.AltitudeUnit.METER)
                .antennaAltitude(400d)
                .usedSatellites(5)
                .quality(GPSData.GPSQuality.UNCORRECTED_COORDINATE)
                .longitude(BigDecimal.valueOf(12.153647070181622d))
                .latitude(BigDecimal.valueOf(47.87224906970429d))
                .timestamp(0d)
                .build(); */
    }
}
