package tf.cyber.thesis.automotiveaccesscontrol.controller;

import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.commands.engine.CalculatedEngineLoad;
import tf.cyber.obdii.commands.engine.EngineOilTemperature;
import tf.cyber.obdii.commands.engine.EngineSpeed;
import tf.cyber.obdii.commands.engine.ThrottlePosition;
import tf.cyber.obdii.commands.environment.AmbientAirTemperature;
import tf.cyber.obdii.commands.vehicle.RuntimeSinceEngineStart;
import tf.cyber.obdii.commands.vehicle.VehicleSpeed;
import tf.cyber.obdii.io.OBD2Connection;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class BundledVehicleDataController {
    @Autowired
    OBD2Connection obd2Connection;

    private final Logger vehicleTestLogger = LoggerFactory.getLogger("VehicleTestController");

    private final List<OBD2Command<?>> basicCommands = List.of(
            new VehicleSpeed(),
            new EngineSpeed(),
            new AmbientAirTemperature(),
            new CalculatedEngineLoad(),
            new EngineOilTemperature(),
            new ThrottlePosition(),
            new RuntimeSinceEngineStart()
    );

    @RequestMapping("/info")
    public Map<String, Object> getBasicVehicleInfo() {
        Map<String, Object> res = new HashMap<>();

        basicCommands.forEach(obd2Command -> {
            try {
                vehicleTestLogger.info("Running command: " + obd2Command.getClass().getName());

                long nanoStart = System.nanoTime();
                obd2Command.execute(obd2Connection);
                long nanoEnd = System.nanoTime();

                long diffMS = (nanoEnd - nanoStart) / 1000 / 1000;

                vehicleTestLogger.info("Running command took " + diffMS + "ms!");

                res.put(obd2Command.getKey(), obd2Command.result());
            } catch (SerialPortException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        res.put("time", Instant.now());

        return res;
    }
}
