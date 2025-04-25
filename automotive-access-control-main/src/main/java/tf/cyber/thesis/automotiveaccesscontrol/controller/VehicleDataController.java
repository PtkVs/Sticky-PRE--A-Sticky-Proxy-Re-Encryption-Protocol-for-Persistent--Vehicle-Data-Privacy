package tf.cyber.thesis.automotiveaccesscontrol.controller;

import jssc.SerialPortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tf.cyber.obdii.commands.connection.SupportedPID0to20;
import tf.cyber.obdii.commands.connection.SupportedPID21to40;
import tf.cyber.obdii.commands.connection.SupportedPID41to60;
import tf.cyber.obdii.commands.engine.*;
import tf.cyber.obdii.commands.environment.AmbientAirTemperature;
import tf.cyber.obdii.commands.fuel.*;
import tf.cyber.obdii.commands.vehicle.*;
import tf.cyber.obdii.io.OBD2Connection;
import tf.cyber.obdii.util.Pair;
import tf.cyber.thesis.automotiveaccesscontrol.pep.annotation.XACMLAccessControl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vehicle")
@XACMLAccessControl
public class VehicleDataController {

    @Autowired
    public OBD2Connection obd2Connection;

    @RequestMapping("/monitor")
    public Map<String, Map<String, String>> getMonitorStatusSinceDTCCleared(String parameter) throws SerialPortException, InterruptedException {
        MonitorStatusSinceDTCCleared cmd = new MonitorStatusSinceDTCCleared();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/fuelStatus")
    public Map<String, FuelSystemStatus.FuelSystemStatusType> getFuelStatus() throws SerialPortException, InterruptedException {
        FuelSystemStatus cmd = new FuelSystemStatus();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/engineLoad")
    public Map<String, Double> getEngineLoad() throws SerialPortException, InterruptedException {
        CalculatedEngineLoad cmd = new CalculatedEngineLoad();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/engineCoolantTemperature")
    public Map<String, Integer> getEngineCoolantTemperature() throws SerialPortException, InterruptedException {
        EngineCoolantTemperature cmd = new EngineCoolantTemperature();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/shortTermFuelTrimBank/1")
    public Map<String, Double> getShortTermFuelTrimBank1() throws SerialPortException, InterruptedException {
        ShortTermFuelTrimBank1 cmd = new ShortTermFuelTrimBank1();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/longTermFuelTrimBank/1")
    public Map<String, Double> getLongTermFuelTrimBank1() throws SerialPortException, InterruptedException {
        LongTermFuelTrimBank1 cmd = new LongTermFuelTrimBank1();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/intakeManifoldAbsolutePressure")
    public Map<String, Integer> getIntakeManifoldAbsolutePressure() throws SerialPortException, InterruptedException {
        IntakeManifoldAbsolutePressure cmd = new IntakeManifoldAbsolutePressure();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/engineSpeed")
    public Map<String, Double> getEngineSpeed() throws SerialPortException, InterruptedException {
        EngineSpeed cmd = new EngineSpeed();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/vehicleSpeed")
    public Map<String, Integer> getVehicleSpeed() throws SerialPortException, InterruptedException {
        VehicleSpeed cmd = new VehicleSpeed();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/timingAdvance")
    public Map<String, Double> getTimingAdvance() throws SerialPortException, InterruptedException {
        TimingAdvance cmd = new TimingAdvance();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/intakeAirTemperature")
    public Map<String, Integer> getIntakeAirTemperature() throws SerialPortException, InterruptedException {
        IntakeAirTemperature cmd = new IntakeAirTemperature();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/throttlePosition")
    public Map<String, Double> getThrottlePosition() throws SerialPortException, InterruptedException {
        ThrottlePosition cmd = new ThrottlePosition();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/oxygenSensorsPresent")
    public Map<String, Boolean[][]> getOxygenSensorsPresent() throws SerialPortException, InterruptedException {
        OxygenSensorsPresent cmd = new OxygenSensorsPresent();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/oxygenSensors/1")
    public Map<String, Pair<Double, Double>> getOxygenSensor1() throws SerialPortException, InterruptedException {
        OxygenSensor1 cmd = new OxygenSensor1();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/oxygenSensors/2")
    public Map<String, Pair<Double, Double>> getOxygenSensor2() throws SerialPortException, InterruptedException {
        OxygenSensor2 cmd = new OxygenSensor2();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/obdStandardCompliance")
    public Map<String, OBDStandardCompliance.OBDStandardComplianceValue> getOBDStandardCompliance() throws SerialPortException, InterruptedException {
        OBDStandardCompliance cmd = new OBDStandardCompliance();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/runtimeSinceEngineStart")
    public Map<String, Integer> getRuntimeSinceEngineStart() throws SerialPortException, InterruptedException {
        RuntimeSinceEngineStart cmd = new RuntimeSinceEngineStart();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/distanceTraveledWithMalfunctionIndicatorLamp")
    public Map<String, Integer> getDistanceTraveledWithMalfunctionIndicatorLamp() throws SerialPortException, InterruptedException {
        DistanceTraveledWithMalfunctionIndicatorLamp cmd = new DistanceTraveledWithMalfunctionIndicatorLamp();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/commandedExhaustGasRecirculation")
    public Map<String, Double> getCommandedExhaustGasRecirculation() throws SerialPortException, InterruptedException {
        CommandedExhaustGasRecirculation cmd = new CommandedExhaustGasRecirculation();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/commandedEvaporativePurge")
    public Map<String, Double> getCommandedEvaporativePurge() throws SerialPortException, InterruptedException {
        CommandedEvaporativePurge cmd = new CommandedEvaporativePurge();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/warmUpsSinceCodesCleared")
    public Map<String, Integer> getWarmUpsSinceCodesCleared() throws SerialPortException, InterruptedException {
        WarmUpsSinceCodesCleared cmd = new WarmUpsSinceCodesCleared();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/distanceTraveledSinceCodesCleared")
    public Map<String, Integer> getDistanceTraveledSinceCodesCleared() throws SerialPortException, InterruptedException {
        DistanceTraveledSinceCodesCleared cmd = new DistanceTraveledSinceCodesCleared();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/absoluteBarometricPressure")
    public Map<String, Integer> getAbsoluteBarometricPressure() throws SerialPortException, InterruptedException {
        AbsoluteBarometricPressure cmd = new AbsoluteBarometricPressure();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/catalystTemperature/bank/1/sensor/1")
    public Map<String, Double> getCatalystTemperatureBank1Sensor1() throws SerialPortException, InterruptedException {
        CatalystTemperatureBank1Sensor1 cmd = new CatalystTemperatureBank1Sensor1();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/catalystTemperature/bank/1/sensor/2")
    public Map<String, Double> getCatalystTemperatureBank1Sensor2() throws SerialPortException, InterruptedException {
        CatalystTemperatureBank1Sensor2 cmd = new CatalystTemperatureBank1Sensor2();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/controlModuleVoltage")
    public Map<String, Double> getControlModuleVoltage() throws SerialPortException, InterruptedException {
        ControlModuleVoltage cmd = new ControlModuleVoltage();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/absoluteLoadValue")
    public Map<String, Double> getAbsoluteLoadValue() throws SerialPortException, InterruptedException {
        AbsoluteLoadValue cmd = new AbsoluteLoadValue();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/commandedAirFuelEquivalenceRatio")
    public Map<String, Double> getCommandedAirFuelEquivalenceRatio() throws SerialPortException, InterruptedException {
        CommandedAirFuelEquivalenceRatio cmd = new CommandedAirFuelEquivalenceRatio();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/relativeThrottlePosition")
    public Map<String, Double> getRelativeThrottlePosition() throws SerialPortException, InterruptedException {
        RelativeThrottlePosition cmd = new RelativeThrottlePosition();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/ambientAirTemperature")
    public Map<String, Integer> getAmbientAirTemperature() throws SerialPortException, InterruptedException {
        AmbientAirTemperature cmd = new AmbientAirTemperature();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/absoluteThrottlePosition/b")
    public Map<String, Double> getAbsoluteThrottlePositionB() throws SerialPortException, InterruptedException {
        AbsoluteThrottlePositionB cmd = new AbsoluteThrottlePositionB();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/acceleratorPedalPosition/d")
    public Map<String, Double> getAcceleratorPedalPositionD() throws SerialPortException, InterruptedException {
        AcceleratorPedalPositionD cmd = new AcceleratorPedalPositionD();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/acceleratorPedalPosition/e")
    public Map<String, Double> getAcceleratorPedalPositionE() throws SerialPortException, InterruptedException {
        AcceleratorPedalPositionE cmd = new AcceleratorPedalPositionE();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/commandedThrottleActuator")
    public Map<String, Double> getCommandedThrottleActuator() throws SerialPortException, InterruptedException {
        CommandedThrottleActuator cmd = new CommandedThrottleActuator();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/timeRunWithMILOn")
    public Map<String, Integer> getTimeRunWithMILOn() throws SerialPortException, InterruptedException {
        TimeRunWithMILOn cmd = new TimeRunWithMILOn();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/timeSinceTroubleCodesCleared")
    public Map<String, Integer> getTimeSinceTroubleCodesCleared() throws SerialPortException, InterruptedException {
        TimeSinceTroubleCodesCleared cmd = new TimeSinceTroubleCodesCleared();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/fuelType")
    public Map<String, FuelType.FuelTypeValue> getFuelType() throws SerialPortException, InterruptedException {
        FuelType cmd = new FuelType();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result());
    }

    @RequestMapping("/supportedPid/0")
    public Map<String, List<String>> getSupportedPID0to20() throws SerialPortException, InterruptedException {
        SupportedPID0to20 cmd = new SupportedPID0to20();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result().stream().map(Class::getCanonicalName).collect(Collectors.toList()));
    }

    @RequestMapping("/supportedPid/1")
    public Map<String, List<String>> getSupportedPID21to40() throws SerialPortException, InterruptedException {
        SupportedPID21to40 cmd = new SupportedPID21to40();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result().stream().map(Class::getCanonicalName).collect(Collectors.toList()));
    }

    @RequestMapping("/supportedPid/2")
    public Map<String, List<String>> getSupportedPID41to60() throws SerialPortException, InterruptedException {
        SupportedPID41to60 cmd = new SupportedPID41to60();
        cmd.execute(obd2Connection);
        return Map.of(cmd.getKey(), cmd.result().stream().map(Class::getCanonicalName).collect(Collectors.toList()));
    }
}
