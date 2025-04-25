package tf.cyber.obdii.commands.connection;

import tf.cyber.obdii.commands.OBD2Command;
import tf.cyber.obdii.commands.engine.*;
import tf.cyber.obdii.commands.environment.AmbientAirTemperature;
import tf.cyber.obdii.commands.fuel.*;
import tf.cyber.obdii.commands.vehicle.*;
import tf.cyber.obdii.util.ByteUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SupportedPID41to60 extends OBD2Command<List<Class<OBD2Command<?>>>> {
    private static final Class<OBD2Command<?>>[][] PID_INDEX = new Class[][]{
            {MonitorStatusDriveCycle.class, ControlModuleVoltage.class, AbsoluteLoadValue.class, CommandedAirFuelEquivalenceRatio.class},
            {RelativeThrottlePosition.class, AmbientAirTemperature.class, AbsoluteThrottlePositionB.class, AbsoluteThrottlePositionC.class},
            {AcceleratorPedalPositionD.class, AcceleratorPedalPositionE.class, AcceleratorPedalPositionF.class, CommandedThrottleActuator.class},
            {TimeRunWithMILOn.class, TimeSinceTroubleCodesCleared.class, MaximumValueSet1.class, MaximumValueAirFlow.class},
            {FuelType.class, EthanolFuelLevel.class, AbsoluteEvaporationSystemVaporPressure.class, EvaporationSystemVaporPressure2.class},
            {ShortTermSecondaryOxygenSensorTrimBank1Bank3.class, LongTermSecondaryOxygenSensorTrimBank1Bank3.class, ShortTermSecondaryOxygenSensorTrimBank2Bank4.class, LongTermSecondaryOxygenSensorTrimBank2Bank4.class},
            {FuelRailAbsolutePressure.class, RelativeAcceleratorPedalPosition.class, HybridBatteryPackRemainingLife.class, EngineOilTemperature.class},
            {FuelInjectionTiming.class, EngineFuelRate.class, EmissionRequirements.class, null},
    };

    @Override
    public String command() {
        return "01 40";
    }

    @Override
    public List<Class<OBD2Command<?>>> result() {
        List<Class<OBD2Command<?>>> supportedCommands = new LinkedList<>();

        String[] bytesStr = rawData.split(" ");
        String str = bytesStr[bytesStr.length - 4] + bytesStr[bytesStr.length - 3]
                + bytesStr[bytesStr.length - 2] + bytesStr[bytesStr.length - 1];

        String[] chars = str.split("");

        for (int c = 0; c < chars.length; c++) {
            int b = Integer.parseInt(chars[c], 16);

            List<Boolean> mask = Arrays.asList(ByteUtils.byteToBooleanFourBits(b));
            Collections.reverse(mask);

            for (int i = 0; i < mask.size(); i++) {
                if (mask.get(i) && PID_INDEX[c][i] != null) { // do not add the last possible command as not implemented
                    supportedCommands.add(PID_INDEX[c][i]);
                }
            }
        }

        return supportedCommands;
    }

    @Override
    public String getFriendlyName() {
        return "Fetch Supported PIDs (41 to 60)";
    }

    @Override
    public String getKey() {
        return "pid_support_41_60";
    }
}
