package tf.cyber.gps.microservice.gpsmicroservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tf.cyber.gps.microservice.gpsmicroservice.service.GPSDataService;
import tf.cyber.vk162.data.GPSData;

@RestController
public class GPSDataController {
    @Autowired
    private GPSDataService gpsDataService;

    @RequestMapping("/")
    public GPSData getGpsData() {
        return gpsDataService.getGpsData();
    }
}
